package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

public class CraftEnderman extends CraftMonster implements Enderman {
    public CraftEnderman(final CraftServer server, final net.minecraft.entity.monster.EntityEnderman entity) {
        super(server, entity);
    }

    public MaterialData getCarriedMaterial() {
        return Material.getMaterial(getHandle().getCarried()).getNewData((byte) getHandle().getCarryingData());
    }

    public void setCarriedMaterial(final MaterialData data) {
        getHandle().setCarried(data.getItemTypeId());
        getHandle().setCarryingData(data.getData());
    }

    @Override
    public net.minecraft.entity.monster.EntityEnderman getHandle() {
        return (net.minecraft.entity.monster.EntityEnderman) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderman";
    }

    public EntityType getType() {
        return EntityType.ENDERMAN;
    }
}
