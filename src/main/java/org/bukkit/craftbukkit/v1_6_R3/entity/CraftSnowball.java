package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;

public class CraftSnowball extends CraftProjectile implements Snowball {
    public CraftSnowball(final CraftServer server, final net.minecraft.entity.projectile.EntitySnowball entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.projectile.EntitySnowball getHandle() {
        return (net.minecraft.entity.projectile.EntitySnowball) entity;
    }

    @Override
    public String toString() {
        return "CraftSnowball";
    }

    public EntityType getType() {
        return EntityType.SNOWBALL;
    }
}
