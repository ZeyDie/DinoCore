package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;

public class CraftGhast extends CraftFlying implements Ghast {

    public CraftGhast(final CraftServer server, final net.minecraft.entity.monster.EntityGhast entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityGhast getHandle() {
        return (net.minecraft.entity.monster.EntityGhast) entity;
    }

    @Override
    public String toString() {
        return "CraftGhast";
    }

    public EntityType getType() {
        return EntityType.GHAST;
    }
}
