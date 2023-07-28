package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Tameable;

public class CraftTameableAnimal extends CraftAnimals implements Tameable, Creature {
    public CraftTameableAnimal(final CraftServer server, final net.minecraft.entity.passive.EntityTameable entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityTameable getHandle() {
        return (net.minecraft.entity.passive.EntityTameable)super.getHandle();
    }

    public AnimalTamer getOwner() {
        if (("").equals(getOwnerName())) return null;

        AnimalTamer owner = getServer().getPlayerExact(getOwnerName());
        if (owner == null) {
            owner = getServer().getOfflinePlayer(getOwnerName());
        }

        return owner;
    }

    public String getOwnerName() {
        return getHandle().getOwnerName();
    }

    public boolean isTamed() {
        return getHandle().isTamed();
    }

    public void setOwner(final AnimalTamer tamer) {
        if (tamer != null) {
            setTamed(true);
            getHandle().setPathToEntity(null);
            setOwnerName(tamer.getName());
        } else {
            setTamed(false);
            setOwnerName("");
        }
    }

    public void setOwnerName(final String ownerName) {
        getHandle().setOwner(ownerName == null ? "" : ownerName);
    }

    public void setTamed(final boolean tame) {
        getHandle().setTamed(tame);
        if (!tame) {
            setOwnerName("");
        }
    }

    public boolean isSitting() {
        return getHandle().isSitting();
    }

    public void setSitting(final boolean sitting) {
        getHandle().func_70907_r().setSitting(sitting);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{owner=" + getOwner() + ",tamed=" + isTamed() + "}";
    }
}
