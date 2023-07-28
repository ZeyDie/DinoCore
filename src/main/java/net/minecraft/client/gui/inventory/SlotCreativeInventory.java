package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

@SideOnly(Side.CLIENT)
class SlotCreativeInventory extends Slot
{
    private final Slot theSlot;

    final GuiContainerCreative theCreativeInventory;

    public SlotCreativeInventory(final GuiContainerCreative par1GuiContainerCreative, final Slot par2Slot, final int par3)
    {
        super(par2Slot.inventory, par3, 0, 0);
        this.theCreativeInventory = par1GuiContainerCreative;
        this.theSlot = par2Slot;
    }

    public void onPickupFromSlot(final EntityPlayer par1EntityPlayer, final ItemStack par2ItemStack)
    {
        this.theSlot.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack par1ItemStack)
    {
        return this.theSlot.isItemValid(par1ItemStack);
    }

    /**
     * Helper fnct to get the stack in the slot.
     */
    public ItemStack getStack()
    {
        return this.theSlot.getStack();
    }

    /**
     * Returns if this slot contains a stack.
     */
    public boolean getHasStack()
    {
        return this.theSlot.getHasStack();
    }

    /**
     * Helper method to put a stack in the slot.
     */
    public void putStack(final ItemStack par1ItemStack)
    {
        this.theSlot.putStack(par1ItemStack);
    }

    /**
     * Called when the stack in a Slot changes
     */
    public void onSlotChanged()
    {
        this.theSlot.onSlotChanged();
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return this.theSlot.getSlotStackLimit();
    }

    /**
     * Returns the icon index on items.png that is used as background image of the slot.
     */
    public Icon getBackgroundIconIndex()
    {
        return this.theSlot.getBackgroundIconIndex();
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(final int par1)
    {
        return this.theSlot.decrStackSize(par1);
    }

    /**
     * returns true if this slot is in par2 of par1
     */
    public boolean isSlotInInventory(final IInventory par1IInventory, final int par2)
    {
        return this.theSlot.isSlotInInventory(par1IInventory, par2);
    }

    static Slot func_75240_a(final SlotCreativeInventory par0SlotCreativeInventory)
    {
        return par0SlotCreativeInventory.theSlot;
    }
}
