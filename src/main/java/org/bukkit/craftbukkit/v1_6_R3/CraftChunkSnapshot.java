package org.bukkit.craftbukkit.v1_6_R3;

import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_6_R3.block.CraftBlock;


/**
 * Represents a static, thread-safe snapshot of chunk of blocks
 * Purpose is to allow clean, efficient copy of a chunk data to be made, and then handed off for processing in another thread (e.g. map rendering)
 */
public class CraftChunkSnapshot implements ChunkSnapshot {
    private final int x, z;
    private final String worldname;
    private final short[][] blockids; /* Block IDs, by section */
    private final byte[][] blockdata;
    private final byte[][] skylight;
    private final byte[][] emitlight;
    private final boolean[] empty;
    private final int[] hmap; // Height map
    private final long captureFulltime;
    private final net.minecraft.world.biome.BiomeGenBase[] biome;
    private final double[] biomeTemp;
    private final double[] biomeRain;

    CraftChunkSnapshot(final int x, final int z, final String wname, final long wtime, final short[][] sectionBlockIDs, final byte[][] sectionBlockData, final byte[][] sectionSkyLights, final byte[][] sectionEmitLights, final boolean[] sectionEmpty, final int[] hmap, final net.minecraft.world.biome.BiomeGenBase[] biome, final double[] biomeTemp, final double[] biomeRain) {
        this.x = x;
        this.z = z;
        this.worldname = wname;
        this.captureFulltime = wtime;
        this.blockids = sectionBlockIDs;
        this.blockdata = sectionBlockData;
        this.skylight = sectionSkyLights;
        this.emitlight = sectionEmitLights;
        this.empty = sectionEmpty;
        this.hmap = hmap;
        this.biome = biome;
        this.biomeTemp = biomeTemp;
        this.biomeRain = biomeRain;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public String getWorldName() {
        return worldname;
    }

    public final int getBlockTypeId(final int x, final int y, final int z) {
        return blockids[y >> 4][((y & 0xF) << 8) | (z << 4) | x];
    }

    public final int getBlockData(final int x, final int y, final int z) {
        final int off = ((y & 0xF) << 7) | (z << 3) | (x >> 1);
        return (blockdata[y >> 4][off] >> ((x & 1) << 2)) & 0xF;
    }

    public final int getBlockSkyLight(final int x, final int y, final int z) {
        final int off = ((y & 0xF) << 7) | (z << 3) | (x >> 1);
        return (skylight[y >> 4][off] >> ((x & 1) << 2)) & 0xF;
    }

    public final int getBlockEmittedLight(final int x, final int y, final int z) {
        final int off = ((y & 0xF) << 7) | (z << 3) | (x >> 1);
        return (emitlight[y >> 4][off] >> ((x & 1) << 2)) & 0xF;
    }

    public final int getHighestBlockYAt(final int x, final int z) {
        return hmap[z << 4 | x];
    }

    public final Biome getBiome(final int x, final int z) {
        return CraftBlock.biomeBaseToBiome(biome[z << 4 | x]);
    }

    public final double getRawBiomeTemperature(final int x, final int z) {
        return biomeTemp[z << 4 | x];
    }

    public final double getRawBiomeRainfall(final int x, final int z) {
        return biomeRain[z << 4 | x];
    }

    public final long getCaptureFullTime() {
        return captureFulltime;
    }

    public final boolean isSectionEmpty(final int sy) {
        return empty[sy];
    }
}
