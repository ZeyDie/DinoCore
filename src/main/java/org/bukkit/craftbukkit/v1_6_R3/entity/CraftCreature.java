package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

public class CraftCreature extends CraftLivingEntity implements Creature {
    public CraftCreature(final CraftServer server, final net.minecraft.entity.EntityCreature entity) {
        super(server, entity);
    }

    public void setTarget(final LivingEntity target) {
        final net.minecraft.entity.EntityCreature entity = getHandle();
        if (target == null) {
            entity.entityToAttack = null;
        } else if (target instanceof CraftLivingEntity) {
            entity.entityToAttack = ((CraftLivingEntity) target).getHandle();
            entity.pathToEntity = entity.worldObj.getPathEntityToEntity(entity, entity.entityToAttack, 16.0F, true, false, false, true);
        }
    }

    public CraftLivingEntity getTarget() {
        if (getHandle().entityToAttack == null) return null;
        if (!(getHandle().entityToAttack instanceof net.minecraft.entity.EntityLivingBase)) return null;

        return (CraftLivingEntity) getHandle().entityToAttack.getBukkitEntity();
    }

    @Override
    public net.minecraft.entity.EntityCreature getHandle() {
        return (net.minecraft.entity.EntityCreature) entity;
    }

    @Override
    public String toString() {
        return this.entityName; // Cauldron
    }
}
