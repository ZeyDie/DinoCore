package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

public class CraftComplexPart extends CraftEntity implements ComplexEntityPart {
    public CraftComplexPart(final CraftServer server, final net.minecraft.entity.boss.EntityDragonPart entity) {
        super(server, entity);
    }

    public ComplexLivingEntity getParent() {
        // Cauldron start - Fix twilight Hydra crashes
        final org.bukkit.entity.Entity result = getParentEntity();
        return (result instanceof ComplexLivingEntity) ? (ComplexLivingEntity)result : null;
    }

    private org.bukkit.entity.Entity getParentEntity() {
        return ((net.minecraft.entity.Entity)getHandle().entityDragonObj).getBukkitEntity();
    }

    @Override
    public void setLastDamageCause(final EntityDamageEvent cause) {
        getParentEntity().setLastDamageCause(cause);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return getParentEntity().getLastDamageCause();
        // Cauldron end
    }

    @Override
    public net.minecraft.entity.boss.EntityDragonPart getHandle() {
        return (net.minecraft.entity.boss.EntityDragonPart) entity;
    }

    @Override
    public String toString() {
        return "CraftComplexPart";
    }

    public EntityType getType() {
        return EntityType.COMPLEX_PART;
    }
}
