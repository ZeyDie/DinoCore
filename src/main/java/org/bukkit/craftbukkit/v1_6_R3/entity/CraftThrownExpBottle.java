package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownExpBottle;

public class CraftThrownExpBottle extends CraftProjectile implements ThrownExpBottle {
    public CraftThrownExpBottle(final CraftServer server, final net.minecraft.entity.item.EntityExpBottle entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.item.EntityExpBottle getHandle() {
        return (net.minecraft.entity.item.EntityExpBottle) entity;
    }

    @Override
    public String toString() {
        return "EntityThrownExpBottle";
    }

    public EntityType getType() {
        return EntityType.THROWN_EXP_BOTTLE;
    }
}
