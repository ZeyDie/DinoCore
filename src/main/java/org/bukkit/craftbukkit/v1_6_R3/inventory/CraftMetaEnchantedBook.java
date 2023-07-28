package org.bukkit.craftbukkit.v1_6_R3.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaEnchantedBook extends CraftMetaItem implements EnchantmentStorageMeta {
    static final ItemMetaKey STORED_ENCHANTMENTS = new ItemMetaKey("StoredEnchantments", "stored-enchants");

    private Map<Enchantment, Integer> enchantments;

    CraftMetaEnchantedBook(final CraftMetaItem meta) {
        super(meta);

        if (!(meta instanceof CraftMetaEnchantedBook)) {
            return;
        }

        final CraftMetaEnchantedBook that = (CraftMetaEnchantedBook) meta;

        if (that.hasEnchants()) {
            this.enchantments = new HashMap<Enchantment, Integer>(that.enchantments);
        }
    }

    CraftMetaEnchantedBook(final net.minecraft.nbt.NBTTagCompound tag) {
        super(tag);

        if (!tag.hasKey(STORED_ENCHANTMENTS.NBT)) {
            return;
        }

        enchantments = buildEnchantments(tag, STORED_ENCHANTMENTS);
    }

    CraftMetaEnchantedBook(final Map<String, Object> map) {
        super(map);

        enchantments = buildEnchantments(map, STORED_ENCHANTMENTS);
    }

    @Override
    void applyToItem(final net.minecraft.nbt.NBTTagCompound itemTag) {
        super.applyToItem(itemTag);

        applyEnchantments(enchantments, itemTag, STORED_ENCHANTMENTS);
    }

    @Override
    boolean applicableTo(final Material type) {
        switch (type) {
            case ENCHANTED_BOOK:
                return true;
            default:
                return false;
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isEnchantedEmpty();
    }

    @Override
    boolean equalsCommon(final CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaEnchantedBook) {
            final CraftMetaEnchantedBook that = (CraftMetaEnchantedBook) meta;

            return (hasStoredEnchants() ? that.hasStoredEnchants() && this.enchantments.equals(that.enchantments) : !that.hasStoredEnchants());
        }
        return true;
    }

    @Override
    boolean notUncommon(final CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaEnchantedBook || isEnchantedEmpty());
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();

        if (hasStoredEnchants()) {
            hash = 61 * hash + enchantments.hashCode();
        }

        return original != hash ? CraftMetaEnchantedBook.class.hashCode() ^ hash : hash;
    }

    @Override
    public CraftMetaEnchantedBook clone() {
        final CraftMetaEnchantedBook meta = (CraftMetaEnchantedBook) super.clone();

        if (this.enchantments != null) {
            meta.enchantments = new HashMap<Enchantment, Integer>(this.enchantments);
        }

        return meta;
    }

    @Override
    Builder<String, Object> serialize(final Builder<String, Object> builder) {
        super.serialize(builder);

        serializeEnchantments(enchantments, builder, STORED_ENCHANTMENTS);

        return builder;
    }

    boolean isEnchantedEmpty() {
        return !hasStoredEnchants();
    }

    public boolean hasStoredEnchant(final Enchantment ench) {
        return hasStoredEnchants() && enchantments.containsKey(ench);
    }

    public int getStoredEnchantLevel(final Enchantment ench) {
        final Integer level = hasStoredEnchants() ? enchantments.get(ench) : null;
        if (level == null) {
            return 0;
        }
        return level;
    }

    public Map<Enchantment, Integer> getStoredEnchants() {
        return hasStoredEnchants() ? ImmutableMap.copyOf(enchantments) : ImmutableMap.<Enchantment, Integer>of();
    }

    public boolean addStoredEnchant(final Enchantment ench, final int level, final boolean ignoreRestrictions) {
        if (enchantments == null) {
            enchantments = new HashMap<Enchantment, Integer>(4);
        }

        if (ignoreRestrictions || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
            final Integer old = enchantments.put(ench, level);
            return old == null || old != level;
        }
        return false;
    }

    public boolean removeStoredEnchant(final Enchantment ench) {
        return hasStoredEnchants() && enchantments.remove(ench) != null;
    }

    public boolean hasStoredEnchants() {
        return !(enchantments == null || enchantments.isEmpty());
    }

    public boolean hasConflictingStoredEnchant(final Enchantment ench) {
        return checkConflictingEnchants(enchantments, ench);
    }
}
