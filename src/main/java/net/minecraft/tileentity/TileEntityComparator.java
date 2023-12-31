package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityComparator extends TileEntity
{
    private int outputSignal;

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("OutputSignal", this.outputSignal);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.outputSignal = par1NBTTagCompound.getInteger("OutputSignal");
    }

    public int getOutputSignal()
    {
        return this.outputSignal;
    }

    public void setOutputSignal(final int par1)
    {
        this.outputSignal = par1;
    }

    // Cauldron start
    @Override
    public boolean canUpdate()
    {
        return false;
    }
    // Cauldron end
}
