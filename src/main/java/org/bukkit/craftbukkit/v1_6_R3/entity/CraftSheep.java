package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

public class CraftSheep extends CraftAnimals implements Sheep {
    public CraftSheep(final CraftServer server, final net.minecraft.entity.passive.EntitySheep entity) {
        super(server, entity);
    }

    public DyeColor getColor() {
        return DyeColor.getByWoolData((byte) getHandle().getFleeceColor());
    }

    public void setColor(final DyeColor color) {
        getHandle().setFleeceColor(color.getWoolData());
    }

    public boolean isSheared() {
        return getHandle().getSheared();
    }

    public void setSheared(final boolean flag) {
        getHandle().setSheared(flag);
    }

    @Override
    public net.minecraft.entity.passive.EntitySheep getHandle() {
        return (net.minecraft.entity.passive.EntitySheep) entity;
    }

    @Override
    public String toString() {
        return "CraftSheep";
    }

    public EntityType getType() {
        return EntityType.SHEEP;
    }
}
