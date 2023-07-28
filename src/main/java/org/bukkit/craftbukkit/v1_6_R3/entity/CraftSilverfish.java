package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;

public class CraftSilverfish extends CraftMonster implements Silverfish {
    public CraftSilverfish(final CraftServer server, final net.minecraft.entity.monster.EntitySilverfish entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntitySilverfish getHandle() {
        return (net.minecraft.entity.monster.EntitySilverfish) entity;
    }

    @Override
    public String toString() {
        return "CraftSilverfish";
    }

    public EntityType getType() {
        return EntityType.SILVERFISH;
    }
}
