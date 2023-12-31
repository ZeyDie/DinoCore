package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;

public class CraftWitch extends CraftMonster implements Witch {
    public CraftWitch(final CraftServer server, final net.minecraft.entity.monster.EntityWitch entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityWitch getHandle() {
        return (net.minecraft.entity.monster.EntityWitch) entity;
    }

    @Override
    public String toString() {
        return "CraftWitch";
    }

    public EntityType getType() {
        return EntityType.WITCH;
    }
}
