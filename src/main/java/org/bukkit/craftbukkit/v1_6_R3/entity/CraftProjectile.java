package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public class CraftProjectile extends AbstractProjectile implements Projectile { // Cauldron - concrete
    public CraftProjectile(final CraftServer server, final net.minecraft.entity.Entity entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().getThrower() != null) {
            return (LivingEntity) getHandle().getThrower().getBukkitEntity();
        }

        return null;
    }

    public void setShooter(final LivingEntity shooter) {
        if (shooter instanceof CraftLivingEntity) {
            getHandle().thrower = (net.minecraft.entity.EntityLivingBase) ((CraftLivingEntity) shooter).entity;
            if (shooter instanceof CraftHumanEntity) {
                getHandle().throwerName = ((CraftHumanEntity) shooter).getName();
            }
        }
    }

    @Override
    public net.minecraft.entity.projectile.EntityThrowable getHandle() {
        return (net.minecraft.entity.projectile.EntityThrowable) entity;
    }

    @Override
    public String toString() {
        return "CraftProjectile";
    }

    // Cauldron start
    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }
    // Cauldron end
}
