package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.EntityType;

public class CraftAmbient extends CraftLivingEntity implements Ambient {
    public CraftAmbient(final CraftServer server, final net.minecraft.entity.passive.EntityAmbientCreature entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityAmbientCreature getHandle() {
        return (net.minecraft.entity.passive.EntityAmbientCreature) entity;
    }

    @Override
    public String toString() {
        return this.entityName; // Cauldron
    }

    public EntityType getType() {
        // Cauldron start
        final EntityType type = EntityType.fromName(this.entityName);
        if (type != null)
            return type;
        else return EntityType.UNKNOWN;
        // Cauldron end
    }
}
