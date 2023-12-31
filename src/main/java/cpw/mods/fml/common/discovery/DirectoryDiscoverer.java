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

package cpw.mods.fml.common.discovery;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.discovery.asm.ASMModParser;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;

public class DirectoryDiscoverer implements ITypeDiscoverer
{
    private class ClassFilter implements FileFilter
    {
        @Override
        public boolean accept(final File file)
        {
            return (file.isFile() && classFile.matcher(file.getName()).find()) || file.isDirectory();
        }
    }

    private ASMDataTable table;

    @Override
    public List<ModContainer> discover(final ModCandidate candidate, final ASMDataTable table)
    {
        this.table = table;
        final List<ModContainer> found = Lists.newArrayList();
        FMLLog.fine("Examining directory %s for potential mods", candidate.getModContainer().getName());
        exploreFileSystem("", candidate.getModContainer(), found, candidate, null);
        for (final ModContainer mc : found)
        {
            table.addContainer(mc);
        }
        return found;
    }

    public void exploreFileSystem(final String path, final File modDir, final List<ModContainer> harvestedMods, final ModCandidate candidate, MetadataCollection mc)
    {
        MetadataCollection mc1 = mc;
        if (path.isEmpty())
        {
            final File metadata = new File(modDir, "mcmod.info");
            try
            {
                final FileInputStream fis = new FileInputStream(metadata);
                mc1 = MetadataCollection.from(fis,modDir.getName());
                fis.close();
                FMLLog.fine("Found an mcmod.info file in directory %s", modDir.getName());
            }
            catch (final Exception e)
            {
                mc1 = MetadataCollection.from(null,"");
                FMLLog.fine("No mcmod.info file found in directory %s", modDir.getName());
            }
        }

        final File[] content = modDir.listFiles(new ClassFilter());

        // Always sort our content
        Arrays.sort(content);
        for (final File file : content)
        {
            if (file.isDirectory())
            {
                FMLLog.finest("Recursing into package %s", path + file.getName());
                exploreFileSystem(path + file.getName() + ".", file, harvestedMods, candidate, mc1);
                continue;
            }
            final Matcher match = classFile.matcher(file.getName());

            if (match.matches())
            {
                ASMModParser modParser = null;
                try
                {
                    final FileInputStream fis = new FileInputStream(file);
                    modParser = new ASMModParser(fis);
                    fis.close();
                    candidate.addClassEntry(path+file.getName());
                }
                catch (final LoaderException e)
                {
                    FMLLog.log(Level.SEVERE, e, "There was a problem reading the file %s - probably this is a corrupt file", file.getPath());
                    throw e;
                }
                catch (final Exception e)
                {
                    Throwables.propagate(e);
                }

                modParser.validate();
                modParser.sendToTable(table, candidate);
                final ModContainer container = ModContainerFactory.instance().build(modParser, candidate.getModContainer(), candidate);
                if (container!=null)
                {
                    harvestedMods.add(container);
                    container.bindMetadata(mc1);
                }
            }


        }
    }

}
