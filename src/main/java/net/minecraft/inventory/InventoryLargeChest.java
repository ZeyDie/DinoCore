package net.minecraft.inventory;

// CraftBukkit start

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import java.util.List;
// CraftBukkit end

public class InventoryLargeChest implements IInventory
{
    /** Name of the chest. */
    private String name;

    /** Inventory object corresponding to double chest upper part */
    public IInventory upperChest; // CraftBukkit - private -> public

    /** Inventory object corresponding to double chest lower part */
    public IInventory lowerChest; // CraftBukkit - private -> public

    // CraftBukkit start
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();

    public ItemStack[] getContents()
    {
        final ItemStack[] result = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = this.getStackInSlot(i);
        }

        return result;
    }

    public void onOpen(final CraftHumanEntity who)
    {
        this.upperChest.onOpen(who);
        this.lowerChest.onOpen(who);
        transaction.add(who);
    }

    public void onClose(final CraftHumanEntity who)
    {
        this.upperChest.onClose(who);
        this.lowerChest.onClose(who);
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers()
    {
        return transaction;
    }

    public org.bukkit.inventory.InventoryHolder getOwner()
    {
        return null; // This method won't be called since CraftInventoryDoubleChest doesn't defer to here
    }

    public void setMaxStackSize(final int size)
    {
        this.upperChest.setMaxStackSize(size);
        this.lowerChest.setMaxStackSize(size);
    }
    // CraftBukkit end

    public InventoryLargeChest(final String par1Str, IInventory par2IInventory, IInventory par3IInventory)
    {
        IInventory par2IInventory1 = par2IInventory;
        IInventory par3IInventory1 = par3IInventory;
        this.name = par1Str;

        if (par2IInventory1 == null)
        {
            par2IInventory1 = par3IInventory1;
        }

        if (par3IInventory1 == null)
        {
            par3IInventory1 = par2IInventory1;
        }

        this.upperChest = par2IInventory1;
        this.lowerChest = par3IInventory1;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
    }

    /**
     * Return whether the given inventory is part of this large chest.
     */
    public boolean isPartOfLargeChest(final IInventory par1IInventory)
    {
        return this.upperChest == par1IInventory || this.lowerChest == par1IInventory;
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return this.upperChest.isInvNameLocalized() ? this.upperChest.getInvName() : (this.lowerChest.isInvNameLocalized() ? this.lowerChest.getInvName() : this.name);
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    public boolean isInvNameLocalized()
    {
        return this.upperChest.isInvNameLocalized() || this.lowerChest.isInvNameLocalized();
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(final int par1)
    {
        return par1 >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlot(par1 - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlot(par1);
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(final int par1, final int par2)
    {
        return par1 >= this.upperChest.getSizeInventory() ? this.lowerChest.decrStackSize(par1 - this.upperChest.getSizeInventory(), par2) : this.upperChest.decrStackSize(par1, par2);
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(final int par1)
    {
        return par1 >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlotOnClosing(par1 - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlotOnClosing(par1);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack)
    {
        if (par1 >= this.upperChest.getSizeInventory())
        {
            this.lowerChest.setInventorySlotContents(par1 - this.upperChest.getSizeInventory(), par2ItemStack);
        }
        else
        {
            this.upperChest.setInventorySlotContents(par1, par2ItemStack);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return Math.min(this.upperChest.getInventoryStackLimit(), this.lowerChest.getInventoryStackLimit()); // CraftBukkit - check both sides
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged()
    {
        this.upperChest.onInventoryChanged();
        this.lowerChest.onInventoryChanged();
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer)
    {
        return this.upperChest.isUseableByPlayer(par1EntityPlayer) && this.lowerChest.isUseableByPlayer(par1EntityPlayer);
    }

    public void openChest()
    {
        this.upperChest.openChest();
        this.lowerChest.openChest();
    }

    public void closeChest()
    {
        this.upperChest.closeChest();
        this.lowerChest.closeChest();
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(final int par1, final ItemStack par2ItemStack)
    {
        return true;
    }
}
