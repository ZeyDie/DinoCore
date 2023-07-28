package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;

public class CraftCow extends CraftAnimals implements Cow {

    public CraftCow(final CraftServer server, final net.minecraft.entity.passive.EntityCow entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityCow getHandle() {
        return (net.minecraft.entity.passive.EntityCow) entity;
    }

    @Override
    public String toString() {
        return "CraftCow";
    }

    public EntityType getType() {
        return EntityType.COW;
    }
}
