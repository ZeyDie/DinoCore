package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;

public class CraftFish extends AbstractProjectile implements Fish {
    private double biteChance = -1;

    public CraftFish(final CraftServer server, final net.minecraft.entity.projectile.EntityFishHook entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().angler != null) {
            return getHandle().angler.getBukkitEntity();
        }

        return null;
    }

    public void setShooter(final LivingEntity shooter) {
        if (shooter instanceof CraftHumanEntity) {
            getHandle().angler = (net.minecraft.entity.player.EntityPlayer) ((CraftHumanEntity) shooter).entity;
        }
    }

    @Override
    public net.minecraft.entity.projectile.EntityFishHook getHandle() {
        return (net.minecraft.entity.projectile.EntityFishHook) entity;
    }

    @Override
    public String toString() {
        return "CraftFish";
    }

    public EntityType getType() {
        return EntityType.FISHING_HOOK;
    }

    public double getBiteChance() {
        final net.minecraft.entity.projectile.EntityFishHook hook = getHandle();

        if (this.biteChance == -1) {
            if (hook.worldObj.canLightningStrikeAt(net.minecraft.util.MathHelper.floor_double(hook.posX), net.minecraft.util.MathHelper.floor_double(hook.posY) + 1, net.minecraft.util.MathHelper.floor_double(hook.posZ))) {
                return 1/300.0;
            }
            return 1/500.0;
        }
        return this.biteChance;
    }

    public void setBiteChance(final double chance) {
        Validate.isTrue(chance >= 0 && chance <= 1, "The bite chance must be between 0 and 1.");
        this.biteChance = chance;
    }
}
