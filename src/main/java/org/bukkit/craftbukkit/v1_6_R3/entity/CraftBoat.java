package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;

public class CraftBoat extends CraftVehicle implements Boat {

    public CraftBoat(final CraftServer server, final net.minecraft.entity.item.EntityBoat entity) {
        super(server, entity);
    }

    public double getMaxSpeed() {
        return getHandle().maxSpeed;
    }

    public void setMaxSpeed(final double speed) {
        if (speed >= 0.0D) {
            getHandle().maxSpeed = speed;
        }
    }

    public double getOccupiedDeceleration() {
        return getHandle().occupiedDeceleration;
    }

    public void setOccupiedDeceleration(final double speed) {
        if (speed >= 0.0D) {
            getHandle().occupiedDeceleration = speed;
        }
    }

    public double getUnoccupiedDeceleration() {
        return getHandle().unoccupiedDeceleration;
    }

    public void setUnoccupiedDeceleration(final double speed) {
        getHandle().unoccupiedDeceleration = speed;
    }

    public boolean getWorkOnLand() {
        return getHandle().landBoats;
    }

    public void setWorkOnLand(final boolean workOnLand) {
        getHandle().landBoats = workOnLand;
    }

    @Override
    public net.minecraft.entity.item.EntityBoat getHandle() {
        return (net.minecraft.entity.item.EntityBoat) entity;
    }

    @Override
    public String toString() {
        return "CraftBoat";
    }

    public EntityType getType() {
        return EntityType.BOAT;
    }
}
