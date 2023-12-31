package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;

public class CraftChicken extends CraftAnimals implements Chicken {

    public CraftChicken(final CraftServer server, final net.minecraft.entity.passive.EntityChicken entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityChicken getHandle() {
        return (net.minecraft.entity.passive.EntityChicken) entity;
    }

    @Override
    public String toString() {
        return "CraftChicken";
    }

    public EntityType getType() {
        return EntityType.CHICKEN;
    }
}
