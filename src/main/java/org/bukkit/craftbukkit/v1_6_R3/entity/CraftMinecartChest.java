package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("deprecation")
public class CraftMinecartChest extends CraftMinecart implements StorageMinecart {
    private final CraftInventory inventory;

    public CraftMinecartChest(final CraftServer server, final net.minecraft.entity.item.EntityMinecartChest entity) {
        super(server, entity);
        inventory = new CraftInventory(entity);
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String toString() {
        return "CraftMinecartChest{" + "inventory=" + inventory + '}';
    }

    public EntityType getType() {
        return EntityType.MINECART_CHEST;
    }
}
