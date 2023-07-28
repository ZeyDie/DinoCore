package org.bukkit.craftbukkit.v1_6_R3.inventory;

import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;


public class CraftInventoryEnchanting extends CraftInventory implements EnchantingInventory {
    public CraftInventoryEnchanting(final net.minecraft.inventory.SlotEnchantmentTable inventory) {
        super(inventory);
    }

    public void setItem(final ItemStack item) {
        setItem(0,item);
    }

    public ItemStack getItem() {
        return getItem(0);
    }

    @Override
    public net.minecraft.inventory.SlotEnchantmentTable getInventory() {
        return (net.minecraft.inventory.SlotEnchantmentTable)inventory;
    }
}
