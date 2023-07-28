package net.minecraft.world;

public class ChunkCoordIntPair
{
    /** The X position of this Chunk Coordinate Pair */
    public final int chunkXPos;

    /** The Z position of this Chunk Coordinate Pair */
    public final int chunkZPos;

    public ChunkCoordIntPair(final int par1, final int par2)
    {
        this.chunkXPos = par1;
        this.chunkZPos = par2;
    }

    /**
     * converts a chunk coordinate pair to an integer (suitable for hashing)
     */
    public static long chunkXZ2Int(final int par0, final int par1)
    {
        return (long)par0 & 4294967295L | ((long)par1 & 4294967295L) << 32;
    }

    public int hashCode()
    {
        final long i = chunkXZ2Int(this.chunkXPos, this.chunkZPos);
        final int j = (int)i;
        final int k = (int)(i >> 32);
        return j ^ k;
    }

    public boolean equals(final Object par1Obj)
    {
        final ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair)par1Obj;
        return chunkcoordintpair.chunkXPos == this.chunkXPos && chunkcoordintpair.chunkZPos == this.chunkZPos;
    }

    public int getCenterXPos()
    {
        return (this.chunkXPos << 4) + 8;
    }

    public int getCenterZPosition()
    {
        return (this.chunkZPos << 4) + 8;
    }

    public ChunkPosition getChunkPosition(final int par1)
    {
        return new ChunkPosition(this.getCenterXPos(), par1, this.getCenterZPosition());
    }

    public String toString()
    {
        return "[" + this.chunkXPos + ", " + this.chunkZPos + "]";
    }
}
