package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;

public class CraftCaveSpider extends CraftSpider implements CaveSpider {
    public CraftCaveSpider(final CraftServer server, final net.minecraft.entity.monster.EntityCaveSpider entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityCaveSpider getHandle() {
        return (net.minecraft.entity.monster.EntityCaveSpider) entity;
    }

    @Override
    public String toString() {
        return "CraftCaveSpider";
    }

    public EntityType getType() {
        return EntityType.CAVE_SPIDER;
    }
}
