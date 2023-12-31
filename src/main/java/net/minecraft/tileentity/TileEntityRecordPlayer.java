package net.minecraft.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityRecordPlayer extends TileEntity
{
    /** ID of record which is in Jukebox */
    private ItemStack record;

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("RecordItem"))
        {
            this.func_96098_a(ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("RecordItem")));
        }
        else if (par1NBTTagCompound.getInteger("Record") > 0)
        {
            this.func_96098_a(new ItemStack(par1NBTTagCompound.getInteger("Record"), 1, 0));
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        if (this.func_96097_a() != null)
        {
            par1NBTTagCompound.setCompoundTag("RecordItem", this.func_96097_a().writeToNBT(new NBTTagCompound()));
            par1NBTTagCompound.setInteger("Record", this.func_96097_a().itemID);
        }
    }

    public ItemStack func_96097_a()
    {
        return this.record;
    }

    public void func_96098_a(final ItemStack par1ItemStack)
    {
        // CraftBukkit start - There can only be one
        if (par1ItemStack != null)
        {
            par1ItemStack.stackSize = 1;
        }

        // CraftBukkit end
        this.record = par1ItemStack;
        this.onInventoryChanged();
    }

    // Cauldron start
    @Override
    public boolean canUpdate()
    {
        return false;
    }
    // Cauldron end
}
