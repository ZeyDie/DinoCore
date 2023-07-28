package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;

public class CraftBlaze extends CraftMonster implements Blaze {
    public CraftBlaze(final CraftServer server, final net.minecraft.entity.monster.EntityBlaze entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityBlaze getHandle() {
        return (net.minecraft.entity.monster.EntityBlaze) entity;
    }

    @Override
    public String toString() {
        return "CraftBlaze";
    }

    public EntityType getType() {
        return EntityType.BLAZE;
    }
}
