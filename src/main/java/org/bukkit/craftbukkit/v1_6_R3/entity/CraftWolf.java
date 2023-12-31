package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;

public class CraftWolf extends CraftTameableAnimal implements Wolf {
    public CraftWolf(final CraftServer server, final net.minecraft.entity.passive.EntityWolf wolf) {
        super(server, wolf);
    }

    public boolean isAngry() {
        return getHandle().isAngry();
    }

    public void setAngry(final boolean angry) {
        getHandle().setAngry(angry);
    }

    @Override
    public net.minecraft.entity.passive.EntityWolf getHandle() {
        return (net.minecraft.entity.passive.EntityWolf) entity;
    }

    @Override
    public EntityType getType() {
        return EntityType.WOLF;
    }

    public DyeColor getCollarColor() {
        return DyeColor.getByWoolData((byte) getHandle().getCollarColor());
    }

    public void setCollarColor(final DyeColor color) {
        getHandle().setCollarColor(color.getWoolData());
    }
}
