package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Vec3
{
    /**
     * A global Vec3Pool that always creates new vectors instead of reusing them and is thread-safe.
     */
    public static final Vec3Pool fakePool = new Vec3Pool(-1, -1);
    public final Vec3Pool myVec3LocalPool;

    /** X coordinate of Vec3D */
    public double xCoord;

    /** Y coordinate of Vec3D */
    public double yCoord;

    /** Z coordinate of Vec3D */
    public double zCoord;
    public Vec3 next; // CraftBukkit

    /**
     * Static method for creating a new Vec3D given the three x,y,z values. This is only called from the other static
     * method which creates and places it in the list.
     */
    public static Vec3 createVectorHelper(final double par0, final double par2, final double par4)
    {
        return new Vec3(fakePool, par0, par2, par4);
    }

    protected Vec3(final Vec3Pool par1Vec3Pool, double par2, double par4, double par6)
    {
        double par21 = par2;
        double par41 = par4;
        double par61 = par6;
        if (par21 == -0.0D)
        {
            par21 = 0.0D;
        }

        if (par41 == -0.0D)
        {
            par41 = 0.0D;
        }

        if (par61 == -0.0D)
        {
            par61 = 0.0D;
        }

        this.xCoord = par21;
        this.yCoord = par41;
        this.zCoord = par61;
        this.myVec3LocalPool = par1Vec3Pool;
    }

    /**
     * Sets the x,y,z components of the vector as specified.
     */
    protected Vec3 setComponents(final double par1, final double par3, final double par5)
    {
        this.xCoord = par1;
        this.yCoord = par3;
        this.zCoord = par5;
        return this;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns a new vector with the result of the specified vector minus this.
     */
    public Vec3 subtract(final Vec3 par1Vec3)
    {
        return this.myVec3LocalPool.getVecFromPool(par1Vec3.xCoord - this.xCoord, par1Vec3.yCoord - this.yCoord, par1Vec3.zCoord - this.zCoord);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public Vec3 normalize()
    {
        final double d0 = (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        return d0 < 1.0E-4D ? this.myVec3LocalPool.getVecFromPool(0.0D, 0.0D, 0.0D) : this.myVec3LocalPool.getVecFromPool(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
    }

    public double dotProduct(final Vec3 par1Vec3)
    {
        return this.xCoord * par1Vec3.xCoord + this.yCoord * par1Vec3.yCoord + this.zCoord * par1Vec3.zCoord;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns a new vector with the result of this vector x the specified vector.
     */
    public Vec3 crossProduct(final Vec3 par1Vec3)
    {
        return this.myVec3LocalPool.getVecFromPool(this.yCoord * par1Vec3.zCoord - this.zCoord * par1Vec3.yCoord, this.zCoord * par1Vec3.xCoord - this.xCoord * par1Vec3.zCoord, this.xCoord * par1Vec3.yCoord - this.yCoord * par1Vec3.xCoord);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this
     * vector.
     */
    public Vec3 addVector(final double par1, final double par3, final double par5)
    {
        return this.myVec3LocalPool.getVecFromPool(this.xCoord + par1, this.yCoord + par3, this.zCoord + par5);
    }

    /**
     * Euclidean distance between this and the specified vector, returned as double.
     */
    public double distanceTo(final Vec3 par1Vec3)
    {
        final double d0 = par1Vec3.xCoord - this.xCoord;
        final double d1 = par1Vec3.yCoord - this.yCoord;
        final double d2 = par1Vec3.zCoord - this.zCoord;
        return (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareDistanceTo(final Vec3 par1Vec3)
    {
        final double d0 = par1Vec3.xCoord - this.xCoord;
        final double d1 = par1Vec3.yCoord - this.yCoord;
        final double d2 = par1Vec3.zCoord - this.zCoord;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * The square of the Euclidean distance between this and the vector of x,y,z components passed in.
     */
    public double squareDistanceTo(final double par1, final double par3, final double par5)
    {
        final double d3 = par1 - this.xCoord;
        final double d4 = par3 - this.yCoord;
        final double d5 = par5 - this.zCoord;
        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    /**
     * Returns the length of the vector.
     */
    public double lengthVector()
    {
        return (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithXValue(final Vec3 par1Vec3, final double par2)
    {
        final double d1 = par1Vec3.xCoord - this.xCoord;
        final double d2 = par1Vec3.yCoord - this.yCoord;
        final double d3 = par1Vec3.zCoord - this.zCoord;

        if (d1 * d1 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            final double d4 = (par2 - this.xCoord) / d1;
            return d4 >= 0.0D && d4 <= 1.0D ? this.myVec3LocalPool.getVecFromPool(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithYValue(final Vec3 par1Vec3, final double par2)
    {
        final double d1 = par1Vec3.xCoord - this.xCoord;
        final double d2 = par1Vec3.yCoord - this.yCoord;
        final double d3 = par1Vec3.zCoord - this.zCoord;

        if (d2 * d2 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            final double d4 = (par2 - this.yCoord) / d2;
            return d4 >= 0.0D && d4 <= 1.0D ? this.myVec3LocalPool.getVecFromPool(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithZValue(final Vec3 par1Vec3, final double par2)
    {
        final double d1 = par1Vec3.xCoord - this.xCoord;
        final double d2 = par1Vec3.yCoord - this.yCoord;
        final double d3 = par1Vec3.zCoord - this.zCoord;

        if (d3 * d3 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            final double d4 = (par2 - this.zCoord) / d3;
            return d4 >= 0.0D && d4 <= 1.0D ? this.myVec3LocalPool.getVecFromPool(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }

    public String toString()
    {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }

    /**
     * Rotates the vector around the x axis by the specified angle.
     */
    public void rotateAroundX(final float par1)
    {
        final float f1 = MathHelper.cos(par1);
        final float f2 = MathHelper.sin(par1);
        final double d0 = this.xCoord;
        final double d1 = this.yCoord * (double)f1 + this.zCoord * (double)f2;
        final double d2 = this.zCoord * (double)f1 - this.yCoord * (double)f2;
        this.xCoord = d0;
        this.yCoord = d1;
        this.zCoord = d2;
    }

    /**
     * Rotates the vector around the y axis by the specified angle.
     */
    public void rotateAroundY(final float par1)
    {
        final float f1 = MathHelper.cos(par1);
        final float f2 = MathHelper.sin(par1);
        final double d0 = this.xCoord * (double)f1 + this.zCoord * (double)f2;
        final double d1 = this.yCoord;
        final double d2 = this.zCoord * (double)f1 - this.xCoord * (double)f2;
        this.xCoord = d0;
        this.yCoord = d1;
        this.zCoord = d2;
    }
    public Vec3 func_72439_b_CodeFix_Public(final double a, final double b, final double c){
        return setComponents(a, b, c);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Rotates the vector around the z axis by the specified angle.
     */
    public void rotateAroundZ(final float par1)
    {
        final float f1 = MathHelper.cos(par1);
        final float f2 = MathHelper.sin(par1);
        final double d0 = this.xCoord * (double)f1 + this.yCoord * (double)f2;
        final double d1 = this.yCoord * (double)f1 - this.xCoord * (double)f2;
        final double d2 = this.zCoord;
        this.xCoord = d0;
        this.yCoord = d1;
        this.zCoord = d2;
    }
}
