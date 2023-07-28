package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class CraftArrow extends AbstractProjectile implements Arrow {

    public CraftArrow(final CraftServer server, final net.minecraft.entity.projectile.EntityArrow entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().shootingEntity != null) {
            return (LivingEntity) getHandle().shootingEntity.getBukkitEntity();
        }

        return null;
    }

    public void setShooter(final LivingEntity shooter) {
        if (shooter instanceof CraftLivingEntity) {
            getHandle().shootingEntity = ((CraftLivingEntity) shooter).getHandle();
        }
    }

    @Override
    public net.minecraft.entity.projectile.EntityArrow getHandle() {
        return (net.minecraft.entity.projectile.EntityArrow) entity;
    }

    @Override
    public String toString() {
        return "CraftArrow";
    }

    public EntityType getType() {
        return EntityType.ARROW;
    }

    // Spigot start
    private final Arrow.Spigot spigot = new Arrow.Spigot()
    {
        @Override
        public double getDamage()
        {
            return getHandle().getDamage();
        }

        @Override
        public void setDamage(final double damage)
        {
            getHandle().setDamage( damage );
        }
    };

    public Arrow.Spigot spigot()
    {
        return spigot;
    }
    // Spigot end
}
