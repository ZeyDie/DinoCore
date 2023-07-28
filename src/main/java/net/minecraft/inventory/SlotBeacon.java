package net.minecraft.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class SlotBeacon extends Slot
{
    /** The beacon this slot belongs to. */
    final ContainerBeacon beacon;

    public SlotBeacon(final ContainerBeacon par1ContainerBeacon, final IInventory par2IInventory, final int par3, final int par4, final int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.beacon = par1ContainerBeacon;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack par1ItemStack)
    {
        return par1ItemStack == null ? false : par1ItemStack.itemID == Item.emerald.itemID || par1ItemStack.itemID == Item.diamond.itemID || par1ItemStack.itemID == Item.ingotGold.itemID || par1ItemStack.itemID == Item.ingotIron.itemID;
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return 1;
    }
}
