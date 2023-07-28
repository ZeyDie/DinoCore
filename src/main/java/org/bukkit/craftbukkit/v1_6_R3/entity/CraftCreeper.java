package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreeperPowerEvent;

public class CraftCreeper extends CraftMonster implements Creeper {

    public CraftCreeper(final CraftServer server, final net.minecraft.entity.monster.EntityCreeper entity) {
        super(server, entity);
    }

    public boolean isPowered() {
        return getHandle().getPowered();
    }

    public void setPowered(final boolean powered) {
        final CraftServer server = this.server;
        final Creeper entity = (Creeper) this.getHandle().getBukkitEntity();

        if (powered) {
            final CreeperPowerEvent event = new CreeperPowerEvent(entity, CreeperPowerEvent.PowerCause.SET_ON);
            server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                getHandle().setPowered(true);
            }
        } else {
            final CreeperPowerEvent event = new CreeperPowerEvent(entity, CreeperPowerEvent.PowerCause.SET_OFF);
            server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                getHandle().setPowered(false);
            }
        }
    }

    @Override
    public net.minecraft.entity.monster.EntityCreeper getHandle() {
        return (net.minecraft.entity.monster.EntityCreeper) entity;
    }

    @Override
    public String toString() {
        return "CraftCreeper";
    }

    public EntityType getType() {
        return EntityType.CREEPER;
    }
}
