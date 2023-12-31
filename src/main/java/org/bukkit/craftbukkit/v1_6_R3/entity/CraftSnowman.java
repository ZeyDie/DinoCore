package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowman;

public class CraftSnowman extends CraftGolem implements Snowman {
    public CraftSnowman(final CraftServer server, final net.minecraft.entity.monster.EntitySnowman entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntitySnowman getHandle() {
        return (net.minecraft.entity.monster.EntitySnowman) entity;
    }

    @Override
    public String toString() {
        return "CraftSnowman";
    }

    public EntityType getType() {
        return EntityType.SNOWMAN;
    }
}
