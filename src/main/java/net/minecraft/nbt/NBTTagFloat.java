package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTBase
{
    /** The float value for the tag. */
    public float data;

    public NBTTagFloat(final String par1Str)
    {
        super(par1Str);
    }

    public NBTTagFloat(final String par1Str, final float par2)
    {
        super(par1Str);
        this.data = par2;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeFloat(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(final DataInput par1DataInput, final int par2) throws IOException
    {
        this.data = par1DataInput.readFloat();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)5;
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
        return new NBTTagFloat(this.getName(), this.data);
    }

    public boolean equals(final Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            final NBTTagFloat nbttagfloat = (NBTTagFloat)par1Obj;
            return this.data == nbttagfloat.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ Float.floatToIntBits(this.data);
    }
}
