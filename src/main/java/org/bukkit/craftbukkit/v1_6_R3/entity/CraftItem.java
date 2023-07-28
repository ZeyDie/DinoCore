package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class CraftItem extends CraftEntity implements Item {
    private final net.minecraft.entity.item.EntityItem item;

    public CraftItem(final CraftServer server, final net.minecraft.entity.Entity entity, final net.minecraft.entity.item.EntityItem item) {
        super(server, entity);
        this.item = item;
    }

    public CraftItem(final CraftServer server, final net.minecraft.entity.item.EntityItem entity) {
        this(server, entity, entity);
    }

    public ItemStack getItemStack() {
        return CraftItemStack.asCraftMirror(item.getEntityItem());
    }

    public void setItemStack(final ItemStack stack) {
        item.setEntityItemStack(CraftItemStack.asNMSCopy(stack));
    }

    public int getPickupDelay() {
        return item.delayBeforeCanPickup;
    }

    public void setPickupDelay(final int delay) {
        item.delayBeforeCanPickup = delay;
    }

    @Override
    public String toString() {
        return "CraftItem";
    }

    public EntityType getType() {
        return EntityType.DROPPED_ITEM;
    }
}
