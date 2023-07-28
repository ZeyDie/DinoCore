package net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;

class ContainerHorseInventorySlotArmor extends Slot
{
    final EntityHorse theHorse;

    final ContainerHorseInventory field_111240_b;

    ContainerHorseInventorySlotArmor(final ContainerHorseInventory par1ContainerHorseInventory, final IInventory par2IInventory, final int par3, final int par4, final int par5, final EntityHorse par6EntityHorse)
    {
        super(par2IInventory, par3, par4, par5);
        this.field_111240_b = par1ContainerHorseInventory;
        this.theHorse = par6EntityHorse;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack par1ItemStack)
    {
        return super.isItemValid(par1ItemStack) && this.theHorse.func_110259_cr() && EntityHorse.func_110211_v(par1ItemStack.itemID);
    }

    @SideOnly(Side.CLIENT)
    public boolean func_111238_b()
    {
        return this.theHorse.func_110259_cr();
    }
}
