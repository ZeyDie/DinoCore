package org.bukkit.craftbukkit.v1_6_R3.entity;


import org.apache.commons.lang.Validate;
import org.bukkit.Rotation;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;

public class CraftItemFrame extends CraftHanging implements ItemFrame {
    public CraftItemFrame(final CraftServer server, final net.minecraft.entity.item.EntityItemFrame entity) {
        super(server, entity);
    }

    public void setItem(final org.bukkit.inventory.ItemStack item) {
        if (item == null || item.getTypeId() == 0) {
            getHandle().getDataWatcher().addObjectByDataType(2, 5);
            getHandle().getDataWatcher().setObjectWatched(2);
        } else {
            getHandle().setDisplayedItem(CraftItemStack.asNMSCopy(item));
        }
    }

    public org.bukkit.inventory.ItemStack getItem() {
        return CraftItemStack.asBukkitCopy(getHandle().getDisplayedItem());
    }

    public Rotation getRotation() {
        return toBukkitRotation(getHandle().getRotation());
    }

    Rotation toBukkitRotation(final int value) {
        // Translate NMS rotation integer to Bukkit API
        switch (value) {
        case 0:
            return Rotation.NONE;
        case 1:
            return Rotation.CLOCKWISE;
        case 2:
            return Rotation.FLIPPED;
        case 3:
            return Rotation.COUNTER_CLOCKWISE;
        default:
            throw new AssertionError("Unknown rotation " + value + " for " + getHandle());
        }
    }

    public void setRotation(final Rotation rotation) {
        Validate.notNull(rotation, "Rotation cannot be null");
        getHandle().setItemRotation(toInteger(rotation));
    }

    static int toInteger(final Rotation rotation) {
        // Translate Bukkit API rotation to NMS integer
        switch (rotation) {
        case NONE:
            return 0;
        case CLOCKWISE:
            return 1;
        case FLIPPED:
            return 2;
        case COUNTER_CLOCKWISE:
            return 3;
        default:
            throw new IllegalArgumentException(rotation + " is not applicable to an ItemFrame");
        }
    }

    @Override
    public net.minecraft.entity.item.EntityItemFrame getHandle() {
        return (net.minecraft.entity.item.EntityItemFrame) entity;
    }

    @Override
    public String toString() {
        return "CraftItemFrame{item=" + getItem() + ", rotation=" + getRotation() + "}";
    }

    public EntityType getType() {
        return EntityType.ITEM_FRAME;
    }
}
