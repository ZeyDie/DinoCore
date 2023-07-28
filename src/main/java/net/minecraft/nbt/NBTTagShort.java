package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase
{
    /** The short value for the tag. */
    public short data;

    public NBTTagShort(final String par1Str)
    {
        super(par1Str);
    }

    public NBTTagShort(final String par1Str, final short par2)
    {
        super(par1Str);
        this.data = par2;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeShort(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(final DataInput par1DataInput, final int par2) throws IOException
    {
        this.data = par1DataInput.readShort();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)2;
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
        return new NBTTagShort(this.getName(), this.data);
    }

    public boolean equals(final Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            final NBTTagShort nbttagshort = (NBTTagShort)par1Obj;
            return this.data == nbttagshort.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ this.data;
    }
}
