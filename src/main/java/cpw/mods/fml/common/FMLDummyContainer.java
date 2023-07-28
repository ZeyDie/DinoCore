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

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.client.FMLFolderResourcePack;
import cpw.mods.fml.common.asm.FMLSanityChecker;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author cpw
 *
 */
public class FMLDummyContainer extends DummyModContainer implements WorldAccessContainer
{
    public FMLDummyContainer()
    {
        super(new ModMetadata());
        final ModMetadata meta = getMetadata();
        meta.modId="FML";
        meta.name="Forge Mod Loader";
        meta.version=Loader.instance().getFMLVersionString();
        meta.credits="Made possible with help from many people";
        meta.authorList= Collections.singletonList("cpw, LexManos");
        meta.description="The Forge Mod Loader provides the ability for systems to load mods " +
                    "from the file system. It also provides key capabilities for mods to be able " +
                    "to cooperate and provide a good modding environment. " +
                    "The mod loading system is compatible with ModLoader, all your ModLoader " +
                    "mods should work.";
        meta.url="https://github.com/MinecraftForge/FML/wiki";
        meta.updateUrl="https://github.com/MinecraftForge/FML/wiki";
        meta.screenshots=new String[0];
        meta.logoFile="";
    }

    @Override
    public boolean registerBus(final EventBus bus, final LoadController controller)
    {
        return true;
    }

    @Override
    public NBTTagCompound getDataForWriting(final SaveHandler handler, final WorldInfo info)
    {
        final NBTTagCompound fmlData = new NBTTagCompound();
        final NBTTagList list = new NBTTagList();
        for (final ModContainer mc : Loader.instance().getActiveModList())
        {
            final NBTTagCompound mod = new NBTTagCompound();
            mod.setString("ModId", mc.getModId());
            mod.setString("ModVersion", mc.getVersion());
            list.appendTag(mod);
        }
        fmlData.setTag("ModList", list);
        final NBTTagList itemList = new NBTTagList();
        GameData.writeItemData(itemList);
        fmlData.setTag("ModItemData", itemList);
        return fmlData;
    }

    @Override
    public void readData(final SaveHandler handler, final WorldInfo info, final Map<String, NBTBase> propertyMap, final NBTTagCompound tag)
    {
        if (tag.hasKey("ModList"))
        {
            final NBTTagList modList = tag.getTagList("ModList");
            for (int i = 0; i < modList.tagCount(); i++)
            {
                final NBTTagCompound mod = (NBTTagCompound) modList.tagAt(i);
                final String modId = mod.getString("ModId");
                final String modVersion = mod.getString("ModVersion");
                final ModContainer container = Loader.instance().getIndexedModList().get(modId);
                if (container == null)
                {
                    FMLLog.log("fml.ModTracker", Level.SEVERE, "This world was saved with mod %s which appears to be missing, things may not work well", modId);
                    continue;
                }
                if (!modVersion.equals(container.getVersion()))
                {
                    FMLLog.log("fml.ModTracker", Level.INFO, "This world was saved with mod %s version %s and it is now at version %s, things may not work well", modId, modVersion, container.getVersion());
                }
            }
        }
        if (tag.hasKey("ModItemData"))
        {
            final NBTTagList modList = tag.getTagList("ModItemData");
            final Set<ItemData> worldSaveItems = GameData.buildWorldItemData(modList);
            GameData.validateWorldSave(worldSaveItems);
        }
        else
        {
            GameData.validateWorldSave(null);
        }
    }


    @Override
    public Certificate getSigningCertificate()
    {
        final Certificate[] certificates = getClass().getProtectionDomain().getCodeSource().getCertificates();
        return certificates != null ? certificates[0] : null;
    }

    @Override
    public File getSource()
    {
        return FMLSanityChecker.fmlLocation;
    }
    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }
}
