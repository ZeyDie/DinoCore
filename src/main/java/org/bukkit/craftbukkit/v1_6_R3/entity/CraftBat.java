package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;

public class CraftBat extends CraftAmbient implements Bat {
    public CraftBat(final CraftServer server, final net.minecraft.entity.passive.EntityBat entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityBat getHandle() {
        return (net.minecraft.entity.passive.EntityBat) entity;
    }

    @Override
    public String toString() {
        return "CraftBat";
    }

    public EntityType getType() {
        return EntityType.BAT;
    }
}
