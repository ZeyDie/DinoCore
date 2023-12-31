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

import com.google.common.collect.Lists;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.discovery.asm.ASMModParser;

import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;

public class JarDiscoverer implements ITypeDiscoverer
{
    @Override
    public List<ModContainer> discover(final ModCandidate candidate, final ASMDataTable table)
    {
        final List<ModContainer> foundMods = Lists.newArrayList();
        FMLLog.fine("Examining file %s for potential mods", candidate.getModContainer().getName());
        JarFile jar = null;
        try
        {
            jar = new JarFile(candidate.getModContainer());

            if (jar.getManifest()!=null && (jar.getManifest().getMainAttributes().get("FMLCorePlugin") != null || jar.getManifest().getMainAttributes().get("TweakClass") != null))
            {
                FMLLog.finest("Ignoring coremod or tweak system %s", candidate.getModContainer());
                return foundMods;
            }
            final ZipEntry modInfo = jar.getEntry("mcmod.info");
            MetadataCollection mc = null;
            if (modInfo != null)
            {
                FMLLog.finer("Located mcmod.info file in file %s", candidate.getModContainer().getName());
                mc = MetadataCollection.from(jar.getInputStream(modInfo), candidate.getModContainer().getName());
            }
            else
            {
                FMLLog.fine("The mod container %s appears to be missing an mcmod.info file", candidate.getModContainer().getName());
                mc = MetadataCollection.from(null, "");
            }
            for (final ZipEntry ze : Collections.list(jar.entries()))
            {
                if (ze.getName()!=null && ze.getName().startsWith("__MACOSX"))
                {
                    continue;
                }
                final Matcher match = classFile.matcher(ze.getName());
                if (match.matches())
                {
                    final ASMModParser modParser;
                    try
                    {
                        modParser = new ASMModParser(jar.getInputStream(ze));
                        candidate.addClassEntry(ze.getName());
                    }
                    catch (final LoaderException e)
                    {
                        FMLLog.log(Level.SEVERE, e, "There was a problem reading the entry %s in the jar %s - probably a corrupt zip", ze.getName(), candidate.getModContainer().getPath());
                        jar.close();
                        throw e;
                    }
                    modParser.validate();
                    modParser.sendToTable(table, candidate);
                    final ModContainer container = ModContainerFactory.instance().build(modParser, candidate.getModContainer(), candidate);
                    if (container!=null)
                    {
                        table.addContainer(container);
                        foundMods.add(container);
                        container.bindMetadata(mc);
                    }
                }
            }
        }
        catch (final Exception e)
        {
            FMLLog.log(Level.WARNING, e, "Zip file %s failed to read properly, it will be ignored", candidate.getModContainer().getName());
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (final Exception e)
                {
                }
            }
        }
        return foundMods;
    }

}
