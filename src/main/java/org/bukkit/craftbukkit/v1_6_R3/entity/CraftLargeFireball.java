package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LargeFireball;

public class CraftLargeFireball extends CraftFireball implements LargeFireball {
    public CraftLargeFireball(final CraftServer server, final net.minecraft.entity.projectile.EntityLargeFireball entity) {
        super(server, entity);
    }

    @Override
    public void setYield(final float yield) {
        super.setYield(yield);
        getHandle().field_92057_e = (int) yield;
    }

    @Override
    public net.minecraft.entity.projectile.EntityLargeFireball getHandle() {
        return (net.minecraft.entity.projectile.EntityLargeFireball) entity;
    }

    @Override
    public String toString() {
        return "CraftLargeFireball";
    }

    public EntityType getType() {
        return EntityType.FIREBALL;
    }
}
