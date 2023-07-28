package org.bukkit.craftbukkit.v1_6_R3.inventory;

import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryBeacon extends CraftInventory implements BeaconInventory {
    public CraftInventoryBeacon(final net.minecraft.tileentity.TileEntityBeacon beacon) {
        super(beacon);
    }

    public void setItem(final ItemStack item) {
        setItem(0, item);
    }

    public ItemStack getItem() {
        return getItem(0);
    }
}
