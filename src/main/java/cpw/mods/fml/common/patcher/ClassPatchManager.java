package cpw.mods.fml.common.patcher;

import LZMA.LzmaInputStream;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.repackage.com.nothome.delta.GDiffPatcher;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class ClassPatchManager {
    public static final ClassPatchManager INSTANCE = new ClassPatchManager();

    public static final boolean dumpPatched = Boolean.parseBoolean(System.getProperty("fml.dumpPatchedClasses", "false"));

    private GDiffPatcher patcher = new GDiffPatcher();
    private ListMultimap<String, ClassPatch> patches;

    private Map<String,byte[]> patchedClasses = Maps.newHashMap();
    private File tempDir;
    private ClassPatchManager()
    {
        if (dumpPatched)
        {
            tempDir = Files.createTempDir();
            FMLRelaunchLog.info("Dumping patched classes to %s",tempDir.getAbsolutePath());
        }
    }


    public byte[] getPatchedResource(final String name, final String mappedName, final LaunchClassLoader loader) throws IOException
    {
        final byte[] rawClassBytes = loader.getClassBytes(name);
        return applyPatch(name, mappedName, rawClassBytes);
    }
    public byte[] applyPatch(final String name, final String mappedName, byte[] inputData)
    {
        byte[] inputData1 = inputData;
        if (patches == null)
        {
            return inputData1;
        }
        if (patchedClasses.containsKey(name))
        {
            return patchedClasses.get(name);
        }
        final List<ClassPatch> list = patches.get(name);
        if (list.isEmpty())
        {
            return inputData1;
        }
        boolean ignoredError = false;
        FMLRelaunchLog.fine("Runtime patching class %s (input size %d), found %d patch%s", mappedName, (inputData1 == null ? 0 : inputData1.length), list.size(), list.size()!=1 ? "es" : "");
        for (final ClassPatch patch: list)
        {
            if (!patch.targetClassName.equals(mappedName) && !patch.sourceClassName.equals(name))
            {
                FMLRelaunchLog.warning("Binary patch found %s for wrong class %s", patch.targetClassName, mappedName);
            }
            if (!patch.existsAtTarget && (inputData1 == null || inputData1.length == 0))
            {
                inputData1 = new byte[0];
            }
            else if (!patch.existsAtTarget)
            {
                FMLRelaunchLog.warning("Patcher expecting empty class data file for %s, but received non-empty", patch.targetClassName);
            }
            else
            {
                final int inputChecksum = Hashing.adler32().hashBytes(inputData1).asInt();
                if (patch.inputChecksum != inputChecksum)
                {
                    FMLRelaunchLog.severe("There is a binary discrepency between the expected input class %s (%s) and the actual class. Checksum on disk is %x, in patch %x. Things are probably about to go very wrong. Did you put something into the jar file?", mappedName, name, inputChecksum, patch.inputChecksum);
                    if (!Boolean.parseBoolean(System.getProperty("fml.ignorePatchDiscrepancies","false")))
                    {
                        FMLRelaunchLog.severe("The game is going to exit, because this is a critical error, and it is very improbable that the modded game will work, please obtain clean jar files.");
                        System.exit(1);
                    }
                    else
                    {
                        FMLRelaunchLog.severe("FML is going to ignore this error, note that the patch will not be applied, and there is likely to be a malfunctioning behaviour, including not running at all");
                        ignoredError = true;
                        continue;
                    }
                }
            }
            synchronized (patcher)
            {
                try
                {
                    inputData1 = patcher.patch(inputData1, patch.patch);
                }
                catch (final IOException e)
                {
                    FMLRelaunchLog.log(Level.SEVERE, e, "Encountered problem runtime patching class %s", name);
                    continue;
                }
            }
        }
        if (!ignoredError)
        {
            FMLRelaunchLog.fine("Successfully applied runtime patches for %s (new size %d)", mappedName, inputData1.length);
        }
        if (dumpPatched)
        {
            try
            {
                Files.write(inputData1, new File(tempDir,mappedName));
            }
            catch (final IOException e)
            {
                FMLRelaunchLog.log(Level.SEVERE, e, "Failed to write %s to %s", mappedName, tempDir.getAbsolutePath());
            }
        }
        patchedClasses.put(name, inputData1);
        return inputData1;
    }

    public void setup(final Side side)
    {
        final Pattern binpatchMatcher = Pattern.compile(String.format("binpatch/%s/.*.binpatch", side.toString().toLowerCase(Locale.ENGLISH)));
        final JarInputStream jis;
        try
        {
            final InputStream binpatchesCompressed = getClass().getResourceAsStream("/binpatches.pack.lzma");
            if (binpatchesCompressed==null)
            {
                FMLRelaunchLog.log(Level.SEVERE, "The binary patch set is missing. Either you are in a development environment, or things are not going to work!");
                return;
            }
            final LzmaInputStream binpatchesDecompressed = new LzmaInputStream(binpatchesCompressed);
            final ByteArrayOutputStream jarBytes = new ByteArrayOutputStream();
            final JarOutputStream jos = new JarOutputStream(jarBytes);
            Pack200.newUnpacker().unpack(binpatchesDecompressed, jos);
            jis = new JarInputStream(new ByteArrayInputStream(jarBytes.toByteArray()));
        }
        catch (final Exception e)
        {
            FMLRelaunchLog.log(Level.SEVERE, e, "Error occurred reading binary patches. Expect severe problems!");
            throw Throwables.propagate(e);
        }

        patches = ArrayListMultimap.create();

        do
        {
            try
            {
                final JarEntry entry = jis.getNextJarEntry();
                if (entry == null)
                {
                    break;
                }
                if (binpatchMatcher.matcher(entry.getName()).matches())
                {
                    final ClassPatch cp = readPatch(entry, jis);
                    if (cp != null)
                    {
                        patches.put(cp.sourceClassName, cp);
                    }
                }
                else
                {
                    jis.closeEntry();
                }
            }
            catch (final IOException e)
            {
            }
        } while (true);
        FMLRelaunchLog.fine("Read %d binary patches", patches.size());
        FMLRelaunchLog.fine("Patch list :\n\t%s", Joiner.on("\t\n").join(patches.asMap().entrySet()));
        patchedClasses.clear();
    }

    private ClassPatch readPatch(final JarEntry patchEntry, final JarInputStream jis)
    {
        FMLRelaunchLog.finest("Reading patch data from %s", patchEntry.getName());
        final ByteArrayDataInput input;
        try
        {
            input = ByteStreams.newDataInput(ByteStreams.toByteArray(jis));
        }
        catch (final IOException e)
        {
            FMLRelaunchLog.log(Level.WARNING, e, "Unable to read binpatch file %s - ignoring", patchEntry.getName());
            return null;
        }
        final String name = input.readUTF();
        final String sourceClassName = input.readUTF();
        final String targetClassName = input.readUTF();
        final boolean exists = input.readBoolean();
        int inputChecksum = 0;
        if (exists)
        {
            inputChecksum = input.readInt();
        }
        final int patchLength = input.readInt();
        final byte[] patchBytes = new byte[patchLength];
        input.readFully(patchBytes);

        return new ClassPatch(name, sourceClassName, targetClassName, exists, inputChecksum, patchBytes);
    }
}
