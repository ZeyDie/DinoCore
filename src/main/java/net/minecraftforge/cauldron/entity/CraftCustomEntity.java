package net.minecraftforge.cauldron.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;

public class CraftCustomEntity extends CraftEntity {

    public Class<? extends Entity> entityClass;
    public String entityName;

    public CraftCustomEntity(final CraftServer server, final net.minecraft.entity.Entity entity) {
        super(server, entity);
        this.entityClass = entity.getClass();
        this.entityName = EntityRegistry.getCustomEntityTypeName(entityClass);
        if (entityName == null)
            entityName = entity.getEntityName();
    }

    @Override
    public net.minecraft.entity.Entity getHandle() {
        return (net.minecraft.entity.Entity) entity;
    }

    @Override
    public String toString() {
        return this.entityName;
    }

    public EntityType getType() {
        final EntityType type = EntityType.fromName(this.entityName);
        if (type != null)
            return type;
        else return EntityType.UNKNOWN;
    }
}