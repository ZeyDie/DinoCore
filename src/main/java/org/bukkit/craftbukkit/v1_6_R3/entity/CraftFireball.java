package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class CraftFireball extends AbstractProjectile implements Fireball {
    public CraftFireball(final CraftServer server, final net.minecraft.entity.projectile.EntityFireball entity) {
        super(server, entity);
    }

    public float getYield() {
        return getHandle().yield;
    }

    public boolean isIncendiary() {
        return getHandle().isIncendiary;
    }

    public void setIsIncendiary(final boolean isIncendiary) {
        getHandle().isIncendiary = isIncendiary;
    }

    public void setYield(final float yield) {
        getHandle().yield = yield;
    }

    public LivingEntity getShooter() {
        if (getHandle().shootingEntity != null) {
            return (LivingEntity) getHandle().shootingEntity.getBukkitEntity();
        }

        return null;
    }

    public void setShooter(final LivingEntity shooter) {
        if (shooter instanceof CraftLivingEntity) {
            getHandle().shootingEntity = (net.minecraft.entity.EntityLivingBase) ((CraftLivingEntity) shooter).entity;
        }
    }

    public Vector getDirection() {
        return new Vector(getHandle().accelerationX, getHandle().accelerationY, getHandle().accelerationZ);
    }

    public void setDirection(final Vector direction) {
        getHandle().setDirection(direction.getX(), direction.getY(), direction.getZ());
    }

    @Override
    public net.minecraft.entity.projectile.EntityFireball getHandle() {
        return (net.minecraft.entity.projectile.EntityFireball) entity;
    }

    @Override
    public String toString() {
        return "CraftFireball";
    }

    public EntityType getType() {
        return EntityType.UNKNOWN;
    }
}
