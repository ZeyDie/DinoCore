package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SmallFireball;

public class CraftSmallFireball extends CraftFireball implements SmallFireball {
    public CraftSmallFireball(final CraftServer server, final net.minecraft.entity.projectile.EntitySmallFireball entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.projectile.EntitySmallFireball getHandle() {
        return (net.minecraft.entity.projectile.EntitySmallFireball) entity;
    }

    @Override
    public String toString() {
        return "CraftSmallFireball";
    }

    public EntityType getType() {
        return EntityType.SMALL_FIREBALL;
    }
}
