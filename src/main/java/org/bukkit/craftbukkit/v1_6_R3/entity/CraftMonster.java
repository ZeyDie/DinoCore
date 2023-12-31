package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Monster;

public class CraftMonster extends CraftCreature implements Monster {

    public CraftMonster(final CraftServer server, final net.minecraft.entity.monster.EntityMob entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityMob getHandle() {
        return (net.minecraft.entity.monster.EntityMob) entity;
    }

    @Override
    public String toString() {
        return this.entityName; // Cauldron
    }
}
