/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.asm.transformers;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MarkerTransformer implements IClassTransformer
{
    private ListMultimap<String, String> markers = ArrayListMultimap.create();

    public MarkerTransformer() throws IOException
    {
        this("fml_marker.cfg");
    }
    protected MarkerTransformer(final String rulesFile) throws IOException
    {
        readMapFile(rulesFile);
    }

    private void readMapFile(final String rulesFile) throws IOException
    {
        final File file = new File(rulesFile);
        final URL rulesResource;
        if (file.exists())
        {
            rulesResource = file.toURI().toURL();
        }
        else
        {
            rulesResource = Resources.getResource(rulesFile);
        }
        Resources.readLines(rulesResource, Charsets.UTF_8, new LineProcessor<Void>()
        {
            @Override
            public Void getResult()
            {
                return null;
            }

            @Override
            public boolean processLine(final String input) throws IOException
            {
                final String line = Iterables.getFirst(Splitter.on('#').limit(2).split(input), "").trim();
                if (line.isEmpty())
                {
                    return true;
                }
                final List<String> parts = Lists.newArrayList(Splitter.on(" ").trimResults().split(line));
                if (parts.size()!=2)
                {
                    throw new RuntimeException("Invalid config file line "+ input);
                }
                final List<String> markerInterfaces = Lists.newArrayList(Splitter.on(",").trimResults().split(parts.get(1)));
                for (final String marker : markerInterfaces)
                {
                    markers.put(parts.get(0), marker);
                }
                return true;
            }
        });
    }

    @Override
    public byte[] transform(final String name, final String transformedName, final byte[] bytes)
    {
    	if (bytes == null) { return null; }
        if (!markers.containsKey(name)) { return bytes; }

        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        classNode.interfaces.addAll(markers.get(name));

        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static void main(final String[] args)
    {
        if (args.length < 2)
        {
            System.out.println("Usage: MarkerTransformer <JarPath> <MapFile> [MapFile2]... ");
            return;
        }

        boolean hasTransformer = false;
        final MarkerTransformer[] trans = new MarkerTransformer[args.length - 1];
        for (int x = 1; x < args.length; x++)
        {
            try
            {
                trans[x - 1] = new MarkerTransformer(args[x]);
                hasTransformer = true;
            }
            catch (final IOException e)
            {
                System.out.println("Could not read Transformer Map: " + args[x]);
                e.printStackTrace();
            }
        }

        if (!hasTransformer)
        {
            System.out.println("Culd not find a valid transformer to perform");
            return;
        }

        final File orig = new File(args[0]);
        final File temp = new File(args[0] + ".ATBack");
        if (!orig.exists() && !temp.exists())
        {
            System.out.println("Could not find target jar: " + orig);
            return;
        }
/*
        if (temp.exists())
        {
            if (orig.exists() && !orig.renameTo(new File(args[0] + (new SimpleDateFormat(".yyyy.MM.dd.HHmmss")).format(new Date()))))
            {
                System.out.println("Could not backup existing file: " + orig);
                return;
            }
            if (!temp.renameTo(orig))
            {
                System.out.println("Could not restore backup from previous run: " + temp);
                return;
            }
        }
*/
        if (!orig.renameTo(temp))
        {
            System.out.println("Could not rename file: " + orig + " -> " + temp);
            return;
        }

        try
        {
            processJar(temp, orig, trans);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }

        if (!temp.delete())
        {
            System.out.println("Could not delete temp file: " + temp);
        }
    }

    private static void processJar(final File inFile, final File outFile, final MarkerTransformer[] transformers) throws IOException
    {
        ZipInputStream inJar = null;
        ZipOutputStream outJar = null;

        try
        {
            try
            {
                inJar = new ZipInputStream(new BufferedInputStream(new FileInputStream(inFile)));
            }
            catch (final FileNotFoundException e)
            {
                throw new FileNotFoundException("Could not open input file: " + e.getMessage());
            }

            try
            {
                outJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            }
            catch (final FileNotFoundException e)
            {
                throw new FileNotFoundException("Could not open output file: " + e.getMessage());
            }

            ZipEntry entry;
            while ((entry = inJar.getNextEntry()) != null)
            {
                if (entry.isDirectory())
                {
                    outJar.putNextEntry(entry);
                    continue;
                }

                final byte[] data = new byte[4096];
                final ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();

                int len;
                do
                {
                    len = inJar.read(data);
                    if (len > 0)
                    {
                        entryBuffer.write(data, 0, len);
                    }
                }
                while (len != -1);

                byte[] entryData = entryBuffer.toByteArray();

                final String entryName = entry.getName();

                if (entryName.endsWith(".class") && !entryName.startsWith("."))
                {
                    final ClassNode cls = new ClassNode();
                    final ClassReader rdr = new ClassReader(entryData);
                    rdr.accept(cls, 0);
                    final String name = cls.name.replace('/', '.').replace('\\', '.');

                    for (final MarkerTransformer trans : transformers)
                    {
                        entryData = trans.transform(name, name, entryData);
                    }
                }

                final ZipEntry newEntry = new ZipEntry(entryName);
                outJar.putNextEntry(newEntry);
                outJar.write(entryData);
            }
        }
        finally
        {
            if (outJar != null)
            {
                try
                {
                    outJar.close();
                }
                catch (final IOException e)
                {
                }
            }

            if (inJar != null)
            {
                try
                {
                    inJar.close();
                }
                catch (final IOException e)
                {
                }
            }
        }
    }
}
