package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTBase
{
    /** The double value for the tag. */
    public double data;

    public NBTTagDouble(final String par1Str)
    {
        super(par1Str);
    }

    public NBTTagDouble(final String par1Str, final double par2)
    {
        super(par1Str);
        this.data = par2;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeDouble(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(final DataInput par1DataInput, final int par2) throws IOException
    {
        this.data = par1DataInput.readDouble();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)6;
    }

    public String toString()
    {
        return "" + this.data;
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagDouble(this.getName(), this.data);
    }

    public boolean equals(final Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            final NBTTagDouble nbttagdouble = (NBTTagDouble)par1Obj;
            return this.data == nbttagdouble.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        final long i = Double.doubleToLongBits(this.data);
        return super.hashCode() ^ (int)(i ^ i >>> 32);
    }
}
