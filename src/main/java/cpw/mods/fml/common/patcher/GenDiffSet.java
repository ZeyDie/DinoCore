package cpw.mods.fml.common.patcher;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.repackage.com.nothome.delta.Delta;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenDiffSet {

    private static final List<String> RESERVED_NAMES = Arrays.asList("CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9");

    public static void main(final String[] args) throws IOException
    {
        final String sourceJar = args[0]; //Clean Vanilla jar minecraft.jar or minecraft_server.jar
        final String targetDir = args[1]; //Directory containing obfed output classes, typically mcp/reobf/minecraft
        final String deobfData = args[2]; //Path to FML's deobfusication_data.lzma
        final String outputDir = args[3]; //Path to place generated .binpatch
        final String killTarget = args[4]; //"true" if we should destroy the target file if it generated a successful .binpatch

        Logger.getLogger("GENDIFF").log(Level.INFO, String.format("Creating patches at %s for %s from %s", outputDir, sourceJar, targetDir));
        final Delta delta = new Delta();
        final FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
        remapper.setupLoadOnly(deobfData, false);
        final JarFile sourceZip = new JarFile(sourceJar);
        final boolean kill = killTarget.equalsIgnoreCase("true");

        final File f = new File(outputDir);
        f.mkdirs();

        for (final String name : remapper.getObfedClasses())
        {
//            Logger.getLogger("GENDIFF").info(String.format("Evaluating path for data :%s",name));
            String fileName = name;
            String jarName = name;
            if (RESERVED_NAMES.contains(name.toUpperCase(Locale.ENGLISH)))
            {
                fileName = "_"+name;
            }
            final File targetFile = new File(targetDir, fileName.replace('/', File.separatorChar) + ".class");
            jarName = jarName+".class";
            if (targetFile.exists())
            {
                final String sourceClassName = name.replace('/', '.');
                final String targetClassName = remapper.map(name).replace('/', '.');
                final JarEntry entry = sourceZip.getJarEntry(jarName);

                final byte[] vanillaBytes = entry != null ? ByteStreams.toByteArray(sourceZip.getInputStream(entry)) : new byte[0];
                final byte[] patchedBytes = Files.toByteArray(targetFile);

                final byte[] diff = delta.compute(vanillaBytes, patchedBytes);


                final ByteArrayDataOutput diffOut = ByteStreams.newDataOutput(diff.length + 50);
                // Original name
                diffOut.writeUTF(name);
                // Source name
                diffOut.writeUTF(sourceClassName);
                // Target name
                diffOut.writeUTF(targetClassName);
                // exists at original
                diffOut.writeBoolean(entry != null);
                if (entry != null)
                {
                    diffOut.writeInt(Hashing.adler32().hashBytes(vanillaBytes).asInt());
                }
                // length of patch
                diffOut.writeInt(diff.length);
                // patch
                diffOut.write(diff);

                final File target = new File(outputDir, targetClassName+".binpatch");
                target.getParentFile().mkdirs();
                Files.write(diffOut.toByteArray(), target);
                Logger.getLogger("GENDIFF").info(String.format("Wrote patch for %s (%s) at %s",name, targetClassName, target.getAbsolutePath()));
                if (kill)
                {
                    targetFile.delete();
                    Logger.getLogger("GENDIFF").info(String.format("  Deleted target: %s", targetFile.toString()));
                }
            }
        }
        sourceZip.close();
    }

}
