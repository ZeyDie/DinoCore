package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Minecart;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class CraftMinecart extends CraftVehicle implements Minecart { // Cauldron - concrete for modded minecarts
    public CraftMinecart(final CraftServer server, final net.minecraft.entity.item.EntityMinecart entity) {
        super(server, entity);
    }

    // Cauldron start
    public org.bukkit.entity.EntityType getType() {
        return org.bukkit.entity.EntityType.MINECART;
    }
    // Cauldron end

    public void setDamage(final double damage) {
        getHandle().setDamage((float) damage);
    }

    public double getDamage() {
        return getHandle().getDamage();
    }

    public double getMaxSpeed() {
        return getHandle().maxSpeed;
    }

    public void setMaxSpeed(final double speed) {
        if (speed >= 0.0D) {
            getHandle().maxSpeed = speed;
        }
    }

    public boolean isSlowWhenEmpty() {
        return getHandle().slowWhenEmpty;
    }

    public void setSlowWhenEmpty(final boolean slow) {
        getHandle().slowWhenEmpty = slow;
    }

    public Vector getFlyingVelocityMod() {
        return getHandle().getFlyingVelocityMod();
    }

    public void setFlyingVelocityMod(final Vector flying) {
        getHandle().setFlyingVelocityMod(flying);
    }

    public Vector getDerailedVelocityMod() {
        return getHandle().getDerailedVelocityMod();
    }

    public void setDerailedVelocityMod(final Vector derailed) {
        getHandle().setDerailedVelocityMod(derailed);
    }

    @Override
    public net.minecraft.entity.item.EntityMinecart getHandle() {
        return (net.minecraft.entity.item.EntityMinecart) entity;
    }

    @Deprecated
    public void _INVALID_setDamage(final int damage) {
        setDamage(damage);
    }

    @Deprecated
    public int _INVALID_getDamage() {
        return NumberConversions.ceil(getDamage());
    }
}
