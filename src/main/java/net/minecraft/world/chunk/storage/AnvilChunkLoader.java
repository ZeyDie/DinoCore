package net.minecraft.world.chunk.storage;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.asm.transformers.SideTransformer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraftforge.cauldron.CauldronUtils;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

// Cauldron start
// Cauldron end

public class AnvilChunkLoader implements IThreadedFileIO, IChunkLoader
{
    private java.util.LinkedHashMap<ChunkCoordIntPair, AnvilChunkLoaderPending> pendingSaves = new java.util.LinkedHashMap<ChunkCoordIntPair, AnvilChunkLoaderPending>(); // Spigot
    private Object syncLockObject = new Object();
    private List checkedTileEntities = new ArrayList(); // Cauldron

    /** Save directory for chunks using the Anvil format */
    public final File chunkSaveLocation;

    public AnvilChunkLoader(final File par1File)
    {
        this.chunkSaveLocation = par1File;
    }

    // CraftBukkit start
    public boolean chunkExists(final World world, final int i, final int j)
    {
        final ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);

        synchronized (this.syncLockObject)
        {
            // Spigot start
            if (pendingSaves.containsKey(chunkcoordintpair))
            {
                return true;
            }
        }

        // Spigot end
        return RegionFileCache.createOrLoadRegionFile(this.chunkSaveLocation, i, j).chunkExists(i & 31, j & 31);
    }
    // CraftBukkit end

    // CraftBukkit start - Add async variant, provide compatibility

    /**
     * Loads the specified(XZ) chunk into the specified world.
     */
    public Chunk loadChunk(final World par1World, final int par2, final int par3)
    {
        par1World.timings.syncChunkLoadDataTimer.startTiming(); // Spigot
        final Object[] data = this.loadChunk__Async_CB(par1World, par2, par3);
        par1World.timings.syncChunkLoadDataTimer.stopTiming(); // Spigot

        if (data != null)
        {
            final Chunk chunk = (Chunk) data[0];
            final NBTTagCompound nbttagcompound = (NBTTagCompound) data[1];
            this.loadEntities(chunk, nbttagcompound.getCompoundTag("Level"), par1World);
            MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(chunk, nbttagcompound)); // Cauldron - Don't call ChunkDataEvent.Load async
            return chunk;
        }

        return null;
    }

    public Object[] loadChunk__Async_CB(final World world, final int i, final int j)
    {
        // CraftBukkit end
        NBTTagCompound nbttagcompound = null;
        final ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
        final Object object = this.syncLockObject;

        synchronized (this.syncLockObject)
        {
            // Spigot start
            final AnvilChunkLoaderPending anvilchunkloaderpending = pendingSaves.get(chunkcoordintpair);

            if (anvilchunkloaderpending != null)
            {
                nbttagcompound = anvilchunkloaderpending.nbtTags;
            }

            /*
            if (this.b.contains(chunkcoordintpair)) {
                for (int k = 0; k < this.a.size(); ++k) {
                    if (((PendingChunkToSave) this.a.get(k)).a.equals(chunkcoordintpair)) {
                        nbttagcompound = ((PendingChunkToSave) this.a.get(k)).b;
                        break;
                    }
                }
            }
            */// Spigot end
        }

        if (nbttagcompound == null)
        {
            final DataInputStream datainputstream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, i, j);

            if (datainputstream == null)
            {
                return null;
            }

            // Cauldron start - catch exception for MCP binary compatibility
            try {
                nbttagcompound = CompressedStreamTools.read((DataInput) datainputstream);
            } catch (final Throwable t) {
                t.printStackTrace();
            }
            // Cauldron end
        }

        return this.a(world, i, j, nbttagcompound);
    }

    protected Object[] a(final World world, final int i, final int j, final NBTTagCompound nbttagcompound)   // CraftBukkit - return Chunk -> Object[]
    {
        // Cauldron start
        if(nbttagcompound == null)
        {
            world.getWorldLogAgent().logSevere("Chunk file at " + i + "," + j + " is missing nbt tag data, this is not good!");
            return null;
        }
        // Cauldron end
        if (!nbttagcompound.hasKey("Level"))
        {
            world.getWorldLogAgent().logSevere("Chunk file at " + i + "," + j + " is missing level data, skipping");
            return null;
        }
        else if (!nbttagcompound.getCompoundTag("Level").hasKey("Sections"))
        {
            world.getWorldLogAgent().logSevere("Chunk file at " + i + "," + j + " is missing block data, skipping");
            return null;
        }
        else
        {
            Chunk chunk = this.readChunkFromNBT(world, nbttagcompound.getCompoundTag("Level"));

            if (!chunk.isAtLocation(i, j))
            {
                world.getWorldLogAgent().logSevere("Chunk file at " + i + "," + j + " is in the wrong location; relocating. (Expected " + i + ", " + j + ", got " + chunk.xPosition + ", " + chunk.zPosition + ")");
                nbttagcompound.getCompoundTag("Level").setInteger("xPos", i); // CraftBukkit - .getCompound("Level")
                nbttagcompound.getCompoundTag("Level").setInteger("zPos", j); // CraftBukkit - .getCompound("Level")
                // CraftBukkit start - Have to move tile entities since we don't load them at this stage
                final NBTTagList tileEntities = nbttagcompound.getCompoundTag("Level").getTagList("TileEntities");

                if (tileEntities != null)
                {
                    for (int te = 0; te < tileEntities.tagCount(); te++)
                    {
                        final NBTTagCompound tileEntity = (NBTTagCompound) tileEntities.tagAt(te);
                        final int x = tileEntity.getInteger("x") - chunk.xPosition * 16;
                        final int z = tileEntity.getInteger("z") - chunk.zPosition * 16;
                        tileEntity.setInteger("x", i * 16 + x);
                        tileEntity.setInteger("z", j * 16 + z);
                    }
                }

                // CraftBukkit end
                chunk = this.readChunkFromNBT(world, nbttagcompound.getCompoundTag("Level"));
            }

            // CraftBukkit start
            final Object[] data = new Object[2];
            data[0] = chunk;
            data[1] = nbttagcompound;
            // Cauldron - Don't call ChunkDataEvent.Load async
            return data;
            // CraftBukkit end
        }
    }

    public void saveChunk(final World par1World, final Chunk par2Chunk)
    {
        // CraftBukkit start - "handle" exception
        try
        {
            par1World.checkSessionLock();
        }
        catch (final MinecraftException ex)
        {
            // Cauldron disable this for now.
            //ex.printStackTrace();
        }

        // CraftBukkit end

        try
        {
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound.setTag("Level", nbttagcompound1);
            this.writeChunkToNBT(par2Chunk, par1World, nbttagcompound1);
            MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Save(par2Chunk, nbttagcompound));
            this.addChunkToPending(par2Chunk.getChunkCoordIntPair(), nbttagcompound);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
    }

    protected void addChunkToPending(final ChunkCoordIntPair par1ChunkCoordIntPair, final NBTTagCompound par2NBTTagCompound)
    {
        final Object object = this.syncLockObject;

        synchronized (this.syncLockObject)
        {
            // Spigot start
            if (this.pendingSaves.put(par1ChunkCoordIntPair, new AnvilChunkLoaderPending(par1ChunkCoordIntPair, par2NBTTagCompound)) != null)
            {
                return;
            }
            /*
            if (this.pendingAnvilChunksCoordinates.contains(par1ChunkCoordIntPair))
            {
                for (int i = 0; i < this.chunksToRemove.size(); ++i)
                {
                    if (((AnvilChunkLoaderPending)this.chunksToRemove.get(i)).chunkCoordinate.equals(par1ChunkCoordIntPair))
                    {
                        this.chunksToRemove.set(i, new AnvilChunkLoaderPending(par1ChunkCoordIntPair, par2NBTTagCompound));
                        return;
                    }
                }
            }

            this.chunksToRemove.add(new AnvilChunkLoaderPending(par1ChunkCoordIntPair, par2NBTTagCompound));
            this.pendingAnvilChunksCoordinates.add(par1ChunkCoordIntPair);
            */// Spigot end
            ThreadedFileIOBase.threadedIOInstance.queueIO(this);
        }
    }

    /**
     * Returns a boolean stating if the write was unsuccessful.
     */
    public boolean writeNextIO()
    {
        AnvilChunkLoaderPending anvilchunkloaderpending = null;
        final Object object = this.syncLockObject;

        synchronized (this.syncLockObject)
        {
            // Spigot start
            if (this.pendingSaves.isEmpty())
            {
                return false;
            }

            anvilchunkloaderpending = this.pendingSaves.values().iterator().next();
            this.pendingSaves.remove(anvilchunkloaderpending.chunkCoordinate);
            /*
            if (this.chunksToRemove.isEmpty())
            {
                return false;
            }

            anvilchunkloaderpending = (AnvilChunkLoaderPending)this.chunksToRemove.remove(0);
            this.pendingAnvilChunksCoordinates.remove(anvilchunkloaderpending.chunkCoordinate);
            */// Spigot end
        }

        if (anvilchunkloaderpending != null)
        {
            try
            {
                this.writeChunkNBTTags(anvilchunkloaderpending);
            }
            catch (final Exception exception)
            {
                exception.printStackTrace();
            }
        }

        return true;
    }

    public void writeChunkNBTTags(final AnvilChunkLoaderPending par1AnvilChunkLoaderPending) throws java.io.IOException   // CraftBukkit - public -> private, added throws
    {
        final DataOutputStream dataoutputstream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, par1AnvilChunkLoaderPending.chunkCoordinate.chunkXPos, par1AnvilChunkLoaderPending.chunkCoordinate.chunkZPos);
        CompressedStreamTools.write(par1AnvilChunkLoaderPending.nbtTags, (DataOutput) dataoutputstream);
        dataoutputstream.close();
    }

    /**
     * Save extra data associated with this Chunk not normally saved during autosave, only during chunk unload.
     * Currently unused.
     */
    public void saveExtraChunkData(final World par1World, final Chunk par2Chunk) {}

    /**
     * Called every World.tick()
     */
    public void chunkTick() {}

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unused.
     */
    public void saveExtraData()
    {
        while (this.writeNextIO())
        {
            ;
        }
    }

    /**
     * Writes the Chunk passed as an argument to the NBTTagCompound also passed, using the World argument to retrieve
     * the Chunk's last update time.
     */
    private void writeChunkToNBT(final Chunk par1Chunk, final World par2World, final NBTTagCompound par3NBTTagCompound)
    {
        par3NBTTagCompound.setInteger("xPos", par1Chunk.xPosition);
        par3NBTTagCompound.setInteger("zPos", par1Chunk.zPosition);
        par3NBTTagCompound.setLong("LastUpdate", par2World.getTotalWorldTime());
        par3NBTTagCompound.setIntArray("HeightMap", par1Chunk.heightMap);
        par3NBTTagCompound.setBoolean("TerrainPopulated", par1Chunk.isTerrainPopulated);
        par3NBTTagCompound.setLong("InhabitedTime", par1Chunk.inhabitedTime);
        final ExtendedBlockStorage[] aextendedblockstorage = par1Chunk.getBlockStorageArray();
        final NBTTagList nbttaglist = new NBTTagList("Sections");
        final boolean flag = !par2World.provider.hasNoSky;
        final ExtendedBlockStorage[] aextendedblockstorage1 = aextendedblockstorage;
        int i = aextendedblockstorage.length;
        NBTTagCompound nbttagcompound1;

        for (int j = 0; j < i; ++j)
        {
            final ExtendedBlockStorage extendedblockstorage = aextendedblockstorage1[j];

            if (extendedblockstorage != null)
            {
                nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Y", (byte)(extendedblockstorage.getYLocation() >> 4 & 255));
                nbttagcompound1.setByteArray("Blocks", extendedblockstorage.getBlockLSBArray());

                if (extendedblockstorage.getBlockMSBArray() != null)
                {
                    nbttagcompound1.setByteArray("Add", extendedblockstorage.getBlockMSBArray().getValueArray()); // Spigot
                }

                nbttagcompound1.setByteArray("Data", extendedblockstorage.getMetadataArray().getValueArray()); // Spigot
                nbttagcompound1.setByteArray("BlockLight", extendedblockstorage.getBlocklightArray().getValueArray()); // Spigot

                if (flag)
                {
                    nbttagcompound1.setByteArray("SkyLight", extendedblockstorage.getSkylightArray().getValueArray()); // Spigot
                }
                else
                {
                    nbttagcompound1.setByteArray("SkyLight", new byte[extendedblockstorage.getBlocklightArray().getValueArray().length]); // Spigot
                }

                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        par3NBTTagCompound.setTag("Sections", nbttaglist);
        par3NBTTagCompound.setByteArray("Biomes", par1Chunk.getBiomeArray());
        par1Chunk.hasEntities = false;
        final NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator;

        for (i = 0; i < par1Chunk.entityLists.length; ++i)
        {
            iterator = par1Chunk.entityLists[i].iterator();

            while (iterator.hasNext())
            {
                final Entity entity = (Entity)iterator.next();
                nbttagcompound1 = new NBTTagCompound();

                try
                {
                    if (entity.writeToNBTOptional(nbttagcompound1))
                    {
                        par1Chunk.hasEntities = true;
                        nbttaglist1.appendTag(nbttagcompound1);
                    }
                }
                catch (final Exception e)
                {
                    FMLLog.log(Level.SEVERE, e,
                            "An Entity type %s at %s,%f,%f,%f has thrown an exception trying to write state. It will not persist. Report this to the mod author",
                            entity.getClass().getName(),
                            entity.worldObj.getWorld().getName(),
                            entity.posX, entity.posY, entity.posZ); // Cauldron - add location
                }
            }
        }

        par3NBTTagCompound.setTag("Entities", nbttaglist1);
        final NBTTagList nbttaglist2 = new NBTTagList();
        iterator = par1Chunk.chunkTileEntityMap.values().iterator();

        while (iterator.hasNext())
        {
            final TileEntity tileentity = (TileEntity)iterator.next();
            nbttagcompound1 = new NBTTagCompound();
            try
            {
                tileentity.writeToNBT(nbttagcompound1);
                nbttaglist2.appendTag(nbttagcompound1);
            }
            catch (final Exception e)
            {
                FMLLog.log(Level.SEVERE, e,
                        "A TileEntity type %s at %s,%d,%d,%d has throw an exception trying to write state. It will not persist. Report this to the mod author",
                        tileentity.getClass().getName(),
                        tileentity.worldObj.getWorld().getName(),
                        tileentity.xCoord, tileentity.yCoord, tileentity.zCoord); // Cauldron - add location
            }
        }

        par3NBTTagCompound.setTag("TileEntities", nbttaglist2);
        final List list = par2World.getPendingBlockUpdates(par1Chunk, false);

        if (list != null)
        {
            final long k = par2World.getTotalWorldTime();
            final NBTTagList nbttaglist3 = new NBTTagList();
            final Iterator iterator1 = list.iterator();

            while (iterator1.hasNext())
            {
                final NextTickListEntry nextticklistentry = (NextTickListEntry)iterator1.next();
                final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                nbttagcompound2.setInteger("i", nextticklistentry.blockID);
                nbttagcompound2.setInteger("x", nextticklistentry.xCoord);
                nbttagcompound2.setInteger("y", nextticklistentry.yCoord);
                nbttagcompound2.setInteger("z", nextticklistentry.zCoord);
                nbttagcompound2.setInteger("t", (int)(nextticklistentry.scheduledTime - k));
                nbttagcompound2.setInteger("p", nextticklistentry.priority);
                nbttaglist3.appendTag(nbttagcompound2);
            }

            par3NBTTagCompound.setTag("TileTicks", nbttaglist3);
        }
    }

    /**
     * Reads the data stored in the passed NBTTagCompound and creates a Chunk with that data in the passed World.
     * Returns the created Chunk.
     */
    private Chunk readChunkFromNBT(final World par1World, final NBTTagCompound par2NBTTagCompound)
    {
        final int i = par2NBTTagCompound.getInteger("xPos");
        final int j = par2NBTTagCompound.getInteger("zPos");
        final Chunk chunk = new Chunk(par1World, i, j);
        chunk.heightMap = par2NBTTagCompound.getIntArray("HeightMap");
        chunk.isTerrainPopulated = par2NBTTagCompound.getBoolean("TerrainPopulated");
        chunk.inhabitedTime = par2NBTTagCompound.getLong("InhabitedTime");
        final NBTTagList nbttaglist = par2NBTTagCompound.getTagList("Sections");
        final byte b0 = 16;
        final ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[b0];
        final boolean flag = !par1World.provider.hasNoSky;

        for (int k = 0; k < nbttaglist.tagCount(); ++k)
        {
            final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(k);
            final byte b1 = nbttagcompound1.getByte("Y");
            final ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(b1 << 4, flag);
            extendedblockstorage.setBlockLSBArray(nbttagcompound1.getByteArray("Blocks"));

            if (nbttagcompound1.hasKey("Add"))
            {
                extendedblockstorage.setBlockMSBArray(new NibbleArray(nbttagcompound1.getByteArray("Add"), 4));
            }

            extendedblockstorage.setBlockMetadataArray(new NibbleArray(nbttagcompound1.getByteArray("Data"), 4));
            extendedblockstorage.setBlocklightArray(new NibbleArray(nbttagcompound1.getByteArray("BlockLight"), 4));

            if (flag)
            {
                extendedblockstorage.setSkylightArray(new NibbleArray(nbttagcompound1.getByteArray("SkyLight"), 4));
            }

            extendedblockstorage.removeInvalidBlocks();
            aextendedblockstorage[b1] = extendedblockstorage;
        }

        chunk.setStorageArrays(aextendedblockstorage);

        if (par2NBTTagCompound.hasKey("Biomes"))
        {
            chunk.setBiomeArray(par2NBTTagCompound.getByteArray("Biomes"));
        }

        // CraftBukkit start - End this method here and split off entity loading to another method
        return chunk;
    }

    public void loadEntities(final Chunk chunk, final NBTTagCompound nbttagcompound, final World world)
    {
        // CraftBukkit end
        world.timings.syncChunkLoadEntitiesTimer.startTiming(); // Spigot
        final NBTTagList nbttaglist1 = nbttagcompound.getTagList("Entities");

        if (nbttaglist1 != null)
        {
            for (int l = 0; l < nbttaglist1.tagCount(); ++l)
            {
                final NBTTagCompound nbttagcompound2 = (NBTTagCompound)nbttaglist1.tagAt(l);
                final Entity entity = EntityList.createEntityFromNBT(nbttagcompound2, world);
                chunk.hasEntities = true;

                if (entity != null)
                {
                    chunk.addEntity(entity);
                    // Cauldron start - check to see if we killed entity due to invalid location
                    if (!entity.isDead)
                    {
                        Entity entity1 = entity;
    
                        for (NBTTagCompound nbttagcompound3 = nbttagcompound2; nbttagcompound3.hasKey("Riding"); nbttagcompound3 = nbttagcompound3.getCompoundTag("Riding"))
                        {
                            final Entity entity2 = EntityList.createEntityFromNBT(nbttagcompound3.getCompoundTag("Riding"), world);
    
                            if (entity2 != null)
                            {
                                chunk.addEntity(entity2);
                                entity1.mountEntity(entity2);
                            }
    
                            entity1 = entity2;
                        }
                    }
                    // Cauldron end
                }
            }
        }

        world.timings.syncChunkLoadEntitiesTimer.stopTiming(); // Spigot
        world.timings.syncChunkLoadTileEntitiesTimer.startTiming(); // Spigot
        final NBTTagList nbttaglist2 = nbttagcompound.getTagList("TileEntities");

        if (nbttaglist2 != null)
        {
            for (int i1 = 0; i1 < nbttaglist2.tagCount(); ++i1)
            {
                final NBTTagCompound nbttagcompound4 = (NBTTagCompound)nbttaglist2.tagAt(i1);
                final TileEntity tileentity = TileEntity.createAndLoadEntity(nbttagcompound4);

                if (tileentity != null)
                {
                    // Cauldron start - check if TE should tick and inject into Bukkit's InventoryType
                    if (!this.checkedTileEntities.contains(tileentity.getClass()))
                    {
                        // verify if TE should tick
                        if (MinecraftServer.getServer().tileEntityConfig.preventInvalidTileEntityUpdates.getValue())
                        {
                            SideTransformer.allowInvalidSide = true;
                            if (!CauldronUtils.isOverridingUpdateEntity(tileentity.getClass()) && CauldronUtils.canTileEntityUpdate(tileentity.getClass()))
                            {
                                if (MinecraftServer.getServer().tileEntityConfig.enableTECanUpdateWarning.getValue())
                                {
                                    MinecraftServer.getServer().logInfo("Detected TE " + tileentity.getClass().getName() + " with canUpdate set to true and no updateEntity override!. Please report to mod author as this can hurt performance.");
                                }
                                MinecraftServer.getServer().bannedTileEntityUpdates.add(tileentity.getClass());
                            }
                            SideTransformer.allowInvalidSide = false;
                        }
                        // inject TE into InventoryType to support inventory events
                        EnumHelper.addInventoryType(tileentity);
                        this.checkedTileEntities.add(tileentity.getClass());
                    }
                    // Cauldron end
                    chunk.addTileEntity(tileentity);
                }
            }
        }

        world.timings.syncChunkLoadTileEntitiesTimer.stopTiming(); // Spigot
        world.timings.syncChunkLoadTileTicksTimer.startTiming(); // Spigot
        if (nbttagcompound.hasKey("TileTicks"))
        {
            final NBTTagList nbttaglist3 = nbttagcompound.getTagList("TileTicks");

            if (nbttaglist3 != null)
            {
                for (int j1 = 0; j1 < nbttaglist3.tagCount(); ++j1)
                {
                    final NBTTagCompound nbttagcompound5 = (NBTTagCompound)nbttaglist3.tagAt(j1);
                    world.scheduleBlockUpdateFromLoad(nbttagcompound5.getInteger("x"), nbttagcompound5.getInteger("y"), nbttagcompound5.getInteger("z"), nbttagcompound5.getInteger("i"), nbttagcompound5.getInteger("t"), nbttagcompound5.getInteger("p"));
                }
            }
        }
        world.timings.syncChunkLoadTileTicksTimer.stopTiming(); // Spigot

        // return chunk; // CraftBukkit
    }
}
