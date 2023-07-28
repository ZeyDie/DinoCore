package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint
{
    /** The x coordinate of this point */
    public final int xCoord;

    /** The y coordinate of this point */
    public final int yCoord;

    /** The z coordinate of this point */
    public final int zCoord;

    /** A hash of the coordinates used to identify this point */
    private final int hash;

    /** The index of this point in its assigned path */
    int index = -1;

    /** The distance along the path to this point */
    float totalPathDistance;

    /** The linear distance to the next point */
    float distanceToNext;

    /** The distance to the target */
    float distanceToTarget;

    /** The point preceding this in its assigned path */
    PathPoint previous;

    /** Indicates this is the origin */
    public boolean isFirst;

    public PathPoint(final int par1, final int par2, final int par3)
    {
        this.xCoord = par1;
        this.yCoord = par2;
        this.zCoord = par3;
        this.hash = makeHash(par1, par2, par3);
    }

    public static int makeHash(final int par0, final int par1, final int par2)
    {
        return par1 & 255 | (par0 & 32767) << 8 | (par2 & 32767) << 24 | (par0 < 0 ? Integer.MIN_VALUE : 0) | (par2 < 0 ? 32768 : 0);
    }

    /**
     * Returns the linear distance to another path point
     */
    public float distanceTo(final PathPoint par1PathPoint)
    {
        final float f = (float)(par1PathPoint.xCoord - this.xCoord);
        final float f1 = (float)(par1PathPoint.yCoord - this.yCoord);
        final float f2 = (float)(par1PathPoint.zCoord - this.zCoord);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

    public float func_75832_b(final PathPoint par1PathPoint)
    {
        final float f = (float)(par1PathPoint.xCoord - this.xCoord);
        final float f1 = (float)(par1PathPoint.yCoord - this.yCoord);
        final float f2 = (float)(par1PathPoint.zCoord - this.zCoord);
        return f * f + f1 * f1 + f2 * f2;
    }

    public boolean equals(final Object par1Obj)
    {
        if (!(par1Obj instanceof PathPoint))
        {
            return false;
        }
        else
        {
            final PathPoint pathpoint = (PathPoint)par1Obj;
            return this.hash == pathpoint.hash && this.xCoord == pathpoint.xCoord && this.yCoord == pathpoint.yCoord && this.zCoord == pathpoint.zCoord;
        }
    }

    public int hashCode()
    {
        return this.hash;
    }

    /**
     * Returns true if this point has already been assigned to a path
     */
    public boolean isAssigned()
    {
        return this.index >= 0;
    }

    public String toString()
    {
        return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }
}
