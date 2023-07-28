package org.bukkit.craftbukkit.v1_6_R3.inventory;

import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;


public class CraftInventoryFurnace extends CraftInventory implements FurnaceInventory {
    public CraftInventoryFurnace(final net.minecraft.tileentity.TileEntityFurnace inventory) {
        super(inventory);
    }

    public ItemStack getResult() {
        return getItem(2);
    }

    public ItemStack getFuel() {
        return getItem(1);
    }

    public ItemStack getSmelting() {
        return getItem(0);
    }

    public void setFuel(final ItemStack stack) {
        setItem(1,stack);
    }

    public void setResult(final ItemStack stack) {
        setItem(2,stack);
    }

    public void setSmelting(final ItemStack stack) {
        setItem(0,stack);
    }

    @Override
    public Furnace getHolder() {
        return (Furnace) inventory.getOwner();
    }
}
