package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.inventory.Inventory;

final class CraftMinecartHopper extends CraftMinecart implements HopperMinecart {
    private final CraftInventory inventory;

    CraftMinecartHopper(final CraftServer server, final net.minecraft.entity.item.EntityMinecartHopper entity) {
        super(server, entity);
        inventory = new CraftInventory(entity);
    }

    @Override
    public String toString() {
        return "CraftMinecartHopper{" + "inventory=" + inventory + '}';
    }

    public EntityType getType() {
        return EntityType.MINECART_HOPPER;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
