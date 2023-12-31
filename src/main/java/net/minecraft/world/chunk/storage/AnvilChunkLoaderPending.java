package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;

class AnvilChunkLoaderPending
{
    public final ChunkCoordIntPair chunkCoordinate;
    public final NBTTagCompound nbtTags;

    public AnvilChunkLoaderPending(final ChunkCoordIntPair par1ChunkCoordIntPair, final NBTTagCompound par2NBTTagCompound)
    {
        this.chunkCoordinate = par1ChunkCoordIntPair;
        this.nbtTags = par2NBTTagCompound;
    }
}
