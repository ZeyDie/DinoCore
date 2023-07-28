package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

class SlotEnchantment extends Slot
{
    /** The brewing stand this slot belongs to. */
    final ContainerEnchantment container;

    SlotEnchantment(final ContainerEnchantment par1ContainerEnchantment, final IInventory par2IInventory, final int par3, final int par4, final int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.container = par1ContainerEnchantment;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack par1ItemStack)
    {
        return true;
    }
}
