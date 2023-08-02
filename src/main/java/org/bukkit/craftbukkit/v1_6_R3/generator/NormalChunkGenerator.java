package org.bukkit.craftbukkit.v1_6_R3.generator;

import net.minecraft.world.chunk.IChunkProvider;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NormalChunkGenerator extends InternalChunkGenerator {
    private final net.minecraft.world.chunk.IChunkProvider provider;

    public NormalChunkGenerator(final net.minecraft.world.World world, final long seed) {
        provider = world.provider.createChunkGenerator();
    }

    @Override
    public byte[] generate(final org.bukkit.World world, final Random random, final int x, final int z) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean canSpawn(final org.bukkit.World world, final int x, final int z) {
        return ((CraftWorld) world).getHandle().provider.canCoordinateBeSpawn(x, z);
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(final org.bukkit.World world) {
        return new ArrayList<BlockPopulator>();
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    @Override
    public boolean chunkExists(final int i, final int i1) {
        return provider.chunkExists(i, i1);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    @Override
    public net.minecraft.world.chunk.Chunk provideChunk(final int i, final int i1) {
        return provider.provideChunk(i, i1);
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    @Override
    public net.minecraft.world.chunk.Chunk loadChunk(final int i, final int i1) {
        return provider.loadChunk(i, i1);
    }

    /**
     * Populates chunk with ores etc etc
     */
    @Override
    public void populate(final net.minecraft.world.chunk.IChunkProvider icp, final int i, final int i1) {
        provider.populate(icp, i, i1);
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    @Override
    public boolean saveChunks(final boolean bln, final net.minecraft.util.IProgressUpdate ipu) {
        return provider.saveChunks(bln, ipu);
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    @Override
    public boolean unloadQueuedChunks() {
        return provider.unloadQueuedChunks();
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    @Override
    public boolean canSave() {
        return provider.canSave();
    }

    @Override
    public List<?> getPossibleCreatures(final net.minecraft.entity.EnumCreatureType ect, final int i, final int i1, final int i2) {
        return provider.getPossibleCreatures(ect, i, i1, i2);
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    @Override
    public net.minecraft.world.ChunkPosition findClosestStructure(final net.minecraft.world.World world, final String string, final int i, final int i1, final int i2) {
        return provider.findClosestStructure(world, string, i, i1, i2);
    }

    @Override
    public void recreateStructures(final int i, final int j) {
        provider.recreateStructures(i, j);
    }

    // n.m.s implementations always return 0. (The true implementation is in ChunkProviderServer)
    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    /**
     * Converts the instance data to a readable string.
     */
    @Override
    public String makeString() {
        return "NormalWorldGenerator";
    }

    public void func_104112_b() {}
    
    // Cauldron start - return vanilla compatible IChunkProvider for forge
    public IChunkProvider getForgeChunkProvider()
    {
        return this.provider;
    }
    // Cauldron end
}
