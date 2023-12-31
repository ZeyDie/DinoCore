package net.minecraft.inventory;

// CraftBukkit start

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

public class InventoryCraftResult implements IInventory
{
    /** A list of one item containing the result of the crafting formula */
    private ItemStack[] stackResult = new ItemStack[1];

    // CraftBukkit start
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents()
    {
        return this.stackResult;
    }

    public org.bukkit.inventory.InventoryHolder getOwner()
    {
        return null; // Result slots don't get an owner
    }

    // Don't need a transaction; the InventoryCrafting keeps track of it for us
    public void onOpen(final CraftHumanEntity who) {}
    public void onClose(final CraftHumanEntity who) {}
    public java.util.List<HumanEntity> getViewers()
    {
        return new java.util.ArrayList<HumanEntity>();
    }

    public void setMaxStackSize(final int size)
    {
        maxStack = size;
    }
    // CraftBukkit end

    public InventoryCraftResult() {}

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return 1;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(final int par1)
    {
        return this.stackResult[0];
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return "Result";
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    public boolean isInvNameLocalized()
    {
        return false;
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(final int par1, final int par2)
    {
        if (this.stackResult[0] != null)
        {
            final ItemStack itemstack = this.stackResult[0];
            this.stackResult[0] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(final int par1)
    {
        if (this.stackResult[0] != null)
        {
            final ItemStack itemstack = this.stackResult[0];
            this.stackResult[0] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack)
    {
        this.stackResult[0] = par2ItemStack;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return maxStack; // CraftBukkit
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged() {}

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    public void openChest() {}

    public void closeChest() {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(final int par1, final ItemStack par2ItemStack)
    {
        return true;
    }
}
