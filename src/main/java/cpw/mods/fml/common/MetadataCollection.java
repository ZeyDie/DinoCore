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

package cpw.mods.fml.common;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;

public class MetadataCollection
{
    private static JdomParser parser = new JdomParser();
    private Map<String, ModMetadata> metadatas = Maps.newHashMap();
    private int metadataVersion = 1;

    public static MetadataCollection from(final InputStream inputStream, final String sourceName)
    {
        if (inputStream == null)
        {
            return new MetadataCollection();
        }

        final InputStreamReader reader = new InputStreamReader(inputStream);
        try
        {
            final JsonRootNode root = parser.parse(reader);
            if (root.hasElements())
            {
                return parse10ModInfo(root);
            }
            else
            {
                return parseModInfo(root);
            }
        }
        catch (final InvalidSyntaxException e)
        {
            FMLLog.log(Level.SEVERE, e, "The mcmod.info file in %s cannot be parsed as valid JSON. It will be ignored", sourceName);
            return new MetadataCollection();
        }
        catch (final Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    private static MetadataCollection parseModInfo(final JsonRootNode root)
    {
        final MetadataCollection mc = new MetadataCollection();
        mc.metadataVersion = Integer.parseInt(root.getNumberValue("modinfoversion"));
        mc.parseModMetadataList(root.getNode("modlist"));
        return mc;
    }

    private static MetadataCollection parse10ModInfo(final JsonRootNode root)
    {
        final MetadataCollection mc = new MetadataCollection();
        mc.parseModMetadataList(root);
        return mc;
    }

    private void parseModMetadataList(final JsonNode metadataList)
    {
        for (final JsonNode node : metadataList.getElements())
        {
            final ModMetadata mmd = new ModMetadata(node);
            metadatas.put(mmd.modId, mmd);
        }
    }

    public ModMetadata getMetadataForId(final String modId, final Map<String, Object> extraData)
    {
        if (!metadatas.containsKey(modId))
        {
            final ModMetadata dummy = new ModMetadata();
            dummy.modId = modId;
            dummy.name = (String) extraData.get("name");
            dummy.version = (String) extraData.get("version");
            dummy.autogenerated = true;
            metadatas.put(modId, dummy);
        }
        return metadatas.get(modId);
    }

}
