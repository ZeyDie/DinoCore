package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;

public class CraftSkeleton extends CraftMonster implements Skeleton {

    public CraftSkeleton(final CraftServer server, final net.minecraft.entity.monster.EntitySkeleton entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntitySkeleton getHandle() {
        return (net.minecraft.entity.monster.EntitySkeleton) entity;
    }

    @Override
    public String toString() {
        return "CraftSkeleton";
    }

    public EntityType getType() {
        return EntityType.SKELETON;
    }

    public SkeletonType getSkeletonType() {
        return SkeletonType.getType(getHandle().getSkeletonType());
    }

    public void setSkeletonType(final SkeletonType type) {
        Validate.notNull(type);
        getHandle().setSkeletonType(type.getId());
    }
}
