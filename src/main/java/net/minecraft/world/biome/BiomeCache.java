package net.minecraft.world.biome;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

import java.util.ArrayList;
import java.util.List;

public class BiomeCache
{
    /** Reference to the WorldChunkManager */
    private final WorldChunkManager chunkManager;

    /** The last time this BiomeCache was cleaned, in milliseconds. */
    private long lastCleanupTime;

    /**
     * The map of keys to BiomeCacheBlocks. Keys are based on the chunk x, z coordinates as (x | z << 32).
     */
    private LongHashMap cacheMap = new LongHashMap();

    /** The list of cached BiomeCacheBlocks */
    private List cache = new ArrayList();

    public BiomeCache(final WorldChunkManager par1WorldChunkManager)
    {
        this.chunkManager = par1WorldChunkManager;
    }

    /**
     * Returns a biome cache block at location specified.
     */
    public BiomeCacheBlock getBiomeCacheBlock(int par1, int par2)
    {
        int par11 = par1;
        int par21 = par2;
        par11 >>= 4;
        par21 >>= 4;
        final long k = (long) par11 & 4294967295L | ((long) par21 & 4294967295L) << 32;
        BiomeCacheBlock biomecacheblock = (BiomeCacheBlock)this.cacheMap.getValueByKey(k);

        if (biomecacheblock == null)
        {
            biomecacheblock = new BiomeCacheBlock(this, par11, par21);
            this.cacheMap.add(k, biomecacheblock);
            this.cache.add(biomecacheblock);
        }

        biomecacheblock.lastAccessTime = MinecraftServer.getSystemTimeMillis();
        return biomecacheblock;
    }

    /**
     * Returns the BiomeGenBase related to the x, z position from the cache.
     */
    public BiomeGenBase getBiomeGenAt(final int par1, final int par2)
    {
        return this.getBiomeCacheBlock(par1, par2).getBiomeGenAt(par1, par2);
    }

    /**
     * Removes BiomeCacheBlocks from this cache that haven't been accessed in at least 30 seconds.
     */
    public void cleanupCache()
    {
        final long i = MinecraftServer.getSystemTimeMillis();
        final long j = i - this.lastCleanupTime;

        if (j > 7500L || j < 0L)
        {
            this.lastCleanupTime = i;

            for (int k = 0; k < this.cache.size(); ++k)
            {
                final BiomeCacheBlock biomecacheblock = (BiomeCacheBlock)this.cache.get(k);
                final long l = i - biomecacheblock.lastAccessTime;

                if (l > 30000L || l < 0L)
                {
                    this.cache.remove(k--);
                    final long i1 = (long)biomecacheblock.xPosition & 4294967295L | ((long)biomecacheblock.zPosition & 4294967295L) << 32;
                    this.cacheMap.remove(i1);
                }
            }
        }
    }

    /**
     * Returns the array of cached biome types in the BiomeCacheBlock at the given location.
     */
    public BiomeGenBase[] getCachedBiomes(final int par1, final int par2)
    {
        return this.getBiomeCacheBlock(par1, par2).biomes;
    }

    /**
     * Get the world chunk manager object for a biome list.
     */
    static WorldChunkManager getChunkManager(final BiomeCache par0BiomeCache)
    {
        return par0BiomeCache.chunkManager;
    }
}
