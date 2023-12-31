package net.minecraft.util;

public class ChunkCoordinates implements Comparable
{
    public int posX;

    /** the y coordinate */
    public int posY;

    /** the z coordinate */
    public int posZ;

    public ChunkCoordinates() {}

    public ChunkCoordinates(final int par1, final int par2, final int par3)
    {
        this.posX = par1;
        this.posY = par2;
        this.posZ = par3;
    }

    public ChunkCoordinates(final ChunkCoordinates par1ChunkCoordinates)
    {
        this.posX = par1ChunkCoordinates.posX;
        this.posY = par1ChunkCoordinates.posY;
        this.posZ = par1ChunkCoordinates.posZ;
    }

    public boolean equals(final Object par1Obj)
    {
        if (!(par1Obj instanceof ChunkCoordinates))
        {
            return false;
        }
        else
        {
            final ChunkCoordinates chunkcoordinates = (ChunkCoordinates)par1Obj;
            return this.posX == chunkcoordinates.posX && this.posY == chunkcoordinates.posY && this.posZ == chunkcoordinates.posZ;
        }
    }

    public int hashCode()
    {
        return this.posX + this.posZ << 8 + this.posY << 16;
    }

    /**
     * Compare the coordinate with another coordinate
     */
    public int compareChunkCoordinate(final ChunkCoordinates par1ChunkCoordinates)
    {
        return this.posY == par1ChunkCoordinates.posY ? (this.posZ == par1ChunkCoordinates.posZ ? this.posX - par1ChunkCoordinates.posX : this.posZ - par1ChunkCoordinates.posZ) : this.posY - par1ChunkCoordinates.posY;
    }

    public void set(final int par1, final int par2, final int par3)
    {
        this.posX = par1;
        this.posY = par2;
        this.posZ = par3;
    }

    /**
     * Returns the squared distance between this coordinates and the coordinates given as argument.
     */
    public float getDistanceSquared(final int par1, final int par2, final int par3)
    {
        final float f = (float)(this.posX - par1);
        final float f1 = (float)(this.posY - par2);
        final float f2 = (float)(this.posZ - par3);
        return f * f + f1 * f1 + f2 * f2;
    }

    /**
     * Return the squared distance between this coordinates and the ChunkCoordinates given as argument.
     */
    public float getDistanceSquaredToChunkCoordinates(final ChunkCoordinates par1ChunkCoordinates)
    {
        return this.getDistanceSquared(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY, par1ChunkCoordinates.posZ);
    }

    public int compareTo(final Object par1Obj)
    {
        return this.compareChunkCoordinate((ChunkCoordinates)par1Obj);
    }
}
