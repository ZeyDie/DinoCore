package net.minecraft.util;

public class AxisAlignedBB
{
    /** ThreadLocal AABBPool */
    private static final ThreadLocal theAABBLocalPool = new AABBLocalPool();
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    /**
     * Returns a bounding box with the specified bounds. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public static AxisAlignedBB getBoundingBox(final double par0, final double par2, final double par4, final double par6, final double par8, final double par10)
    {
        return new AxisAlignedBB(par0, par2, par4, par6, par8, par10);
    }

    /**
     * Gets the ThreadLocal AABBPool
     */
    public static AABBPool getAABBPool()
    {
        return (AABBPool)theAABBLocalPool.get();
    }

    protected AxisAlignedBB(final double par1, final double par3, final double par5, final double par7, final double par9, final double par11)
    {
        this.minX = par1;
        this.minY = par3;
        this.minZ = par5;
        this.maxX = par7;
        this.maxY = par9;
        this.maxZ = par11;
    }

    /**
     * Sets the bounds of the bounding box. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public AxisAlignedBB setBounds(final double par1, final double par3, final double par5, final double par7, final double par9, final double par11)
    {
        this.minX = par1;
        this.minY = par3;
        this.minZ = par5;
        this.maxX = par7;
        this.maxY = par9;
        this.maxZ = par11;
        return this;
    }

    /**
     * Adds the coordinates to the bounding box extending it if the point lies outside the current ranges. Args: x, y, z
     */
    public AxisAlignedBB addCoord(final double par1, final double par3, final double par5)
    {
        double d3 = this.minX;
        double d4 = this.minY;
        double d5 = this.minZ;
        double d6 = this.maxX;
        double d7 = this.maxY;
        double d8 = this.maxZ;

        if (par1 < 0.0D)
        {
            d3 += par1;
        }

        if (par1 > 0.0D)
        {
            d6 += par1;
        }

        if (par3 < 0.0D)
        {
            d4 += par3;
        }

        if (par3 > 0.0D)
        {
            d7 += par3;
        }

        if (par5 < 0.0D)
        {
            d5 += par5;
        }

        if (par5 > 0.0D)
        {
            d8 += par5;
        }

        return getAABBPool().getAABB(d3, d4, d5, d6, d7, d8);
    }

    /**
     * Returns a bounding box expanded by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    public AxisAlignedBB expand(final double par1, final double par3, final double par5)
    {
        final double d3 = this.minX - par1;
        final double d4 = this.minY - par3;
        final double d5 = this.minZ - par5;
        final double d6 = this.maxX + par1;
        final double d7 = this.maxY + par3;
        final double d8 = this.maxZ + par5;
        return getAABBPool().getAABB(d3, d4, d5, d6, d7, d8);
    }

    public AxisAlignedBB func_111270_a(final AxisAlignedBB par1AxisAlignedBB)
    {
        final double d0 = Math.min(this.minX, par1AxisAlignedBB.minX);
        final double d1 = Math.min(this.minY, par1AxisAlignedBB.minY);
        final double d2 = Math.min(this.minZ, par1AxisAlignedBB.minZ);
        final double d3 = Math.max(this.maxX, par1AxisAlignedBB.maxX);
        final double d4 = Math.max(this.maxY, par1AxisAlignedBB.maxY);
        final double d5 = Math.max(this.maxZ, par1AxisAlignedBB.maxZ);
        return getAABBPool().getAABB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Returns a bounding box offseted by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    public AxisAlignedBB getOffsetBoundingBox(final double par1, final double par3, final double par5)
    {
        return getAABBPool().getAABB(this.minX + par1, this.minY + par3, this.minZ + par5, this.maxX + par1, this.maxY + par3, this.maxZ + par5);
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateXOffset(final AxisAlignedBB paramAxisAlignedBB, double paramDouble) {
        double paramDouble1 = paramDouble;
        if (paramAxisAlignedBB.maxY <= this.minY || paramAxisAlignedBB.minY >= this.maxY)
            return paramDouble1;
        if (paramAxisAlignedBB.maxZ <= this.minZ || paramAxisAlignedBB.minZ >= this.maxZ)
            return paramDouble1;
        if (paramDouble1 > 0.0D && paramAxisAlignedBB.maxX <= this.minX) {
            final double d = this.minX - paramAxisAlignedBB.maxX;
            if (d < paramDouble1)
                paramDouble1 = d;
        }
        if (paramDouble1 < 0.0D && paramAxisAlignedBB.minX >= this.maxX) {
            final double d = this.maxX - paramAxisAlignedBB.minX;
            if (d > paramDouble1)
                paramDouble1 = d;
        }
        return paramDouble1;
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateYOffset(final AxisAlignedBB paramAxisAlignedBB, double paramDouble) {
        double paramDouble1 = paramDouble;
        if (paramAxisAlignedBB.maxX <= this.minX || paramAxisAlignedBB.minX >= this.maxX)
            return paramDouble1;
        if (paramAxisAlignedBB.maxZ <= this.minZ || paramAxisAlignedBB.minZ >= this.maxZ)
            return paramDouble1;
        if (paramDouble1 > 0.0D && paramAxisAlignedBB.maxY <= this.minY) {
            final double d = this.minY - paramAxisAlignedBB.maxY;
            if (d < paramDouble1)
                paramDouble1 = d;
        }
        if (paramDouble1 < 0.0D && paramAxisAlignedBB.minY >= this.maxY) {
            final double d = this.maxY - paramAxisAlignedBB.minY;
            if (d > paramDouble1)
                paramDouble1 = d;
        }
        return paramDouble1;
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateZOffset(final AxisAlignedBB paramAxisAlignedBB, double paramDouble) {
        double paramDouble1 = paramDouble;
        if (paramAxisAlignedBB.maxX <= this.minX || paramAxisAlignedBB.minX >= this.maxX)
            return paramDouble1;
        if (paramAxisAlignedBB.maxY <= this.minY || paramAxisAlignedBB.minY >= this.maxY)
            return paramDouble1;
        if (paramDouble1 > 0.0D && paramAxisAlignedBB.maxZ <= this.minZ) {
            final double d = this.minZ - paramAxisAlignedBB.maxZ;
            if (d < paramDouble1)
                paramDouble1 = d;
        }
        if (paramDouble1 < 0.0D && paramAxisAlignedBB.minZ >= this.maxZ) {
            final double d = this.maxZ - paramAxisAlignedBB.minZ;
            if (d > paramDouble1)
                paramDouble1 = d;
        }
        return paramDouble1;
    }

    /**
     * Returns whether the given bounding box intersects with this one. Args: axisAlignedBB
     */
    public boolean intersectsWith(final AxisAlignedBB par1AxisAlignedBB)
    {
        return (par1AxisAlignedBB.maxX > this.minX && par1AxisAlignedBB.minX < this.maxX) && ((par1AxisAlignedBB.maxY > this.minY && par1AxisAlignedBB.minY < this.maxY) && (par1AxisAlignedBB.maxZ > this.minZ && par1AxisAlignedBB.minZ < this.maxZ));
    }

    /**
     * Offsets the current bounding box by the specified coordinates. Args: x, y, z
     */
    public AxisAlignedBB offset(final double par1, final double par3, final double par5)
    {
        this.minX += par1;
        this.minY += par3;
        this.minZ += par5;
        this.maxX += par1;
        this.maxY += par3;
        this.maxZ += par5;
        return this;
    }

    /**
     * Returns if the supplied Vec3D is completely inside the bounding box
     */
    public boolean isVecInside(final Vec3 par1Vec3)
    {
        return (par1Vec3.xCoord > this.minX && par1Vec3.xCoord < this.maxX) && ((par1Vec3.yCoord > this.minY && par1Vec3.yCoord < this.maxY) && (par1Vec3.zCoord > this.minZ && par1Vec3.zCoord < this.maxZ));
    }

    /**
     * Returns the average length of the edges of the bounding box.
     */
    public double getAverageEdgeLength()
    {
        final double d0 = this.maxX - this.minX;
        final double d1 = this.maxY - this.minY;
        final double d2 = this.maxZ - this.minZ;
        return (d0 + d1 + d2) / 3.0D;
    }

    /**
     * Returns a bounding box that is inset by the specified amounts
     */
    public AxisAlignedBB contract(final double par1, final double par3, final double par5)
    {
        final double d3 = this.minX + par1;
        final double d4 = this.minY + par3;
        final double d5 = this.minZ + par5;
        final double d6 = this.maxX - par1;
        final double d7 = this.maxY - par3;
        final double d8 = this.maxZ - par5;
        return getAABBPool().getAABB(d3, d4, d5, d6, d7, d8);
    }

    /**
     * Returns a copy of the bounding box.
     */
    public AxisAlignedBB copy()
    {
        return getAABBPool().getAABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public MovingObjectPosition calculateIntercept(final Vec3 par1Vec3, final Vec3 par2Vec3)
    {
        Vec3 vec32 = par1Vec3.getIntermediateWithXValue(par2Vec3, this.minX);
        Vec3 vec33 = par1Vec3.getIntermediateWithXValue(par2Vec3, this.maxX);
        Vec3 vec34 = par1Vec3.getIntermediateWithYValue(par2Vec3, this.minY);
        Vec3 vec35 = par1Vec3.getIntermediateWithYValue(par2Vec3, this.maxY);
        Vec3 vec36 = par1Vec3.getIntermediateWithZValue(par2Vec3, this.minZ);
        Vec3 vec37 = par1Vec3.getIntermediateWithZValue(par2Vec3, this.maxZ);

        if (!this.isVecInYZ(vec32))
        {
            vec32 = null;
        }

        if (!this.isVecInYZ(vec33))
        {
            vec33 = null;
        }

        if (!this.isVecInXZ(vec34))
        {
            vec34 = null;
        }

        if (!this.isVecInXZ(vec35))
        {
            vec35 = null;
        }

        if (!this.isVecInXY(vec36))
        {
            vec36 = null;
        }

        if (!this.isVecInXY(vec37))
        {
            vec37 = null;
        }

        Vec3 vec38 = null;

        if (vec32 != null) {
            vec38 = vec32;
        }

        if (vec33 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec33) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec33;
        }

        if (vec34 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec34) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec34;
        }

        if (vec35 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec35) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec35;
        }

        if (vec36 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec36) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec36;
        }

        if (vec37 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec37) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec37;
        }

        if (vec38 == null)
        {
            return null;
        }
        else
        {
            byte b0 = -1;

            if (vec38 == vec32)
            {
                b0 = 4;
            }

            if (vec38 == vec33)
            {
                b0 = 5;
            }

            if (vec38 == vec34)
            {
                b0 = 0;
            }

            if (vec38 == vec35)
            {
                b0 = 1;
            }

            if (vec38 == vec36)
            {
                b0 = 2;
            }

            if (vec38 == vec37)
            {
                b0 = 3;
            }

            return new MovingObjectPosition(0, 0, 0, b0, vec38);
        }
    }

    /**
     * Checks if the specified vector is within the YZ dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInYZ(final Vec3 par1Vec3)
    {
        return par1Vec3 != null && (par1Vec3.yCoord >= this.minY && par1Vec3.yCoord <= this.maxY && par1Vec3.zCoord >= this.minZ && par1Vec3.zCoord <= this.maxZ);
    }

    /**
     * Checks if the specified vector is within the XZ dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInXZ(final Vec3 par1Vec3)
    {
        return par1Vec3 != null && (par1Vec3.xCoord >= this.minX && par1Vec3.xCoord <= this.maxX && par1Vec3.zCoord >= this.minZ && par1Vec3.zCoord <= this.maxZ);
    }

    /**
     * Checks if the specified vector is within the XY dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInXY(final Vec3 par1Vec3)
    {
        return par1Vec3 != null && (par1Vec3.xCoord >= this.minX && par1Vec3.xCoord <= this.maxX && par1Vec3.yCoord >= this.minY && par1Vec3.yCoord <= this.maxY);
    }

    /**
     * Sets the bounding box to the same bounds as the bounding box passed in. Args: axisAlignedBB
     */
    public void setBB(final AxisAlignedBB par1AxisAlignedBB)
    {
        this.minX = par1AxisAlignedBB.minX;
        this.minY = par1AxisAlignedBB.minY;
        this.minZ = par1AxisAlignedBB.minZ;
        this.maxX = par1AxisAlignedBB.maxX;
        this.maxY = par1AxisAlignedBB.maxY;
        this.maxZ = par1AxisAlignedBB.maxZ;
    }

    public String toString()
    {
        final double d0 = this.maxX - this.minX;
        final double d1 = this.maxY - this.minY;
        final double d2 = this.maxZ - this.minZ;
        return "box (" + d0 + ", " + d2 + ", " + d1 + ") [" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
}
