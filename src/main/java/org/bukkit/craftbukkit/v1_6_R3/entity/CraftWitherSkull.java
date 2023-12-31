package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkull;

public class CraftWitherSkull extends CraftFireball implements WitherSkull {
    public CraftWitherSkull(final CraftServer server, final net.minecraft.entity.projectile.EntityWitherSkull entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.projectile.EntityWitherSkull getHandle() {
        return (net.minecraft.entity.projectile.EntityWitherSkull) entity;
    }

    @Override
    public String toString() {
        return "CraftWitherSkull";
    }

    public EntityType getType() {
        return EntityType.WITHER_SKULL;
    }
}
