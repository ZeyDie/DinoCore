package net.minecraft.util;

class LongHashMapEntry
{
    /**
     * the key as a long (for playerInstances it is the x in the most significant 32 bits and then y)
     */
    final long key;

    /** the value held by the hash at the specified key */
    Object value;

    /** the next hashentry in the table */
    LongHashMapEntry nextEntry;
    final int hash;

    LongHashMapEntry(final int par1, final long par2, final Object par4Obj, final LongHashMapEntry par5LongHashMapEntry)
    {
        this.value = par4Obj;
        this.nextEntry = par5LongHashMapEntry;
        this.key = par2;
        this.hash = par1;
    }

    public final long getKey()
    {
        return this.key;
    }

    public final Object getValue()
    {
        return this.value;
    }

    public final boolean equals(final Object par1Obj)
    {
        if (!(par1Obj instanceof LongHashMapEntry))
        {
            return false;
        }
        else
        {
            final LongHashMapEntry longhashmapentry = (LongHashMapEntry)par1Obj;
            final Long olong = Long.valueOf(this.getKey());
            final Long olong1 = Long.valueOf(longhashmapentry.getKey());

            if (olong == olong1 || olong != null && olong.equals(olong1))
            {
                final Object object1 = this.getValue();
                final Object object2 = longhashmapentry.getValue();

                if (object1 == object2 || object1 != null && object1.equals(object2))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public final int hashCode()
    {
        return LongHashMap.getHashCode(this.key);
    }

    public final String toString()
    {
        return this.getKey() + "=" + this.getValue();
    }
}
