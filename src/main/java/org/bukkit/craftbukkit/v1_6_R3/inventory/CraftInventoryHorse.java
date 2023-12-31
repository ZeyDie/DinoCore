package org.bukkit.craftbukkit.v1_6_R3.inventory;

import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryHorse extends CraftInventory implements HorseInventory {

    public CraftInventoryHorse(final net.minecraft.inventory.IInventory inventory) {
        super(inventory);
    }

    public ItemStack getSaddle() {
        return getItem(0);
    }

    public ItemStack getArmor() {
       return getItem(1);
    }

    public void setSaddle(final ItemStack stack) {
        setItem(0, stack);
    }

    public void setArmor(final ItemStack stack) {
        setItem(1, stack);
    }
}
