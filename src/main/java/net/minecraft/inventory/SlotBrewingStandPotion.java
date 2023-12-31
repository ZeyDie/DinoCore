package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

class SlotBrewingStandPotion extends Slot
{
    /** The player that has this container open. */
    private EntityPlayer player;

    public SlotBrewingStandPotion(final EntityPlayer par1EntityPlayer, final IInventory par2IInventory, final int par3, final int par4, final int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.player = par1EntityPlayer;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack par1ItemStack)
    {
        return canHoldPotion(par1ItemStack);
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return 1;
    }

    public void onPickupFromSlot(final EntityPlayer par1EntityPlayer, final ItemStack par2ItemStack)
    {
        if (par2ItemStack.getItem() instanceof ItemPotion && par2ItemStack.getItemDamage() > 0)
        {
            this.player.addStat(AchievementList.potion, 1);
        }

        super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
    }

    /**
     * Returns true if this itemstack can be filled with a potion
     */
    public static boolean canHoldPotion(final ItemStack par0ItemStack)
    {
        return par0ItemStack != null && (par0ItemStack.getItem() instanceof ItemPotion || par0ItemStack.itemID == Item.glassBottle.itemID);
    }
}
