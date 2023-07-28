package net.minecraft.inventory;

// CraftBukkit start

import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import java.util.List;
// CraftBukkit end

public class SlotEnchantmentTable extends InventoryBasic   // CraftBukkit -> public
{
    /** The brewing stand this slot belongs to. */
    final ContainerEnchantment container;

    // CraftBukkit start
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    public org.bukkit.entity.Player player;
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents()
    {
        return this.inventoryContents;
    }

    public void onOpen(final CraftHumanEntity who)
    {
        transaction.add(who);
    }

    public void onClose(final CraftHumanEntity who)
    {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers()
    {
        return transaction;
    }

    public org.bukkit.inventory.InventoryHolder getOwner()
    {
        return this.player;
    }

    public void setMaxStackSize(final int size)
    {
        maxStack = size;
    }
    // CraftBukkit end

    SlotEnchantmentTable(final ContainerEnchantment par1ContainerEnchantment, final String par2Str, final boolean par3, final int par4)
    {
        super(par2Str, par3, par4);
        this.container = par1ContainerEnchantment;
        this.setMaxStackSize(1); // CraftBukkit
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
    public void onInventoryChanged()
    {
        super.onInventoryChanged();
        this.container.onCraftMatrixChanged(this);
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(final int par1, final ItemStack par2ItemStack)
    {
        return true;
    }
}
