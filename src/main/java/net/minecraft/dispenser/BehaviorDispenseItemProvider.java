package net.minecraft.dispenser;

import net.minecraft.item.ItemStack;

final class BehaviorDispenseItemProvider implements IBehaviorDispenseItem
{
    /**
     * Dispenses the specified ItemStack from a dispenser.
     */
    public ItemStack dispense(final IBlockSource par1IBlockSource, final ItemStack par2ItemStack)
    {
        return par2ItemStack;
    }
}
