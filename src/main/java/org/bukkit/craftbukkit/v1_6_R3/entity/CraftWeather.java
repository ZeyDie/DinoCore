package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Weather;

public class CraftWeather extends CraftEntity implements Weather {
    public CraftWeather(final CraftServer server, final net.minecraft.entity.effect.EntityWeatherEffect entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.effect.EntityWeatherEffect getHandle() {
        return (net.minecraft.entity.effect.EntityWeatherEffect) entity;
    }

    @Override
    public String toString() {
        return "CraftWeather";
    }

    public EntityType getType() {
        return EntityType.WEATHER;
    }
}
