package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.util.NumberConversions;

public class CraftEnderDragonPart extends CraftComplexPart implements EnderDragonPart {
    public CraftEnderDragonPart(final CraftServer server, final net.minecraft.entity.boss.EntityDragonPart entity) {
        super(server, entity);
    }

    @Override
    public EnderDragon getParent() {
        return (EnderDragon) super.getParent();
    }

    @Override
    public net.minecraft.entity.boss.EntityDragonPart getHandle() {
        return (net.minecraft.entity.boss.EntityDragonPart) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderDragonPart";
    }

    public void damage(final double amount) {
        getParent().damage(amount);
    }

    public void damage(final double amount, final Entity source) {
        getParent().damage(amount, source);
    }

    public double getHealth() {
        return getParent().getHealth();
    }

    public void setHealth(final double health) {
        getParent().setHealth(health);
    }

    public double getMaxHealth() {
        return getParent().getMaxHealth();
    }

    public void setMaxHealth(final double health) {
        getParent().setMaxHealth(health);
    }

    public void resetMaxHealth() {
        getParent().resetMaxHealth();
    }

    @Deprecated
    public void _INVALID_damage(final int amount) {
        damage(amount);
    }

    @Deprecated
    public void _INVALID_damage(final int amount, final Entity source) {
        damage(amount, source);
    }

    @Deprecated
    public int _INVALID_getHealth() {
        return NumberConversions.ceil(getHealth());
    }

    @Deprecated
    public void _INVALID_setHealth(final int health) {
        setHealth(health);
    }

    @Deprecated
    public int _INVALID_getMaxHealth() {
        return NumberConversions.ceil(getMaxHealth());
    }

    @Deprecated
    public void _INVALID_setMaxHealth(final int health) {
        setMaxHealth(health);
    }
}
