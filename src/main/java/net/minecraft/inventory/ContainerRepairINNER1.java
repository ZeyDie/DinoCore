package net.minecraft.inventory;

// CraftBukkit start

import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import java.util.List;
// CraftBukkit end

public class ContainerRepairINNER1 extends InventoryBasic   // CraftBukkit - public
{
    final ContainerRepair repairContainer;

    // CraftBukkit start
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    public org.bukkit.entity.Player player;
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents()
    {
        return this.inventoryContents;
    }

    public void onOpen(CraftHumanEntity who)
    {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who)
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

    public void setMaxStackSize(int size)
    {
        maxStack = size;
    }
    // CraftBukkit end

    ContainerRepairINNER1(ContainerRepair par1ContainerRepair, String par2Str, boolean par3, int par4)
    {
        super(par2Str, par3, par4);
        this.repairContainer = par1ContainerRepair;
        this.setMaxStackSize(1); // CraftBukkit
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged()
    {
        super.onInventoryChanged();
        this.repairContainer.onCraftMatrixChanged(this);
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return true;
    }
}
