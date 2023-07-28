package net.minecraft.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class ContainerHorseInventorySlotSaddle extends Slot
{
    final ContainerHorseInventory field_111239_a;

    ContainerHorseInventorySlotSaddle(final ContainerHorseInventory par1ContainerHorseInventory, final IInventory par2IInventory, final int par3, final int par4, final int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.field_111239_a = par1ContainerHorseInventory;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack par1ItemStack)
    {
        return super.isItemValid(par1ItemStack) && par1ItemStack.itemID == Item.saddle.itemID && !this.getHasStack();
    }
}
