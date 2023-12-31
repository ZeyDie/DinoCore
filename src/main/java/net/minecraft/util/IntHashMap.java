package net.minecraft.util;

public class IntHashMap
{
    /** An array of HashEntries representing the heads of hash slot lists */
    private transient IntHashMapEntry[] slots = new IntHashMapEntry[16];

    /** The number of items stored in this map */
    private transient int count;

    /** The grow threshold */
    private int threshold = 12;

    /** The scale factor used to determine when to grow the table */
    private final float growFactor = 0.75F;

    /** A serial stamp used to mark changes */
    private transient volatile int versionStamp;

    /** The set of all the keys stored in this MCHash object */
    // private Set keySet = new HashSet(); // CraftBukkit - expensive and unused

    /**
     * Makes the passed in integer suitable for hashing by a number of shifts
     */
    private static int computeHash(int par0)
    {
        int par01 = par0;
        par01 ^= par01 >>> 20 ^ par01 >>> 12;
        return par01 ^ par01 >>> 7 ^ par01 >>> 4;
    }

    /**
     * Computes the index of the slot for the hash and slot count passed in.
     */
    private static int getSlotIndex(final int par0, final int par1)
    {
        return par0 & par1 - 1;
    }

    /**
     * Returns the object associated to a key
     */
    public Object lookup(final int par1)
    {
        final int j = computeHash(par1);

        for (IntHashMapEntry inthashmapentry = this.slots[getSlotIndex(j, this.slots.length)]; inthashmapentry != null; inthashmapentry = inthashmapentry.nextEntry)
        {
            if (inthashmapentry.hashEntry == par1)
            {
                return inthashmapentry.valueEntry;
            }
        }

        return null;
    }

    /**
     * Return true if an object is associated with the given key
     */
    public boolean containsItem(final int par1)
    {
        return this.lookupEntry(par1) != null;
    }

    /**
     * Returns the key/object mapping for a given key as a MCHashEntry
     */
    final IntHashMapEntry lookupEntry(final int par1)
    {
        final int j = computeHash(par1);

        for (IntHashMapEntry inthashmapentry = this.slots[getSlotIndex(j, this.slots.length)]; inthashmapentry != null; inthashmapentry = inthashmapentry.nextEntry)
        {
            if (inthashmapentry.hashEntry == par1)
            {
                return inthashmapentry;
            }
        }

        return null;
    }

    /**
     * Adds a key and associated value to this map
     */
    public void addKey(final int par1, final Object par2Obj)
    {
        // this.keySet.add(Integer.valueOf(par1)); // CraftBukkit
        final int j = computeHash(par1);
        final int k = getSlotIndex(j, this.slots.length);

        for (IntHashMapEntry inthashmapentry = this.slots[k]; inthashmapentry != null; inthashmapentry = inthashmapentry.nextEntry)
        {
            if (inthashmapentry.hashEntry == par1)
            {
                inthashmapentry.valueEntry = par2Obj;
                return;
            }
        }

        ++this.versionStamp;
        this.insert(j, par1, par2Obj, k);
    }

    /**
     * Increases the number of hash slots
     */
    private void grow(final int par1)
    {
        final IntHashMapEntry[] ainthashmapentry = this.slots;
        final int j = ainthashmapentry.length;

        if (j == 1073741824)
        {
            this.threshold = Integer.MAX_VALUE;
        }
        else
        {
            final IntHashMapEntry[] ainthashmapentry1 = new IntHashMapEntry[par1];
            this.copyTo(ainthashmapentry1);
            this.slots = ainthashmapentry1;
            this.threshold = (int)((float)par1 * this.growFactor);
        }
    }

    /**
     * Copies the hash slots to a new array
     */
    private void copyTo(final IntHashMapEntry[] par1ArrayOfIntHashMapEntry)
    {
        final IntHashMapEntry[] ainthashmapentry1 = this.slots;
        final int i = par1ArrayOfIntHashMapEntry.length;

        for (int j = 0; j < ainthashmapentry1.length; ++j)
        {
            IntHashMapEntry inthashmapentry = ainthashmapentry1[j];

            if (inthashmapentry != null)
            {
                ainthashmapentry1[j] = null;
                IntHashMapEntry inthashmapentry1;

                do
                {
                    inthashmapentry1 = inthashmapentry.nextEntry;
                    final int k = getSlotIndex(inthashmapentry.slotHash, i);
                    inthashmapentry.nextEntry = par1ArrayOfIntHashMapEntry[k];
                    par1ArrayOfIntHashMapEntry[k] = inthashmapentry;
                    inthashmapentry = inthashmapentry1;
                }
                while (inthashmapentry1 != null);
            }
        }
    }

    /**
     * Removes the specified object from the map and returns it
     */
    public Object removeObject(final int par1)
    {
        // this.keySet.remove(Integer.valueOf(par1)); // CraftBukkit
        final IntHashMapEntry inthashmapentry = this.removeEntry(par1);
        return inthashmapentry == null ? null : inthashmapentry.valueEntry;
    }

    /**
     * Removes the specified entry from the map and returns it
     */
    final IntHashMapEntry removeEntry(final int par1)
    {
        final int j = computeHash(par1);
        final int k = getSlotIndex(j, this.slots.length);
        IntHashMapEntry inthashmapentry = this.slots[k];
        IntHashMapEntry inthashmapentry1;
        IntHashMapEntry inthashmapentry2;

        for (inthashmapentry1 = inthashmapentry; inthashmapentry1 != null; inthashmapentry1 = inthashmapentry2)
        {
            inthashmapentry2 = inthashmapentry1.nextEntry;

            if (inthashmapentry1.hashEntry == par1)
            {
                ++this.versionStamp;
                --this.count;

                if (inthashmapentry == inthashmapentry1)
                {
                    this.slots[k] = inthashmapentry2;
                }
                else
                {
                    inthashmapentry.nextEntry = inthashmapentry2;
                }

                return inthashmapentry1;
            }

            inthashmapentry = inthashmapentry1;
        }

        return inthashmapentry1;
    }

    /**
     * Removes all entries from the map
     */
    public void clearMap()
    {
        ++this.versionStamp;
        final IntHashMapEntry[] ainthashmapentry = this.slots;

        for (int i = 0; i < ainthashmapentry.length; ++i)
        {
            ainthashmapentry[i] = null;
        }

        this.count = 0;
    }

    /**
     * Adds an object to a slot
     */
    private void insert(final int par1, final int par2, final Object par3Obj, final int par4)
    {
        final IntHashMapEntry inthashmapentry = this.slots[par4];
        this.slots[par4] = new IntHashMapEntry(par1, par2, par3Obj, inthashmapentry);

        if (this.count++ >= this.threshold)
        {
            this.grow(2 * this.slots.length);
        }
    }

    /**
     * Returns the hash code for a key
     */
    static int getHash(final int par0)
    {
        return computeHash(par0);
    }
}
