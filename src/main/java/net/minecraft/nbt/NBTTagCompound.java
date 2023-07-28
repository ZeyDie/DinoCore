package net.minecraft.nbt;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NBTTagCompound extends NBTBase
{
    /**
     * The key-value pairs for the tag. Each key is a UTF string, each value is a tag.
     */
    private Map tagMap = new HashMap();

    public NBTTagCompound()
    {
        super("");
    }

    public NBTTagCompound(final String par1Str)
    {
        super(par1Str);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(final DataOutput par1DataOutput) throws IOException
    {
        final Iterator iterator = this.tagMap.values().iterator();

        while (iterator.hasNext())
        {
            final NBTBase nbtbase = (NBTBase)iterator.next();
            NBTBase.writeNamedTag(nbtbase, par1DataOutput);
        }

        par1DataOutput.writeByte(0);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(final DataInput par1DataInput, final int par2) throws IOException
    {
        if (par2 > 512)
        {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        else
        {
            this.tagMap.clear();
            NBTBase nbtbase;

            while ((nbtbase = NBTBase.func_130104_b(par1DataInput, par2 + 1)).getId() != 0)
            {
                this.tagMap.put(nbtbase.getName(), nbtbase);
            }
        }
    }

    /**
     * Returns all the values in the tagMap HashMap.
     */
    public Collection getTags()
    {
        return this.tagMap.values();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)10;
    }

    /**
     * Stores the given tag into the map with the given string key. This is mostly used to store tag lists.
     */
    public void setTag(final String par1Str, final NBTBase par2NBTBase)
    {
        this.tagMap.put(par1Str, par2NBTBase.setName(par1Str));
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given string key.
     */
    public void setByte(final String par1Str, final byte par2)
    {
        this.tagMap.put(par1Str, new NBTTagByte(par1Str, par2));
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the given string key.
     */
    public void setShort(final String par1Str, final short par2)
    {
        this.tagMap.put(par1Str, new NBTTagShort(par1Str, par2));
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the given string key.
     */
    public void setInteger(final String par1Str, final int par2)
    {
        this.tagMap.put(par1Str, new NBTTagInt(par1Str, par2));
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given string key.
     */
    public void setLong(final String par1Str, final long par2)
    {
        this.tagMap.put(par1Str, new NBTTagLong(par1Str, par2));
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the given string key.
     */
    public void setFloat(final String par1Str, final float par2)
    {
        this.tagMap.put(par1Str, new NBTTagFloat(par1Str, par2));
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the given string key.
     */
    public void setDouble(final String par1Str, final double par2)
    {
        this.tagMap.put(par1Str, new NBTTagDouble(par1Str, par2));
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the given string key.
     */
    public void setString(final String par1Str, final String par2Str)
    {
        this.tagMap.put(par1Str, new NBTTagString(par1Str, par2Str));
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
     */
    public void setByteArray(final String par1Str, final byte[] par2ArrayOfByte)
    {
        this.tagMap.put(par1Str, new NBTTagByteArray(par1Str, par2ArrayOfByte));
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
     */
    public void setIntArray(final String par1Str, final int[] par2ArrayOfInteger)
    {
        this.tagMap.put(par1Str, new NBTTagIntArray(par1Str, par2ArrayOfInteger));
    }

    /**
     * Stores the given NBTTagCompound into the map with the given string key.
     */
    public void setCompoundTag(final String par1Str, final NBTTagCompound par2NBTTagCompound)
    {
        this.tagMap.put(par1Str, par2NBTTagCompound.setName(par1Str));
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
     */
    public void setBoolean(final String par1Str, final boolean par2)
    {
        this.setByte(par1Str, (byte)(par2 ? 1 : 0));
    }

    /**
     * gets a generic tag with the specified name
     */
    public NBTBase getTag(final String par1Str)
    {
        return (NBTBase)this.tagMap.get(par1Str);
    }

    /**
     * Returns whether the given string has been previously stored as a key in the map.
     */
    public boolean hasKey(final String par1Str)
    {
        return this.tagMap.containsKey(par1Str);
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was stored.
     */
    public byte getByte(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0 : ((NBTTagByte)this.tagMap.get(par1Str)).data;
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 1, classcastexception));
        }
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was stored.
     */
    public short getShort(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0 : ((NBTTagShort)this.tagMap.get(par1Str)).data;
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 2, classcastexception));
        }
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was stored.
     */
    public int getInteger(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0 : ((NBTTagInt)this.tagMap.get(par1Str)).data;
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 3, classcastexception));
        }
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was stored.
     */
    public long getLong(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0L : ((NBTTagLong)this.tagMap.get(par1Str)).data;
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 4, classcastexception));
        }
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was stored.
     */
    public float getFloat(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0.0F : ((NBTTagFloat)this.tagMap.get(par1Str)).data;
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 5, classcastexception));
        }
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was stored.
     */
    public double getDouble(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0.0D : ((NBTTagDouble)this.tagMap.get(par1Str)).data;
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 6, classcastexception));
        }
    }

    /**
     * Retrieves a string value using the specified key, or an empty string if no such key was stored.
     */
    public String getString(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? "" : ((NBTTagString)this.tagMap.get(par1Str)).data;
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 8, classcastexception));
        }
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
     */
    public byte[] getByteArray(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(par1Str)).byteArray;
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 7, classcastexception));
        }
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
     */
    public int[] getIntArray(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(par1Str)).intArray;
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 11, classcastexception));
        }
    }

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
     * stored.
     */
    public NBTTagCompound getCompoundTag(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new NBTTagCompound(par1Str) : (NBTTagCompound)this.tagMap.get(par1Str);
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 10, classcastexception));
        }
    }

    /**
     * Retrieves a NBTTagList subtag matching the specified key, or a new empty NBTTagList if no such key was stored.
     */
    public NBTTagList getTagList(final String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new NBTTagList(par1Str) : (NBTTagList)this.tagMap.get(par1Str);
        }
        catch (final ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 9, classcastexception));
        }
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
     * method.
     */
    public boolean getBoolean(final String par1Str)
    {
        return this.getByte(par1Str) != 0;
    }

    /**
     * Remove the specified tag.
     */
    public void removeTag(final String par1Str)
    {
        this.tagMap.remove(par1Str);
    }

    public String toString()
    {
        String s = this.getName() + ":[";
        String s1;

        for (final Iterator iterator = this.tagMap.keySet().iterator(); iterator.hasNext(); s = s + s1 + ":" + this.tagMap.get(s1) + ",")
        {
            s1 = (String)iterator.next();
        }

        return s + "]";
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags()
    {
        return this.tagMap.isEmpty();
    }

    /**
     * Create a crash report which indicates a NBT read error.
     */
    private CrashReport createCrashReport(final String par1Str, final int par2, final ClassCastException par3ClassCastException)
    {
        final CrashReport crashreport = CrashReport.makeCrashReport(par3ClassCastException, "Reading NBT data");
        final CrashReportCategory crashreportcategory = crashreport.makeCategoryDepth("Corrupt NBT tag", 1);
        crashreportcategory.addCrashSectionCallable("Tag type found", new CallableTagCompound1(this, par1Str));
        crashreportcategory.addCrashSectionCallable("Tag type expected", new CallableTagCompound2(this, par2));
        crashreportcategory.addCrashSection("Tag name", par1Str);

        if (this.getName() != null && !this.getName().isEmpty())
        {
            crashreportcategory.addCrashSection("Tag parent", this.getName());
        }

        return crashreport;
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        final NBTTagCompound nbttagcompound = new NBTTagCompound(this.getName());
        final Iterator iterator = this.tagMap.keySet().iterator();

        while (iterator.hasNext())
        {
            final String s = (String)iterator.next();
            nbttagcompound.setTag(s, ((NBTBase)this.tagMap.get(s)).copy());
        }

        return nbttagcompound;
    }

    public boolean equals(final Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            final NBTTagCompound nbttagcompound = (NBTTagCompound)par1Obj;
            return this.tagMap.entrySet().equals(nbttagcompound.tagMap.entrySet());
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

    /**
     * Return the tag map for this compound.
     */
    static Map getTagMap(final NBTTagCompound par0NBTTagCompound)
    {
        return par0NBTTagCompound.tagMap;
    }
}
