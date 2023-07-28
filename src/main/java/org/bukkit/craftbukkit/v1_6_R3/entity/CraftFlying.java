package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Flying;

public class CraftFlying extends CraftLivingEntity implements Flying {

    public CraftFlying(final CraftServer server, final net.minecraft.entity.EntityFlying entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.EntityFlying getHandle() {
        return (net.minecraft.entity.EntityFlying) entity;
    }

    @Override
    public String toString() {
        return "CraftFlying";
    }
}
