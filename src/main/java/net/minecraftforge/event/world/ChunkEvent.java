package net.minecraftforge.event.world;

import net.minecraft.world.chunk.Chunk;

public class ChunkEvent extends WorldEvent
{
    private final Chunk chunk;
    
    public ChunkEvent(final Chunk chunk)
    {
        super(chunk.worldObj);
        this.chunk = chunk;
    }
    
    public Chunk getChunk()
    {
        return chunk;
    }
    
    public static class Load extends ChunkEvent
    {
        public Load(final Chunk chunk)
        {
            super(chunk);
        }
    }

    public static class Unload extends ChunkEvent
    {
        public Unload(final Chunk chunk)
        {
            super(chunk);
        }
    }
}
