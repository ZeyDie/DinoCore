package net.minecraft.world.chunk.storage;

import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.io.IOException;

public interface IChunkLoader
{
    /**
     * Loads the specified(XZ) chunk into the specified world.
     */
    Chunk loadChunk(World world, int i, int j) throws IOException;

    void saveChunk(World world, Chunk chunk) throws MinecraftException, IOException;

    /**
     * Save extra data associated with this Chunk not normally saved during autosave, only during chunk unload.
     * Currently unused.
     */
    void saveExtraChunkData(World world, Chunk chunk);

    /**
     * Called every World.tick()
     */
    void chunkTick();

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unused.
     */
    void saveExtraData();
}
