package net.minecraft.world;

import net.minecraft.block.Block;

public class NextTickListEntry implements Comparable
{
    /** The id number for the next tick entry */
    private static long nextTickEntryID;

    /** X position this tick is occuring at */
    public int xCoord;

    /** Y position this tick is occuring at */
    public int yCoord;

    /** Z position this tick is occuring at */
    public int zCoord;

    /**
     * blockID of the scheduled tick (ensures when the tick occurs its still for this block)
     */
    public int blockID;

    /** Time this tick is scheduled to occur at */
    public long scheduledTime;
    public int priority;

    /** The id of the tick entry */
    private long tickEntryID;

    public NextTickListEntry(final int par1, final int par2, final int par3, final int par4)
    {
        this.tickEntryID = (long)(nextTickEntryID++);
        this.xCoord = par1;
        this.yCoord = par2;
        this.zCoord = par3;
        this.blockID = par4;
    }

    public boolean equals(final Object par1Obj)
    {
        if (!(par1Obj instanceof NextTickListEntry))
        {
            return false;
        }
        else
        {
            final NextTickListEntry nextticklistentry = (NextTickListEntry)par1Obj;
            return this.xCoord == nextticklistentry.xCoord && this.yCoord == nextticklistentry.yCoord && this.zCoord == nextticklistentry.zCoord && Block.isAssociatedBlockID(this.blockID, nextticklistentry.blockID);
        }
    }

    public int hashCode()
    {
        return (this.xCoord * 257) ^ this.yCoord ^ (this.zCoord * 60217); // Spigot - better hash
    }

    /**
     * Sets the scheduled time for this tick entry
     */
    public NextTickListEntry setScheduledTime(final long par1)
    {
        this.scheduledTime = par1;
        return this;
    }

    public void setPriority(final int par1)
    {
        this.priority = par1;
    }

    /**
     * Compares this tick entry to another tick entry for sorting purposes. Compared first based on the scheduled time
     * and second based on tickEntryID.
     */
    public int comparer(final NextTickListEntry par1NextTickListEntry)
    {
        return this.scheduledTime < par1NextTickListEntry.scheduledTime ? -1 : (this.scheduledTime > par1NextTickListEntry.scheduledTime ? 1 : (this.priority != par1NextTickListEntry.priority ? this.priority - par1NextTickListEntry.priority : (this.tickEntryID < par1NextTickListEntry.tickEntryID ? -1 : (this.tickEntryID > par1NextTickListEntry.tickEntryID ? 1 : 0))));
    }

    public String toString()
    {
        return this.blockID + ": (" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + "), " + this.scheduledTime + ", " + this.priority + ", " + this.tickEntryID;
    }

    public int compareTo(final Object par1Obj)
    {
        return this.comparer((NextTickListEntry)par1Obj);
    }
}
