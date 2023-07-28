package net.minecraft.world.gen;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockSand;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraftforge.cauldron.CauldronHooks;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_6_R3.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_6_R3.util.LongHash;
import org.bukkit.craftbukkit.v1_6_R3.util.LongHashSet;
import org.bukkit.craftbukkit.v1_6_R3.util.LongObjectHashMap;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// CraftBukkit start
// CraftBukkit end
// Cauldron start
// Cauldron end

public class ChunkProviderServer implements IChunkProvider
{
    // CraftBukkit start

    /**
     * used by unload100OldestChunks to iterate the loadedChunkHashMap for unload (underlying assumption, first in,
     * first out)
     */
    public LongHashSet chunksToUnload = new LongHashSet();
    public Chunk defaultEmptyChunk;
    public IChunkProvider currentChunkProvider; // CraftBukkit
    public IChunkLoader currentChunkLoader; // Spigot

    /**
     * if this is false, the defaultEmptyChunk will be returned by the provider
     */
    public boolean loadChunkOnProvideRequest = MinecraftServer.getServer().cauldronConfig.loadChunkOnRequest.getValue(); // Cauldron - if true, allows mods to force load chunks. to disable, set load-chunk-on-request in cauldron.yml to false
    public int initialTick; // Cauldron counter to keep track of when this loader was created
    public LongObjectHashMap<Chunk> loadedChunkHashMap = new LongObjectHashMap<Chunk>();
    public List loadedChunks = new ArrayList(); // Cauldron  vanilla compatibility
    public WorldServer worldObj;
    // CraftBukkit end

    public ChunkProviderServer(final WorldServer par1WorldServer, final IChunkLoader par2IChunkLoader, final IChunkProvider par3IChunkProvider)
    {
        this.initialTick = MinecraftServer.currentTick; // Cauldron keep track of when the loader was created
        this.defaultEmptyChunk = new EmptyChunk(par1WorldServer, 0, 0);
        this.worldObj = par1WorldServer;
        this.currentChunkLoader = par2IChunkLoader;
        this.currentChunkProvider = par3IChunkProvider;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(final int par1, final int par2)
    {
        return this.loadedChunkHashMap.containsKey(LongHash.toLong(par1, par2)); // CraftBukkit
    }

    /**
     * marks chunk for unload by "unload100OldestChunks"  if there is no spawn point, or if the center of the chunk is
     * outside 200 blocks (x or z) of the spawn
     */
    public void unloadChunksIfNotNearSpawn(final int par1, final int par2)
    {
        if (this.worldObj.provider.canRespawnHere() && DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId))
        {
            final ChunkCoordinates chunkcoordinates = this.worldObj.getSpawnPoint();
            final int k = par1 * 16 + 8 - chunkcoordinates.posX;
            final int l = par2 * 16 + 8 - chunkcoordinates.posZ;
            final short short1 = 128;

            // CraftBukkit start
            if (k < -short1 || k > short1 || l < -short1 || l > short1)
            {
                final Chunk c = this.loadedChunkHashMap.get(LongHash.toLong(par1, par2));
                this.chunksToUnload.add(par1, par2);

                if (c != null)
                {
                    c.mustSave = true;
                }
                CauldronHooks.logChunkUnload(this, par1, par2, "Chunk added to unload queue");
            }
            // CraftBukkit end
        }
        else
        {
            // CraftBukkit start
            final Chunk c = this.loadedChunkHashMap.get(LongHash.toLong(par1, par2));
            this.chunksToUnload.add(par1, par2);

            if (c != null)
            {
                c.mustSave = true;
            }
            CauldronHooks.logChunkUnload(this, par1, par2, "Chunk added to unload queue");
            // CraftBukkit end
        }
    }

    /**
     * marks all chunks for unload, ignoring those near the spawn
     */
    public void unloadAllChunks()
    {
        // Cauldron start -- use thread-safe method for iterating loaded chunks
        final Object[] chunks = this.loadedChunks.toArray();

        for (int j = 0; j < chunks.length; ++j)
        {
            final Chunk chunk = (Chunk)chunks[j];
            //Cauldron end
            this.unloadChunksIfNotNearSpawn(chunk.xPosition, chunk.zPosition);
        }
    }

    // CraftBukkit start - Add async variant, provide compatibility

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(final int par1, final int par2)
    {
        return getChunkAt(par1, par2, null);
    }

    public Chunk getChunkAt(final int i, final int j, final Runnable runnable)
    {
        this.chunksToUnload.remove(i, j);
        Chunk chunk = (Chunk) this.loadedChunkHashMap.get(LongHash.toLong(i, j));
        final boolean newChunk = false;
        AnvilChunkLoader loader = null;

        if (this.currentChunkLoader instanceof AnvilChunkLoader)
        {
            loader = (AnvilChunkLoader) this.currentChunkLoader;
        }

        CauldronHooks.logChunkLoad(this, "Get", i, j, true);

        // We can only use the queue for already generated chunks
        if (chunk == null && loader != null && loader.chunkExists(this.worldObj, i, j))
        {
            if (runnable != null)
            {
                ChunkIOExecutor.queueChunkLoad(this.worldObj, loader, this, i, j, runnable);
                return null;
            }
            else
            {
                chunk = ChunkIOExecutor.syncChunkLoad(this.worldObj, loader, this, i, j);
            }
        }
        else if (chunk == null)
        {
            chunk = this.originalGetChunkAt(i, j);
        }

        // If we didn't load the chunk async and have a callback run it now
        if (runnable != null)
        {
            runnable.run();
        }

        return chunk;
    }

    public Chunk originalGetChunkAt(final int i, final int j)
    {
        this.chunksToUnload.remove(i, j);
        Chunk chunk = (Chunk) this.loadedChunkHashMap.get(LongHash.toLong(i, j));
        boolean newChunk = false;
    // CraftBukkit end

        if (chunk == null)
        {
            worldObj.timings.syncChunkLoadTimer.startTiming(); // Spigot
            chunk = ForgeChunkManager.fetchDormantChunk(LongHash.toLong(i, j), this.worldObj);

            if (chunk == null)
            {
                chunk = this.safeLoadChunk(i, j);
            }

            if (chunk == null)
            {
                if (this.currentChunkProvider == null)
                {
                    chunk = this.defaultEmptyChunk;
                }
                else
                {
                    try
                    {
                        chunk = this.currentChunkProvider.provideChunk(i, j);
                    }
                    catch (final Throwable throwable)
                    {
                        final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                        final CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                        crashreportcategory.addCrashSection("Location", String.format("%d,%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j)}));
                        crashreportcategory.addCrashSection("Position hash", Long.valueOf(LongHash.toLong(i, j))); // CraftBukkit - Use LongHash
                        crashreportcategory.addCrashSection("Generator", this.currentChunkProvider.makeString());
                        throw new ReportedException(crashreport);
                    }
                }

                newChunk = true; // CraftBukkit
            }

            this.loadedChunkHashMap.put(LongHash.toLong(i, j), chunk); // CraftBukkit
            this.loadedChunks.add(chunk); // Cauldron - vanilla compatibility

            if (chunk != null)
            {
                chunk.onChunkLoad();
            }
            // CraftBukkit start
            final Server server = this.worldObj.getServer();

            if (server != null)
            {
                /*
                 * If it's a new world, the first few chunks are generated inside
                 * the World constructor. We can't reliably alter that, so we have
                 * no way of creating a CraftWorld/CraftServer at that point.
                 */
                server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(chunk.bukkitChunk, newChunk));
            }

            // CraftBukkit end
            chunk.populateChunk(this, this, i, j);
            worldObj.timings.syncChunkLoadTimer.stopTiming(); // Spigot
        }

        return chunk;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(final int par1, final int par2)
    {
        // CraftBukkit start
        Chunk chunk = (Chunk) this.loadedChunkHashMap.get(LongHash.toLong(par1, par2));
        chunk = chunk == null ? (shouldLoadChunk() ? this.loadChunk(par1, par2) : this.defaultEmptyChunk) : chunk; //Cauldron handle forge server tick events and load the chunk within 5 seconds of the world being loaded (for chunk loaders)

        if (chunk == this.defaultEmptyChunk)
        {
            return chunk;
        }

        if (par1 != chunk.xPosition || par2 != chunk.zPosition)
        {
            this.worldObj.getWorldLogAgent().logSevere("Chunk (" + chunk.xPosition + ", " + chunk.zPosition + ") stored at  (" + par1 + ", " + par2 + ") in world '" + worldObj.getWorld().getName() + "'");
            this.worldObj.getWorldLogAgent().logSevere(chunk.getClass().getName());
            final Throwable ex = new Throwable();
            ex.fillInStackTrace();
            ex.printStackTrace();
        }
        chunk.lastAccessedTick = MinecraftServer.getServer().getTickCounter(); // Cauldron
        return chunk;
        // CraftBukkit end
    }

    /**
     * used by loadChunk, but catches any exceptions if the load fails.
     */
    public Chunk safeLoadChunk(final int par1, final int par2)   // CraftBukkit - private -> public
    {
        if (this.currentChunkLoader == null)
        {
            return null;
        }
        else
        {
            try
            {
                CauldronHooks.logChunkLoad(this, "Safe Load", par1, par2, false);

                final Chunk chunk = this.currentChunkLoader.loadChunk(this.worldObj, par1, par2);

                if (chunk != null)
                {
                    chunk.lastSaveTime = this.worldObj.getTotalWorldTime();

                    if (this.currentChunkProvider != null)
                    {
                        worldObj.timings.syncChunkLoadStructuresTimer.startTiming(); // Spigot
                        this.currentChunkProvider.recreateStructures(par1, par2);
                        worldObj.timings.syncChunkLoadStructuresTimer.stopTiming(); // Spigot
                    }
                    chunk.lastAccessedTick = MinecraftServer.getServer().getTickCounter(); // Cauldron
                }

                return chunk;
            }
            catch (final Exception exception)
            {
                exception.printStackTrace();
                return null;
            }
        }
    }

    /**
     * used by saveChunks, but catches any exceptions if the save fails.
     */
    public void safeSaveExtraChunkData(final Chunk par1Chunk)   // CraftBukkit - private -> public
    {
        if (this.currentChunkLoader != null)
        {
            try
            {
                this.currentChunkLoader.saveExtraChunkData(this.worldObj, par1Chunk);
            }
            catch (final Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * used by saveChunks, but catches any exceptions if the save fails.
     */
    public void safeSaveChunk(final Chunk par1Chunk)   // CraftBukkit - private -> public
    {
        if (this.currentChunkLoader != null)
        {
            try
            {
                par1Chunk.lastSaveTime = this.worldObj.getTotalWorldTime();
                this.currentChunkLoader.saveChunk(this.worldObj, par1Chunk);
            }
            catch (final Exception ioexception)     // CraftBukkit - IOException -> Exception
            {
                ioexception.printStackTrace();
                // CraftBukkit start - Remove extra exception
            }

            // } catch (ExceptionWorldConflict exceptionworldconflict) {
            //     exceptionworldconflict.printStackTrace();
            // }
            // CraftBukkit end
        }
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(final IChunkProvider par1IChunkProvider, final int par2, final int par3)
    {
        final Chunk chunk = this.provideChunk(par2, par3);

        if (!chunk.isTerrainPopulated)
        {
            chunk.isTerrainPopulated = true;

            if (this.currentChunkProvider != null)
            {
                this.currentChunkProvider.populate(par1IChunkProvider, par2, par3);
                // CraftBukkit start
                BlockSand.fallInstantly = true;
                final Random random = new Random();
                random.setSeed(worldObj.getSeed());
                final long xRand = random.nextLong() / 2L * 2L + 1L;
                final long zRand = random.nextLong() / 2L * 2L + 1L;
                random.setSeed((long) par2 * xRand + (long) par3 * zRand ^ worldObj.getSeed());
                final org.bukkit.World world = this.worldObj.getWorld();

                if (world != null)
                {
                    for (final org.bukkit.generator.BlockPopulator populator : world.getPopulators())
                    {
                        populator.populate(world, random, chunk.bukkitChunk);
                    }
                }

                BlockSand.fallInstantly = false;
                this.worldObj.getServer().getPluginManager().callEvent(new org.bukkit.event.world.ChunkPopulateEvent(chunk.bukkitChunk));
                // CraftBukkit end
                GameRegistry.generateWorld(par2, par3, this.worldObj, this.currentChunkProvider, par1IChunkProvider); // Forge
                chunk.setChunkModified();
            }
        }
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(final boolean par1, final IProgressUpdate par2IProgressUpdate)
    {
        int i = 0;
        // Cauldron start -- use thread-safe method for iterating loaded chunks
        final Object[] chunks = this.loadedChunks.toArray();

        for (int j = 0; j < chunks.length; ++j)
        {
            final Chunk chunk = (Chunk)chunks[j];
            //Cauldron end
            if (par1)
            {
                this.safeSaveExtraChunkData(chunk);
            }

            if (chunk.needsSaving(par1))
            {
                this.safeSaveChunk(chunk);
                chunk.isModified = false;
                ++i;

                if (i == 24 && !par1)
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData()
    {
        if (this.currentChunkLoader != null)
        {
            this.currentChunkLoader.saveExtraData();
        }
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        if (!this.worldObj.canNotSave)
        {
            // Cauldron start - remove any chunk that has a ticket associated with it
            if (!this.chunksToUnload.isEmpty())
            {
                for (final ChunkCoordIntPair forcedChunk : this.worldObj.getPersistentChunks().keys())
                {
                    this.chunksToUnload.remove(forcedChunk.chunkXPos, forcedChunk.chunkZPos);
                }
            }
            // Cauldron end        
            // CraftBukkit start
            final Server server = this.worldObj.getServer();

            for (int i = 0; i < 100 && !this.chunksToUnload.isEmpty(); i++)
            {
                final long chunkcoordinates = this.chunksToUnload.popFirst();
                final Chunk chunk = this.loadedChunkHashMap.get(chunkcoordinates);

                if (chunk == null)
                {
                    continue;
                }

                final ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk);
                server.getPluginManager().callEvent(event);

                if (!event.isCancelled())
                {
                    CauldronHooks.logChunkUnload(this, chunk.xPosition, chunk.zPosition, "Unloading Chunk at");

                    chunk.onChunkUnload();
                    this.safeSaveChunk(chunk);
                    this.safeSaveExtraChunkData(chunk);
                    // this.unloadQueue.remove(integer);
                    this.loadedChunkHashMap.remove(chunkcoordinates); // CraftBukkit
                    loadedChunks.remove(chunk); // Cauldron - vanilla compatibility   
                    ForgeChunkManager.putDormantChunk(chunkcoordinates, chunk);

                    if (this.loadedChunkHashMap.isEmpty() && ForgeChunkManager.getPersistentChunksFor(this.worldObj).isEmpty() && !DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId))
                    {
                        DimensionManager.unloadWorld(this.worldObj.provider.dimensionId); // Cauldron - unload the dimension
                        return this.currentChunkProvider.unloadQueuedChunks();
                    }
                }
            }

            // CraftBukkit end

            if (this.currentChunkLoader != null)
            {
                this.currentChunkLoader.chunkTick();
            }
        }

        return this.currentChunkProvider.unloadQueuedChunks();
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return !this.worldObj.canNotSave;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "ServerChunkCache: " + this.loadedChunkHashMap.values().size() + " Drop: " + this.chunksToUnload.size(); // CraftBukkit
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures(final EnumCreatureType par1EnumCreatureType, final int par2, final int par3, final int par4)
    {
        return this.currentChunkProvider.getPossibleCreatures(par1EnumCreatureType, par2, par3, par4);
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition findClosestStructure(final World par1World, final String par2Str, final int par3, final int par4, final int par5)
    {
        return this.currentChunkProvider.findClosestStructure(par1World, par2Str, par3, par4, par5);
    }

    public int getLoadedChunkCount()
    {
        return this.loadedChunkHashMap.values().size(); // CraftBukkit
    }

    public void recreateStructures(final int par1, final int par2) {}

    // Cauldron start
    private boolean shouldLoadChunk()
    {
        return this.worldObj.findingSpawnPoint ||
                this.loadChunkOnProvideRequest ||
                (MinecraftServer.callingForgeTick && MinecraftServer.getServer().cauldronConfig.loadChunkOnForgeTick.getValue()) ||
                (MinecraftServer.currentTick - initialTick <= 100);
    }

    public long lastAccessed(final int x, final int z)
    {
        final long chunkHash = LongHash.toLong(x, z);
        if (!loadedChunkHashMap.containsKey(chunkHash)) return 0;
        return loadedChunkHashMap.get(chunkHash).lastAccessedTick;
    }
    // Cauldron end
}
