package net.minecraft.inventory;

// CraftBukkit start

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryView;
// CraftBukkit end

public class ContainerHopper extends Container
{
    private final IInventory field_94538_a;

    // CraftBukkit start
    private CraftInventoryView bukkitEntity = null;
    private InventoryPlayer player;

    public CraftInventoryView getBukkitView()
    {
        if (bukkitEntity != null)
        {
            return bukkitEntity;
        }

        final CraftInventory inventory = new CraftInventory(this.field_94538_a);
        bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end

    public ContainerHopper(final InventoryPlayer par1InventoryPlayer, final IInventory par2IInventory)
    {
        this.field_94538_a = par2IInventory;
        this.player = par1InventoryPlayer; // CraftBukkit - save player
        par2IInventory.openChest();
        final byte b0 = 51;
        int i;

        for (i = 0; i < par2IInventory.getSizeInventory(); ++i)
        {
            this.addSlotToContainer(new Slot(par2IInventory, i, 44 + i * 18, 20));
        }

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, i * 18 + b0));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 58 + b0));
        }
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer)
    {
        if (!this.checkReachable)
        {
            return true;    // CraftBukkit
        }

        return this.field_94538_a.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par2)
    {
        ItemStack itemstack = null;
        final Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            final ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < this.field_94538_a.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.field_94538_a.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.field_94538_a.getSizeInventory(), false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(final EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);
        this.field_94538_a.closeChest();
    }
}
