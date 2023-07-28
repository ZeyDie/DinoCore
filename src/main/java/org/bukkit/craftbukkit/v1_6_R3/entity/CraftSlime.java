package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

public class CraftSlime extends CraftLivingEntity implements Slime {

    public CraftSlime(final CraftServer server, final net.minecraft.entity.monster.EntitySlime entity) {
        super(server, entity);
    }

    public int getSize() {
        return getHandle().getSlimeSize();
    }

    public void setSize(final int size) {
        getHandle().setSlimeSize(size);
    }

    @Override
    public net.minecraft.entity.monster.EntitySlime getHandle() {
        return (net.minecraft.entity.monster.EntitySlime) entity;
    }

    @Override
    public String toString() {
        return "CraftSlime";
    }

    public EntityType getType() {
        return EntityType.SLIME;
    }
}
