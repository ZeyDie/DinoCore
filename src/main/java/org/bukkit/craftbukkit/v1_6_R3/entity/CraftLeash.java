package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LeashHitch;

public class CraftLeash extends CraftHanging implements LeashHitch {
    public CraftLeash(final CraftServer server, final net.minecraft.entity.EntityLeashKnot entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.EntityLeashKnot getHandle() {
        return (net.minecraft.entity.EntityLeashKnot) entity;
    }

    @Override
    public String toString() {
        return "CraftLeash";
    }

    public EntityType getType() {
        return EntityType.LEASH_HITCH;
    }
}
