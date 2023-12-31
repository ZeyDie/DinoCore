package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;

public class CraftEnderPearl extends CraftProjectile implements EnderPearl {
    public CraftEnderPearl(final CraftServer server, final net.minecraft.entity.item.EntityEnderPearl entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.item.EntityEnderPearl getHandle() {
        return (net.minecraft.entity.item.EntityEnderPearl) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderPearl";
    }

    public EntityType getType() {
        return EntityType.ENDER_PEARL;
    }
}
