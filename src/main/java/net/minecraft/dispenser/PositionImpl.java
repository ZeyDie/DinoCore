package net.minecraft.dispenser;

public class PositionImpl implements IPosition
{
    protected final double x;
    protected final double y;
    protected final double z;

    public PositionImpl(final double par1, final double par3, final double par5)
    {
        this.x = par1;
        this.y = par3;
        this.z = par5;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getZ()
    {
        return this.z;
    }
}
