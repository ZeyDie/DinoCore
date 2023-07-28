package net.minecraft.block;

public class BlockEventData
{
    private int coordX;
    private int coordY;
    private int coordZ;
    private int blockID;

    /** Different for each blockID */
    private int eventID;

    /** Different for each blockID, eventID */
    private int eventParameter;

    public BlockEventData(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        this.coordX = par1;
        this.coordY = par2;
        this.coordZ = par3;
        this.eventID = par5;
        this.eventParameter = par6;
        this.blockID = par4;
    }

    /**
     * Get the X coordinate.
     */
    public int getX()
    {
        return this.coordX;
    }

    /**
     * Get the Y coordinate.
     */
    public int getY()
    {
        return this.coordY;
    }

    /**
     * Get the Z coordinate.
     */
    public int getZ()
    {
        return this.coordZ;
    }

    /**
     * Get the Event ID (different for each BlockID)
     */
    public int getEventID()
    {
        return this.eventID;
    }

    /**
     * Get the Event Parameter (different for each BlockID,EventID)
     */
    public int getEventParameter()
    {
        return this.eventParameter;
    }

    /**
     * Gets the BlockID for this BlockEventData
     */
    public int getBlockID()
    {
        return this.blockID;
    }

    public boolean equals(final Object par1Obj)
    {
        if (!(par1Obj instanceof BlockEventData))
        {
            return false;
        }
        else
        {
            final BlockEventData blockeventdata = (BlockEventData)par1Obj;
            return this.coordX == blockeventdata.coordX && this.coordY == blockeventdata.coordY && this.coordZ == blockeventdata.coordZ && this.eventID == blockeventdata.eventID && this.eventParameter == blockeventdata.eventParameter && this.blockID == blockeventdata.blockID;
        }
    }

    public String toString()
    {
        return "TE(" + this.coordX + "," + this.coordY + "," + this.coordZ + ")," + this.eventID + "," + this.eventParameter + "," + this.blockID;
    }
}
