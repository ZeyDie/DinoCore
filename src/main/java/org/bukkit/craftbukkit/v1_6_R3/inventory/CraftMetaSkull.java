package org.bukkit.craftbukkit.v1_6_R3.inventory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap.Builder;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaSkull extends CraftMetaItem implements SkullMeta {
    static final ItemMetaKey SKULL_OWNER = new ItemMetaKey("SkullOwner", "skull-owner");
    static final int MAX_OWNER_LENGTH = 16;

    private String player;

    CraftMetaSkull(final CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaSkull)) {
            return;
        }
        final CraftMetaSkull skullMeta = (CraftMetaSkull) meta;
        this.player = skullMeta.player;
    }

    CraftMetaSkull(final net.minecraft.nbt.NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey(SKULL_OWNER.NBT)) {
            player = tag.getString(SKULL_OWNER.NBT);
        }
    }

    CraftMetaSkull(final Map<String, Object> map) {
        super(map);
        setOwner(SerializableMeta.getString(map, SKULL_OWNER.BUKKIT, true));
    }

    @Override
    void applyToItem(final net.minecraft.nbt.NBTTagCompound tag) {
        super.applyToItem(tag);

        if (hasOwner()) {
            tag.setString(SKULL_OWNER.NBT, player);
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isSkullEmpty();
    }

    boolean isSkullEmpty() {
        return !(hasOwner());
    }

    @Override
    boolean applicableTo(final Material type) {
        switch(type) {
            case SKULL_ITEM:
                return true;
            default:
                return false;
        }
    }

    @Override
    public CraftMetaSkull clone() {
        return (CraftMetaSkull) super.clone();
    }

    public boolean hasOwner() {
        return !Strings.isNullOrEmpty(player);
    }

    public String getOwner() {
        return player;
    }

    public boolean setOwner(final String name) {
        if (name != null && name.length() > MAX_OWNER_LENGTH) {
            return false;
        }
        player = name;
        return true;
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasOwner()) {
            hash = 61 * hash + player.hashCode();
        }
        return original != hash ? CraftMetaSkull.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(final CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaSkull) {
            final CraftMetaSkull that = (CraftMetaSkull) meta;

            return (this.hasOwner() ? that.hasOwner() && this.player.equals(that.player) : !that.hasOwner());
        }
        return true;
    }

    @Override
    boolean notUncommon(final CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaSkull || isSkullEmpty());
    }

    @Override
    Builder<String, Object> serialize(final Builder<String, Object> builder) {
        super.serialize(builder);
        if (hasOwner()) {
            return builder.put(SKULL_OWNER.BUKKIT, this.player);
        }
        return builder;
    }
}
