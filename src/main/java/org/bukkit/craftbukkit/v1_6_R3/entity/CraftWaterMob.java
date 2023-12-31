package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.WaterMob;

public class CraftWaterMob extends CraftCreature implements WaterMob {

    public CraftWaterMob(final CraftServer server, final net.minecraft.entity.passive.EntityWaterMob entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityWaterMob getHandle() {
        return (net.minecraft.entity.passive.EntityWaterMob) entity;
    }

    @Override
    public String toString() {
        return this.entityName; // Cauldron
    }
}
