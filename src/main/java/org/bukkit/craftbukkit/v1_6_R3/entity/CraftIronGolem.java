package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

public class CraftIronGolem extends CraftGolem implements IronGolem {
    public CraftIronGolem(final CraftServer server, final net.minecraft.entity.monster.EntityIronGolem entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityIronGolem getHandle() {
        return (net.minecraft.entity.monster.EntityIronGolem) entity;
    }

    @Override
    public String toString() {
        return "CraftIronGolem";
    }

    public boolean isPlayerCreated() {
        return getHandle().isPlayerCreated();
    }

    public void setPlayerCreated(final boolean playerCreated) {
        getHandle().setJumping(playerCreated);
    }

    @Override
    public EntityType getType() {
        return EntityType.IRON_GOLEM;
    }
}
