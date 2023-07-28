package net.minecraftforge.event.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;

public class ChunkDataEvent extends ChunkEvent
{
    private final NBTTagCompound data;

    public ChunkDataEvent(final Chunk chunk, final NBTTagCompound data)
    {
        super(chunk);
        this.data = data;
    }
    
    public NBTTagCompound getData()
    {
        return data;
    }
    
    public static class Load extends ChunkDataEvent
    {
        public Load(final Chunk chunk, final NBTTagCompound data)
        {
            super(chunk, data);
        }
    }

    public static class Save extends ChunkDataEvent
    {
        public Save(final Chunk chunk, final NBTTagCompound data)
        {
            super(chunk, data);
        }
    }
}
