package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Golem;

public class CraftGolem extends CraftCreature implements Golem {
    public CraftGolem(final CraftServer server, final net.minecraft.entity.monster.EntityGolem entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityGolem getHandle() {
        return (net.minecraft.entity.monster.EntityGolem) entity;
    }

    @Override
    public String toString() {
        return "CraftGolem";
    }
}
