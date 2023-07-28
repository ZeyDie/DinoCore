package net.minecraft.server.management;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import java.util.Comparator;

public class PlayerPositionComparator implements Comparator
{
    private final ChunkCoordinates theChunkCoordinates;

    public PlayerPositionComparator(final ChunkCoordinates par1ChunkCoordinates)
    {
        this.theChunkCoordinates = par1ChunkCoordinates;
    }

    /**
     * Compare the position of two players.
     */
    public int comparePlayers(final EntityPlayerMP par1EntityPlayerMP, final EntityPlayerMP par2EntityPlayerMP)
    {
        final double d0 = par1EntityPlayerMP.getDistanceSq((double)this.theChunkCoordinates.posX, (double)this.theChunkCoordinates.posY, (double)this.theChunkCoordinates.posZ);
        final double d1 = par2EntityPlayerMP.getDistanceSq((double)this.theChunkCoordinates.posX, (double)this.theChunkCoordinates.posY, (double)this.theChunkCoordinates.posZ);
        return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
    }

    public int compare(final Object par1Obj, final Object par2Obj)
    {
        return this.comparePlayers((EntityPlayerMP)par1Obj, (EntityPlayerMP)par2Obj);
    }
}
