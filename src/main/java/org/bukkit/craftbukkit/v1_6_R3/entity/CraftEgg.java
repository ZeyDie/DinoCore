package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;

public class CraftEgg extends CraftProjectile implements Egg {
    public CraftEgg(CraftServer server, net.minecraft.entity.projectile.EntityEgg entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.projectile.EntityEgg getHandle() {
        return (net.minecraft.entity.projectile.EntityEgg) entity;
    }

    @Override
    public String toString() {
        return "CraftEgg";
    }

    public EntityType getType() {
        return EntityType.EGG;
    }
}
