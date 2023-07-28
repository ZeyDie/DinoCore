package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;

public class CraftPig extends CraftAnimals implements Pig {
    public CraftPig(final CraftServer server, final net.minecraft.entity.passive.EntityPig entity) {
        super(server, entity);
    }

    public boolean hasSaddle() {
        return getHandle().getSaddled();
    }

    public void setSaddle(final boolean saddled) {
        getHandle().setSaddled(saddled);
    }

    public net.minecraft.entity.passive.EntityPig getHandle() {
        return (net.minecraft.entity.passive.EntityPig) entity;
    }

    @Override
    public String toString() {
        return "CraftPig";
    }

    public EntityType getType() {
        return EntityType.PIG;
    }
}
