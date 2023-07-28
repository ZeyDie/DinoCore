package org.bukkit.craftbukkit.v1_6_R3.inventory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_6_R3.Overridden;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftMetaItem.ItemMetaKey.Specific;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Children must include the following:
 *
 * <li> Constructor(CraftMetaItem meta)
 * <li> Constructor(NBTTagCompound tag)
 * <li> Constructor(Map<String, Object> map)
 * <br><br>
 * <li> void applyToItem(NBTTagCompound tag)
 * <li> boolean applicableTo(Material type)
 * <br><br>
 * <li> boolean equalsCommon(CraftMetaItem meta)
 * <li> boolean notUncommon(CraftMetaItem meta)
 * <br><br>
 * <li> boolean isEmpty()
 * <li> boolean is{Type}Empty()
 * <br><br>
 * <li> int applyHash()
 * <li> public Class clone()
 * <br><br>
 * <li> Builder<String, Object> serialize(Builder<String, Object> builder)
 * <li> SerializableMeta.Deserializers deserializer()
 */
@DelegateDeserialization(CraftMetaItem.SerializableMeta.class)
class CraftMetaItem implements ItemMeta, Repairable {

    static class ItemMetaKey {

        @Retention(RetentionPolicy.SOURCE)
        @Target(ElementType.FIELD)
        @interface Specific {
            enum To {
                BUKKIT,
                NBT,
                ;
            }
            To value();
        }

        final String BUKKIT;
        final String NBT;

        ItemMetaKey(final String both) {
            this(both, both);
        }

        ItemMetaKey(final String nbt, final String bukkit) {
            this.NBT = nbt;
            this.BUKKIT = bukkit;
        }
    }

    @SerializableAs("ItemMeta")
    public static class SerializableMeta implements ConfigurationSerializable {
        static final String TYPE_FIELD = "meta-type";

        static final ImmutableMap<Class<? extends CraftMetaItem>, String> classMap;
        static final ImmutableMap<String, Constructor<? extends CraftMetaItem>> constructorMap;

        static {
            classMap = ImmutableMap.<Class<? extends CraftMetaItem>, String>builder()
                    .put(CraftMetaBook.class, "BOOK")
                    .put(CraftMetaSkull.class, "SKULL")
                    .put(CraftMetaLeatherArmor.class, "LEATHER_ARMOR")
                    .put(CraftMetaMap.class, "MAP")
                    .put(CraftMetaPotion.class, "POTION")
                    .put(CraftMetaEnchantedBook.class, "ENCHANTED")
                    .put(CraftMetaFirework.class, "FIREWORK")
                    .put(CraftMetaCharge.class, "FIREWORK_EFFECT")
                    .put(CraftMetaItem.class, "UNSPECIFIC")
                    .build();

            final ImmutableMap.Builder<String, Constructor<? extends CraftMetaItem>> classConstructorBuilder = ImmutableMap.builder();
            for (final Map.Entry<Class<? extends CraftMetaItem>, String> mapping : classMap.entrySet()) {
                try {
                    classConstructorBuilder.put(mapping.getValue(), mapping.getKey().getDeclaredConstructor(Map.class));
                } catch (final NoSuchMethodException e) {
                    throw new AssertionError(e);
                }
            }
            constructorMap = classConstructorBuilder.build();
        }

        private SerializableMeta() {
        }

        public static ItemMeta deserialize(final Map<String, Object> map) throws Throwable {
            Validate.notNull(map, "Cannot deserialize null map");

            final String type = getString(map, TYPE_FIELD, false);
            final Constructor<? extends CraftMetaItem> constructor = constructorMap.get(type);

            if (constructor == null) {
                throw new IllegalArgumentException(type + " is not a valid " + TYPE_FIELD);
            }

            try {
                return constructor.newInstance(map);
            } catch (final InstantiationException e) {
                throw new AssertionError(e);
            } catch (final IllegalAccessException e) {
                throw new AssertionError(e);
            } catch (final InvocationTargetException e) {
                throw e.getCause();
            }
        }

        public Map<String, Object> serialize() {
            throw new AssertionError();
        }

        static String getString(final Map<?, ?> map, final Object field, final boolean nullable) {
            return getObject(String.class, map, field, nullable);
        }

        static boolean getBoolean(final Map<?, ?> map, final Object field) {
            final Boolean value = getObject(Boolean.class, map, field, true);
            return value != null && value;
        }

        static <T> T getObject(final Class<T> clazz, final Map<?, ?> map, final Object field, final boolean nullable) {
            final Object object = map.get(field);

            if (clazz.isInstance(object)) {
                return clazz.cast(object);
            }
            if (object == null) {
                if (!nullable) {
                    throw new NoSuchElementException(map + " does not contain " + field);
                }
                return null;
            }
            throw new IllegalArgumentException(field + "(" + object + ") is not a valid " + clazz);
        }
    }

    static final ItemMetaKey NAME = new ItemMetaKey("Name", "display-name");
    @Specific(Specific.To.NBT)
    static final ItemMetaKey DISPLAY = new ItemMetaKey("display");
    static final ItemMetaKey LORE = new ItemMetaKey("Lore", "lore");
    static final ItemMetaKey ENCHANTMENTS = new ItemMetaKey("ench", "enchants");
    @Specific(Specific.To.NBT)
    static final ItemMetaKey ENCHANTMENTS_ID = new ItemMetaKey("id");
    @Specific(Specific.To.NBT)
    static final ItemMetaKey ENCHANTMENTS_LVL = new ItemMetaKey("lvl");
    static final ItemMetaKey REPAIR = new ItemMetaKey("RepairCost", "repair-cost");
    @Specific(Specific.To.NBT)
    static final ItemMetaKey ATTRIBUTES = new ItemMetaKey("AttributeModifiers");
    @Specific(Specific.To.NBT)
    static final ItemMetaKey ATTRIBUTES_IDENTIFIER = new ItemMetaKey("AttributeName");
    @Specific(Specific.To.NBT)
    static final ItemMetaKey ATTRIBUTES_NAME = new ItemMetaKey("Name");
    @Specific(Specific.To.NBT)
    static final ItemMetaKey ATTRIBUTES_VALUE = new ItemMetaKey("Amount");
    @Specific(Specific.To.NBT)
    static final ItemMetaKey ATTRIBUTES_TYPE = new ItemMetaKey("Operation");
    @Specific(Specific.To.NBT)
    static final ItemMetaKey ATTRIBUTES_UUID_HIGH = new ItemMetaKey("UUIDMost");
    @Specific(Specific.To.NBT)
    static final ItemMetaKey ATTRIBUTES_UUID_LOW = new ItemMetaKey("UUIDLeast");

    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private int repairCost;
    private final net.minecraft.nbt.NBTTagList attributes;

    CraftMetaItem(final CraftMetaItem meta) {
        if (meta == null) {
            attributes = null;
            return;
        }

        this.displayName = meta.displayName;

        if (meta.hasLore()) {
            this.lore = new ArrayList<String>(meta.lore);
        }

        if (meta.hasEnchants()) {
            this.enchantments = new HashMap<Enchantment, Integer>(meta.enchantments);
        }

        this.repairCost = meta.repairCost;
        this.attributes = meta.attributes;
    }

    CraftMetaItem(final net.minecraft.nbt.NBTTagCompound tag) {
        if (tag.hasKey(DISPLAY.NBT)) {
            final net.minecraft.nbt.NBTTagCompound display = tag.getCompoundTag(DISPLAY.NBT);

            if (display.hasKey(NAME.NBT)) {
                displayName = display.getString(NAME.NBT);
            }

            if (display.hasKey(LORE.NBT)) {
                final net.minecraft.nbt.NBTTagList list = display.getTagList(LORE.NBT);
                lore = new ArrayList<String>(list.tagCount());

                for (int index = 0; index < list.tagCount(); index++) {
                    final String line = ((net.minecraft.nbt.NBTTagString) list.tagAt(index)).data;
                    lore.add(line);
                }
            }
        }

        this.enchantments = buildEnchantments(tag, ENCHANTMENTS);

        if (tag.hasKey(REPAIR.NBT)) {
            repairCost = tag.getInteger(REPAIR.NBT);
        }


        if (tag.getTag(ATTRIBUTES.NBT) instanceof net.minecraft.nbt.NBTTagList) {
            net.minecraft.nbt.NBTTagList save = null;
            final net.minecraft.nbt.NBTTagList nbttaglist = tag.getTagList(ATTRIBUTES.NBT);

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                if (!(nbttaglist.tagAt(i) instanceof net.minecraft.nbt.NBTTagCompound)) {
                    continue;
                }
                final net.minecraft.nbt.NBTTagCompound nbttagcompound = (net.minecraft.nbt.NBTTagCompound) nbttaglist.tagAt(i);

                if (!(nbttagcompound.getTag(ATTRIBUTES_UUID_HIGH.NBT) instanceof net.minecraft.nbt.NBTTagLong)) {
                    continue;
                }
                if (!(nbttagcompound.getTag(ATTRIBUTES_UUID_LOW.NBT) instanceof net.minecraft.nbt.NBTTagLong)) {
                    continue;
                }
                if (!(nbttagcompound.getTag(ATTRIBUTES_IDENTIFIER.NBT) instanceof net.minecraft.nbt.NBTTagString) || !CraftItemFactory.KNOWN_NBT_ATTRIBUTE_NAMES.contains(nbttagcompound.getString(ATTRIBUTES_IDENTIFIER.NBT))) {
                    continue;
                }
                if (!(nbttagcompound.getTag(ATTRIBUTES_NAME.NBT) instanceof net.minecraft.nbt.NBTTagString) || nbttagcompound.getString(ATTRIBUTES_NAME.NBT).isEmpty()) {
                    continue;
                }
                if (!(nbttagcompound.getTag(ATTRIBUTES_VALUE.NBT) instanceof net.minecraft.nbt.NBTTagDouble)) {
                    continue;
                }
                if (!(nbttagcompound.getTag(ATTRIBUTES_TYPE.NBT) instanceof net.minecraft.nbt.NBTTagInt) || nbttagcompound.getInteger(ATTRIBUTES_TYPE.NBT) < 0 || nbttagcompound.getInteger(ATTRIBUTES_TYPE.NBT) > 2) {
                    continue;
                }

                if (save == null) {
                    save = new net.minecraft.nbt.NBTTagList(ATTRIBUTES.NBT);
                }

                final net.minecraft.nbt.NBTTagCompound entry = new net.minecraft.nbt.NBTTagCompound();
                entry.setTag(ATTRIBUTES_UUID_HIGH.NBT, nbttagcompound.getTag(ATTRIBUTES_UUID_HIGH.NBT));
                entry.setTag(ATTRIBUTES_UUID_LOW.NBT, nbttagcompound.getTag(ATTRIBUTES_UUID_LOW.NBT));
                entry.setTag(ATTRIBUTES_IDENTIFIER.NBT, nbttagcompound.getTag(ATTRIBUTES_IDENTIFIER.NBT));
                entry.setTag(ATTRIBUTES_NAME.NBT, nbttagcompound.getTag(ATTRIBUTES_NAME.NBT));
                entry.setTag(ATTRIBUTES_VALUE.NBT, nbttagcompound.getTag(ATTRIBUTES_VALUE.NBT));
                entry.setTag(ATTRIBUTES_TYPE.NBT, nbttagcompound.getTag(ATTRIBUTES_TYPE.NBT));
                save.appendTag(entry);
            }

            attributes = save;
        } else {
            attributes = null;
        }
    }

    static Map<Enchantment, Integer> buildEnchantments(final net.minecraft.nbt.NBTTagCompound tag, final ItemMetaKey key) {
        if (!tag.hasKey(key.NBT)) {
            return null;
        }

        final net.minecraft.nbt.NBTTagList ench = tag.getTagList(key.NBT);
        final Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>(ench.tagCount());

        for (int i = 0; i < ench.tagCount(); i++) {
            final int id = 0xffff & ((net.minecraft.nbt.NBTTagCompound) ench.tagAt(i)).getShort(ENCHANTMENTS_ID.NBT);
            final int level = 0xffff & ((net.minecraft.nbt.NBTTagCompound) ench.tagAt(i)).getShort(ENCHANTMENTS_LVL.NBT);

            enchantments.put(Enchantment.getById(id), level);
        }

        return enchantments;
    }

    CraftMetaItem(final Map<String, Object> map) {
        setDisplayName(SerializableMeta.getString(map, NAME.BUKKIT, true));

        final Iterable<?> lore = SerializableMeta.getObject(Iterable.class, map, LORE.BUKKIT, true);
        if (lore != null) {
            safelyAdd(lore, this.lore = new ArrayList<String>(), Integer.MAX_VALUE);
        }

        enchantments = buildEnchantments(map, ENCHANTMENTS);

        final Integer repairCost = SerializableMeta.getObject(Integer.class, map, REPAIR.BUKKIT, true);
        if (repairCost != null) {
            setRepairCost(repairCost);
        }

        attributes = null;
    }

    static Map<Enchantment, Integer> buildEnchantments(final Map<String, Object> map, final ItemMetaKey key) {
        final Map<?, ?> ench = SerializableMeta.getObject(Map.class, map, key.BUKKIT, true);
        if (ench == null) {
            return null;
        }

        final Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>(ench.size());
        for (final Map.Entry<?, ?> entry : ench.entrySet()) {
            final Enchantment enchantment = Enchantment.getByName(entry.getKey().toString());

            if ((enchantment != null) && (entry.getValue() instanceof Integer)) {
                enchantments.put(enchantment, (Integer) entry.getValue());
            }
        }

        return enchantments;
    }

    @Overridden
    void applyToItem(final net.minecraft.nbt.NBTTagCompound itemTag) {
        if (hasDisplayName()) {
            setDisplayTag(itemTag, NAME.NBT, new net.minecraft.nbt.NBTTagString(NAME.NBT, displayName));
        }

        if (hasLore()) {
            setDisplayTag(itemTag, LORE.NBT, createStringList(lore, LORE));
        }

        applyEnchantments(enchantments, itemTag, ENCHANTMENTS);

        if (hasRepairCost()) {
            itemTag.setInteger(REPAIR.NBT, repairCost);
        }

        if (attributes != null) {
            itemTag.setTag(ATTRIBUTES.NBT, attributes);
        }
    }

    static net.minecraft.nbt.NBTTagList createStringList(final List<String> list, final ItemMetaKey key) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        final net.minecraft.nbt.NBTTagList tagList = new net.minecraft.nbt.NBTTagList(key.NBT);
        for (final String value : list) {
            tagList.appendTag(new net.minecraft.nbt.NBTTagString("", value));
        }

        return tagList;
    }

    static void applyEnchantments(final Map<Enchantment, Integer> enchantments, final net.minecraft.nbt.NBTTagCompound tag, final ItemMetaKey key) {
        if (enchantments == null || enchantments.isEmpty()) {
            return;
        }

        final net.minecraft.nbt.NBTTagList list = new net.minecraft.nbt.NBTTagList(key.NBT);

        for (final Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            final net.minecraft.nbt.NBTTagCompound subtag = new net.minecraft.nbt.NBTTagCompound();

            subtag.setShort(ENCHANTMENTS_ID.NBT, (short) entry.getKey().getId());
            subtag.setShort(ENCHANTMENTS_LVL.NBT, entry.getValue().shortValue());

            list.appendTag(subtag);
        }

        tag.setTag(key.NBT, list);
    }

    void setDisplayTag(final net.minecraft.nbt.NBTTagCompound tag, final String key, final net.minecraft.nbt.NBTBase value) {
        final net.minecraft.nbt.NBTTagCompound display = tag.getCompoundTag(DISPLAY.NBT);

        if (!tag.hasKey(DISPLAY.NBT)) {
            tag.setCompoundTag(DISPLAY.NBT, display);
        }

        display.setTag(key, value);
    }

    @Overridden
    boolean applicableTo(final Material type) {
        return type != Material.AIR;
    }

    @Overridden
    boolean isEmpty() {
        return !(hasDisplayName() || hasEnchants() || hasLore() || hasAttributes());
    }

    public String getDisplayName() {
        return displayName;
    }

    public final void setDisplayName(final String name) {
        this.displayName = name;
    }

    public boolean hasDisplayName() {
        return !Strings.isNullOrEmpty(displayName);
    }

    public boolean hasLore() {
        return this.lore != null && !this.lore.isEmpty();
    }

    public boolean hasAttributes() {
        return this.attributes != null;
    }

    public boolean hasRepairCost() {
        return repairCost > 0;
    }

    public boolean hasEnchant(final Enchantment ench) {
        return hasEnchants() && enchantments.containsKey(ench);
    }

    public int getEnchantLevel(final Enchantment ench) {
        final Integer level = hasEnchants() ? enchantments.get(ench) : null;
        if (level == null) {
            return 0;
        }
        return level;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return hasEnchants() ? ImmutableMap.copyOf(enchantments) : ImmutableMap.<Enchantment, Integer>of();
    }

    public boolean addEnchant(final Enchantment ench, final int level, final boolean ignoreRestrictions) {
        if (enchantments == null) {
            enchantments = new HashMap<Enchantment, Integer>(4);
        }

        if (ignoreRestrictions || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
            final Integer old = enchantments.put(ench, level);
            return old == null || old != level;
        }
        return false;
    }

    public boolean removeEnchant(final Enchantment ench) {
        return hasEnchants() && enchantments.remove(ench) != null;
    }

    public boolean hasEnchants() {
        return !(enchantments == null || enchantments.isEmpty());
    }

    public boolean hasConflictingEnchant(final Enchantment ench) {
        return checkConflictingEnchants(enchantments, ench);
    }

    public List<String> getLore() {
        return this.lore == null ? null : new ArrayList<String>(this.lore);
    }

    public void setLore(final List<String> lore) { // too tired to think if .clone is better
        if (lore == null) {
            this.lore = null;
        } else {
            if (this.lore == null) {
                safelyAdd(lore, this.lore = new ArrayList<String>(lore.size()), Integer.MAX_VALUE);
            } else {
                this.lore.clear();
                safelyAdd(lore, this.lore, Integer.MAX_VALUE);
            }
        }
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(final int cost) { // TODO: Does this have limits?
        repairCost = cost;
    }

    @Override
    public final boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (!(object instanceof CraftMetaItem)) {
            return false;
        }
        return CraftItemFactory.instance().equals(this, (ItemMeta) object);
    }

    /**
     * This method is almost as weird as notUncommon.
     * Only return false if your common internals are unequal.
     * Checking your own internals is redundant if you are not common, as notUncommon is meant for checking those 'not common' variables.
     */
    @Overridden
    boolean equalsCommon(final CraftMetaItem that) {
        return ((this.hasDisplayName() ? that.hasDisplayName() && this.displayName.equals(that.displayName) : !that.hasDisplayName()))
                && (this.hasEnchants() ? that.hasEnchants() && this.enchantments.equals(that.enchantments) : !that.hasEnchants())
                && (this.hasLore() ? that.hasLore() && this.lore.equals(that.lore) : !that.hasLore())
                && (this.hasAttributes() ? that.hasAttributes() && this.attributes.equals(that.attributes) : !that.hasAttributes())
                && (this.hasRepairCost() ? that.hasRepairCost() && this.repairCost == that.repairCost : !that.hasRepairCost());
    }

    /**
     * This method is a bit weird...
     * Return true if you are a common class OR your uncommon parts are empty.
     * Empty uncommon parts implies the NBT data would be equivalent if both were applied to an item
     */
    @Overridden
    boolean notUncommon(final CraftMetaItem meta) {
        return true;
    }

    @Override
    public final int hashCode() {
        return applyHash();
    }

    @Overridden
    int applyHash() {
        int hash = 3;
        hash = 61 * hash + (hasDisplayName() ? this.displayName.hashCode() : 0);
        hash = 61 * hash + (hasLore() ? this.lore.hashCode() : 0);
        hash = 61 * hash + (hasEnchants() ? this.enchantments.hashCode() : 0);
        hash = 61 * hash + (hasAttributes() ? this.attributes.hashCode() : 0);
        hash = 61 * hash + (hasRepairCost() ? this.repairCost : 0);
        return hash;
    }

    @Overridden
    @Override
    public CraftMetaItem clone() {
        try {
            final CraftMetaItem clone = (CraftMetaItem) super.clone();
            if (this.lore != null) {
                clone.lore = new ArrayList<String>(this.lore);
            }
            if (this.enchantments != null) {
                clone.enchantments = new HashMap<Enchantment, Integer>(this.enchantments);
            }
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public final Map<String, Object> serialize() {
        final ImmutableMap.Builder<String, Object> map = ImmutableMap.builder();
        map.put(SerializableMeta.TYPE_FIELD, SerializableMeta.classMap.get(getClass()));
        serialize(map);
        return map.build();
    }

    @Overridden
    ImmutableMap.Builder<String, Object> serialize(final ImmutableMap.Builder<String, Object> builder) {
        if (hasDisplayName()) {
            builder.put(NAME.BUKKIT, displayName);
        }

        if (hasLore()) {
            builder.put(LORE.BUKKIT, ImmutableList.copyOf(lore));
        }

        serializeEnchantments(enchantments, builder, ENCHANTMENTS);

        if (hasRepairCost()) {
            builder.put(REPAIR.BUKKIT, repairCost);
        }

        return builder;
    }

    static void serializeEnchantments(final Map<Enchantment, Integer> enchantments, final ImmutableMap.Builder<String, Object> builder, final ItemMetaKey key) {
        if (enchantments == null || enchantments.isEmpty()) {
            return;
        }

        final ImmutableMap.Builder<String, Integer> enchants = ImmutableMap.builder();
        for (final Map.Entry<? extends Enchantment, Integer> enchant : enchantments.entrySet()) {
            enchants.put(enchant.getKey().getName(), enchant.getValue());
        }

        builder.put(key.BUKKIT, enchants.build());
    }

    static void safelyAdd(final Iterable<?> addFrom, final Collection<String> addTo, final int maxItemLength) {
        if (addFrom == null) {
            return;
        }

        for (final Object object : addFrom) {
            if (!(object instanceof String)) {
                if (object != null) {
                    throw new IllegalArgumentException(addFrom + " cannot contain non-string " + object.getClass().getName());
                }

                addTo.add("");
            } else {
                String page = object.toString();

                if (page.length() > maxItemLength) {
                    page = page.substring(0, maxItemLength);
                }

                addTo.add(page);
            }
        }
    }

    static boolean checkConflictingEnchants(final Map<Enchantment, Integer> enchantments, final Enchantment ench) {
        if (enchantments == null || enchantments.isEmpty()) {
            return false;
        }

        for (final Enchantment enchant : enchantments.keySet()) {
            if (enchant.conflictsWith(ench)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final String toString() {
        return SerializableMeta.classMap.get(getClass()) + "_META:" + serialize(); // TODO: cry
    }
}
