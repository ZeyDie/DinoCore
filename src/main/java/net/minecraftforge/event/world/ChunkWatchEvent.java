package net.minecraftforge.event.world;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.event.Event;

public class ChunkWatchEvent extends Event
{
    public final ChunkCoordIntPair chunk;
    public final EntityPlayerMP player;
    
    public ChunkWatchEvent(final ChunkCoordIntPair chunk, final EntityPlayerMP player)
    {
        this.chunk = chunk;
        this.player = player;
    }
    
    public static class Watch extends ChunkWatchEvent
    {
        public Watch(final ChunkCoordIntPair chunk, final EntityPlayerMP player) { super(chunk, player); }
    }
    
    public static class UnWatch extends ChunkWatchEvent
    {
        public UnWatch(final ChunkCoordIntPair chunkLocation, final EntityPlayerMP player) { super(chunkLocation, player); }
    }
}
