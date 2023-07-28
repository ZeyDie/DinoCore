package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot
{
    /** Merchant's inventory. */
    private final InventoryMerchant theMerchantInventory;

    /** The Player whos trying to buy/sell stuff. */
    private EntityPlayer thePlayer;
    private int field_75231_g;

    /** "Instance" of the Merchant. */
    private final IMerchant theMerchant;

    public SlotMerchantResult(final EntityPlayer par1EntityPlayer, final IMerchant par2IMerchant, final InventoryMerchant par3InventoryMerchant, final int par4, final int par5, final int par6)
    {
        super(par3InventoryMerchant, par4, par5, par6);
        this.thePlayer = par1EntityPlayer;
        this.theMerchant = par2IMerchant;
        this.theMerchantInventory = par3InventoryMerchant;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack par1ItemStack)
    {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(final int par1)
    {
        if (this.getHasStack())
        {
            this.field_75231_g += Math.min(par1, this.getStack().stackSize);
        }

        return super.decrStackSize(par1);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(final ItemStack par1ItemStack, final int par2)
    {
        this.field_75231_g += par2;
        this.onCrafting(par1ItemStack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(final ItemStack par1ItemStack)
    {
        par1ItemStack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75231_g);
        this.field_75231_g = 0;
    }

    public void onPickupFromSlot(final EntityPlayer par1EntityPlayer, final ItemStack par2ItemStack)
    {
        this.onCrafting(par2ItemStack);
        final MerchantRecipe merchantrecipe = this.theMerchantInventory.getCurrentRecipe();

        if (merchantrecipe != null)
        {
            ItemStack itemstack1 = this.theMerchantInventory.getStackInSlot(0);
            ItemStack itemstack2 = this.theMerchantInventory.getStackInSlot(1);

            if (this.func_75230_a(merchantrecipe, itemstack1, itemstack2) || this.func_75230_a(merchantrecipe, itemstack2, itemstack1))
            {
                this.theMerchant.useRecipe(merchantrecipe);

                if (itemstack1 != null && itemstack1.stackSize <= 0)
                {
                    itemstack1 = null;
                }

                if (itemstack2 != null && itemstack2.stackSize <= 0)
                {
                    itemstack2 = null;
                }

                this.theMerchantInventory.setInventorySlotContents(0, itemstack1);
                this.theMerchantInventory.setInventorySlotContents(1, itemstack2);
            }
        }
    }

    private boolean func_75230_a(final MerchantRecipe par1MerchantRecipe, final ItemStack par2ItemStack, final ItemStack par3ItemStack)
    {
        final ItemStack itemstack2 = par1MerchantRecipe.getItemToBuy();
        final ItemStack itemstack3 = par1MerchantRecipe.getSecondItemToBuy();

        if (par2ItemStack != null && par2ItemStack.itemID == itemstack2.itemID)
        {
            if (itemstack3 != null && par3ItemStack != null && itemstack3.itemID == par3ItemStack.itemID)
            {
                par2ItemStack.stackSize -= itemstack2.stackSize;
                par3ItemStack.stackSize -= itemstack3.stackSize;
                return true;
            }

            if (itemstack3 == null && par3ItemStack == null)
            {
                par2ItemStack.stackSize -= itemstack2.stackSize;
                return true;
            }
        }

        return false;
    }
}
