package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;

public class CraftOcelot extends CraftTameableAnimal implements Ocelot {
    public CraftOcelot(final CraftServer server, final net.minecraft.entity.passive.EntityOcelot wolf) {
        super(server, wolf);
    }

    @Override
    public net.minecraft.entity.passive.EntityOcelot getHandle() {
        return (net.minecraft.entity.passive.EntityOcelot) entity;
    }

    public Type getCatType() {
        return Type.getType(getHandle().getTameSkin());
    }

    public void setCatType(final Type type) {
        Validate.notNull(type, "Cat type cannot be null");
        getHandle().setTameSkin(type.getId());
    }

    @Override
    public EntityType getType() {
        return EntityType.OCELOT;
    }
}
