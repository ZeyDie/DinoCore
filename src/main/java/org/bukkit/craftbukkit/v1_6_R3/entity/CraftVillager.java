package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class CraftVillager extends CraftAgeable implements Villager {
    public CraftVillager(final CraftServer server, final net.minecraft.entity.passive.EntityVillager entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityVillager getHandle() {
        return (net.minecraft.entity.passive.EntityVillager) entity;
    }

    @Override
    public String toString() {
        return "CraftVillager";
    }

    public EntityType getType() {
        return EntityType.VILLAGER;
    }

    public Profession getProfession() {
        return Profession.getProfession(getHandle().getProfession());
    }

    public void setProfession(final Profession profession) {
        Validate.notNull(profession);
        getHandle().setProfession(profession.getId());
    }
}
