package com.zeydie.legacy.core;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkCoordIntPair;

// CraftBukkit start - Sorter to load nearby chunks first
public final class ChunkCoordComparator implements java.util.Comparator<ChunkCoordIntPair> {
    private final int x;
    private final int z;

    public ChunkCoordComparator(final EntityPlayerMP entityplayer) {
        x = (int) entityplayer.posX >> 4;
        z = (int) entityplayer.posZ >> 4;
    }

    public int compare(final ChunkCoordIntPair a, final ChunkCoordIntPair b) {
        if (a.equals(b)) {
            return 0;
        }

        // Subtract current position to set center point
        final int ax = a.chunkXPos - this.x;
        final int az = a.chunkZPos - this.z;
        final int bx = b.chunkXPos - this.x;
        final int bz = b.chunkZPos - this.z;
        final int result = ((ax - bx) * (ax + bx)) + ((az - bz) * (az + bz));

        if (result != 0) {
            return result;
        }

        if (ax < 0) {
            if (bx < 0) {
                return bz - az;
            } else {
                return -1;
            }
        } else {
            if (bx < 0) {
                return 1;
            } else {
                return az - bz;
            }
        }
    }
}
// CraftBukkit end