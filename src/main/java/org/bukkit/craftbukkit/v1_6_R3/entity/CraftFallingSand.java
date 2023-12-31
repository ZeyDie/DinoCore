package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingSand;

public class CraftFallingSand extends CraftEntity implements FallingSand {

    public CraftFallingSand(final CraftServer server, final net.minecraft.entity.item.EntityFallingSand entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.item.EntityFallingSand getHandle() {
        return (net.minecraft.entity.item.EntityFallingSand) entity;
    }

    @Override
    public String toString() {
        return "CraftFallingSand";
    }

    public EntityType getType() {
        return EntityType.FALLING_BLOCK;
    }

    public Material getMaterial() {
        return Material.getMaterial(getBlockId());
    }

    public int getBlockId() {
        return getHandle().blockID;
    }

    public byte getBlockData() {
        return (byte) getHandle().metadata;
    }

    public boolean getDropItem() {
        return getHandle().shouldDropItem;
    }

    public void setDropItem(final boolean drop) {
        getHandle().shouldDropItem = drop;
    }
}
