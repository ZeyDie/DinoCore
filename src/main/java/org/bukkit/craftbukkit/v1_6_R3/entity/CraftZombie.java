package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

public class CraftZombie extends CraftMonster implements Zombie {

    public CraftZombie(final CraftServer server, final net.minecraft.entity.monster.EntityZombie entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityZombie getHandle() {
        return (net.minecraft.entity.monster.EntityZombie) entity;
    }

    @Override
    public String toString() {
        return "CraftZombie";
    }

    public EntityType getType() {
        return EntityType.ZOMBIE;
    }

    public boolean isBaby() {
        return getHandle().isChild();
    }

    public void setBaby(final boolean flag) {
        getHandle().setChild(flag);
    }

    public boolean isVillager() {
        return getHandle().isVillager();
    }

    public void setVillager(final boolean flag) {
        getHandle().setVillager(flag);
    }
}
