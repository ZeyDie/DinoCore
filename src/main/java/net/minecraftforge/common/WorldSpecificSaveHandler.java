package net.minecraftforge.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;
import java.util.UUID;

//Class used internally to provide the world specific data directories.

public class WorldSpecificSaveHandler implements ISaveHandler
{
    private WorldServer world;
    private ISaveHandler parent;
    private File dataDir;

    public WorldSpecificSaveHandler(final WorldServer world, final ISaveHandler parent)
    {
        this.world = world;
        this.parent = parent;
        dataDir = new File(world.getChunkSaveLocation(), "data");
        dataDir.mkdirs();
    }

    @Override public WorldInfo loadWorldInfo() { return parent.loadWorldInfo(); }
    @Override public void checkSessionLock() throws MinecraftException { parent.checkSessionLock(); }
    @Override public IChunkLoader getChunkLoader(final WorldProvider var1) { return parent.getChunkLoader(var1); }
    @Override public void saveWorldInfoWithPlayer(final WorldInfo var1, final NBTTagCompound var2) { parent.saveWorldInfoWithPlayer(var1, var2); }
    @Override public void saveWorldInfo(final WorldInfo var1){ parent.saveWorldInfo(var1); }
    @Override public IPlayerFileData getSaveHandler() { return parent.getSaveHandler(); }
    @Override public void flush() { parent.flush(); }
    @Override public String getWorldDirectoryName() { return parent.getWorldDirectoryName(); }

    @Override
    public File getMapFileFromName(final String name)
    {
        return new File(dataDir, name + ".dat");
    }

    // Cauldron start
    @Override
    public UUID getUUID() {
        // TODO Auto-generated method stub
        return null;
    }
    // Cauldron end
}
