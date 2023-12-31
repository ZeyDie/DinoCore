package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

public class CraftTNTPrimed extends CraftEntity implements TNTPrimed {

    public CraftTNTPrimed(final CraftServer server, final net.minecraft.entity.item.EntityTNTPrimed entity) {
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

    public int getFuseTicks() {
        return getHandle().fuse;
    }

    public void setFuseTicks(final int fuseTicks) {
        getHandle().fuse = fuseTicks;
    }

    @Override
    public net.minecraft.entity.item.EntityTNTPrimed getHandle() {
        return (net.minecraft.entity.item.EntityTNTPrimed) entity;
    }

    @Override
    public String toString() {
        return "CraftTNTPrimed";
    }

    public EntityType getType() {
        return EntityType.PRIMED_TNT;
    }

    public Entity getSource() {
        final net.minecraft.entity.EntityLivingBase source = getHandle().getTntPlacedBy();

        if (source != null) {
            final Entity bukkitEntity = source.getBukkitEntity();

            if (bukkitEntity.isValid()) {
                return bukkitEntity;
            }
        }

        return null;
    }
}
