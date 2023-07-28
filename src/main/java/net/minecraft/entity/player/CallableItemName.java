package net.minecraft.entity.player;

import net.minecraft.item.ItemStack;

import java.util.concurrent.Callable;

class CallableItemName implements Callable
{
    final ItemStack theItemStack;

    final InventoryPlayer playerInventory;

    CallableItemName(final InventoryPlayer par1InventoryPlayer, final ItemStack par2ItemStack)
    {
        this.playerInventory = par1InventoryPlayer;
        this.theItemStack = par2ItemStack;
    }

    public String callItemDisplayName()
    {
        return this.theItemStack.getDisplayName();
    }

    public Object call()
    {
        return this.callItemDisplayName();
    }
}
