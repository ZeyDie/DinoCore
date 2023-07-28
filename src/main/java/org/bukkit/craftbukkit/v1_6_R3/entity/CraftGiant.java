package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;

public class CraftGiant extends CraftMonster implements Giant {

    public CraftGiant(final CraftServer server, final net.minecraft.entity.monster.EntityGiantZombie entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityGiantZombie getHandle() {
        return (net.minecraft.entity.monster.EntityGiantZombie) entity;
    }

    @Override
    public String toString() {
        return "CraftGiant";
    }

    public EntityType getType() {
        return EntityType.GIANT;
    }
}
