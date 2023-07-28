package net.minecraft.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class SlotBrewingStandIngredient extends Slot
{
    /** The brewing stand this slot belongs to. */
    final ContainerBrewingStand brewingStand;

    public SlotBrewingStandIngredient(final ContainerBrewingStand par1ContainerBrewingStand, final IInventory par2IInventory, final int par3, final int par4, final int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.brewingStand = par1ContainerBrewingStand;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack par1ItemStack)
    {
        return par1ItemStack != null ? Item.itemsList[par1ItemStack.itemID].isPotionIngredient(par1ItemStack) : false;
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return 64;
    }
}
