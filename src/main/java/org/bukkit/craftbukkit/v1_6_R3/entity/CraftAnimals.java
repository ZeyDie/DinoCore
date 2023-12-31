package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Animals;

public class CraftAnimals extends CraftAgeable implements Animals {

    public CraftAnimals(final CraftServer server, final net.minecraft.entity.passive.EntityAnimal entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityAnimal getHandle() {
        return (net.minecraft.entity.passive.EntityAnimal) entity;
    }

    @Override
    public String toString() {
        return this.entityName; // Cauldron
    }
}
