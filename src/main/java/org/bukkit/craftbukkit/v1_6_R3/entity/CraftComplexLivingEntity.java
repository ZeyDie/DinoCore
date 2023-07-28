package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.ComplexLivingEntity;

public abstract class CraftComplexLivingEntity extends CraftLivingEntity implements ComplexLivingEntity {
    public CraftComplexLivingEntity(final CraftServer server, final net.minecraft.entity.EntityLivingBase entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.EntityLivingBase getHandle() {
        return (net.minecraft.entity.EntityLivingBase) entity;
    }

    @Override
    public String toString() {
        return "CraftComplexLivingEntity";
    }
}
