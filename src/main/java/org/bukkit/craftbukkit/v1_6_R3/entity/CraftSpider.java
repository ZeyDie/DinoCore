package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;

public class CraftSpider extends CraftMonster implements Spider {

    public CraftSpider(final CraftServer server, final net.minecraft.entity.monster.EntitySpider entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntitySpider getHandle() {
        return (net.minecraft.entity.monster.EntitySpider) entity;
    }

    @Override
    public String toString() {
        return "CraftSpider";
    }

    public EntityType getType() {
        return EntityType.SPIDER;
    }
}
