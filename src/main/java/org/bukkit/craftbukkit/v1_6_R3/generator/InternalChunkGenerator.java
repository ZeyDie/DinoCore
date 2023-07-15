package org.bukkit.craftbukkit.v1_6_R3.generator;

import org.bukkit.generator.ChunkGenerator;

// Do not implement functions to this class, add to NormalChunkGenerator
public abstract class InternalChunkGenerator extends ChunkGenerator implements net.minecraft.world.chunk.IChunkProvider {
    public void saveExtraData() {}
}
