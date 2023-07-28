package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;

public class CraftPigZombie extends CraftZombie implements PigZombie {

    public CraftPigZombie(final CraftServer server, final net.minecraft.entity.monster.EntityPigZombie entity) {
        super(server, entity);
    }

    public int getAnger() {
        return getHandle().angerLevel;
    }

    public void setAnger(final int level) {
        getHandle().angerLevel = level;
    }

    public void setAngry(final boolean angry) {
        setAnger(angry ? 400 : 0);
    }

    public boolean isAngry() {
        return getAnger() > 0;
    }

    @Override
    public net.minecraft.entity.monster.EntityPigZombie getHandle() {
        return (net.minecraft.entity.monster.EntityPigZombie) entity;
    }

    @Override
    public String toString() {
        return "CraftPigZombie";
    }

    public EntityType getType() {
        return EntityType.PIG_ZOMBIE;
    }
}
