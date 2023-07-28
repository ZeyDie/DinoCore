package net.minecraft.village;

public class VillageDoorInfo
{
    public final int posX;
    public final int posY;
    public final int posZ;
    public final int insideDirectionX;
    public final int insideDirectionZ;
    public int lastActivityTimestamp;
    public boolean isDetachedFromVillageFlag;
    private int doorOpeningRestrictionCounter;

    public VillageDoorInfo(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        this.posX = par1;
        this.posY = par2;
        this.posZ = par3;
        this.insideDirectionX = par4;
        this.insideDirectionZ = par5;
        this.lastActivityTimestamp = par6;
    }

    /**
     * Returns the squared distance between this door and the given coordinate.
     */
    public int getDistanceSquared(final int par1, final int par2, final int par3)
    {
        final int l = par1 - this.posX;
        final int i1 = par2 - this.posY;
        final int j1 = par3 - this.posZ;
        return l * l + i1 * i1 + j1 * j1;
    }

    /**
     * Get the square of the distance from a location 2 blocks away from the door considered 'inside' and the given
     * arguments
     */
    public int getInsideDistanceSquare(final int par1, final int par2, final int par3)
    {
        final int l = par1 - this.posX - this.insideDirectionX;
        final int i1 = par2 - this.posY;
        final int j1 = par3 - this.posZ - this.insideDirectionZ;
        return l * l + i1 * i1 + j1 * j1;
    }

    public int getInsidePosX()
    {
        return this.posX + this.insideDirectionX;
    }

    public int getInsidePosY()
    {
        return this.posY;
    }

    public int getInsidePosZ()
    {
        return this.posZ + this.insideDirectionZ;
    }

    public boolean isInside(final int par1, final int par2)
    {
        final int k = par1 - this.posX;
        final int l = par2 - this.posZ;
        return k * this.insideDirectionX + l * this.insideDirectionZ >= 0;
    }

    public void resetDoorOpeningRestrictionCounter()
    {
        this.doorOpeningRestrictionCounter = 0;
    }

    public void incrementDoorOpeningRestrictionCounter()
    {
        ++this.doorOpeningRestrictionCounter;
    }

    public int getDoorOpeningRestrictionCounter()
    {
        return this.doorOpeningRestrictionCounter;
    }
}
