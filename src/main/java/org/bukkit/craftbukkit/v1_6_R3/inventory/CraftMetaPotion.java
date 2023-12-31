package org.bukkit.craftbukkit.v1_6_R3.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaPotion extends CraftMetaItem implements PotionMeta {
    static final ItemMetaKey AMPLIFIER = new ItemMetaKey("Amplifier", "amplifier");
    static final ItemMetaKey AMBIENT = new ItemMetaKey("Ambient", "ambient");
    static final ItemMetaKey DURATION = new ItemMetaKey("Duration", "duration");
    static final ItemMetaKey POTION_EFFECTS = new ItemMetaKey("CustomPotionEffects", "custom-effects");
    static final ItemMetaKey ID = new ItemMetaKey("Id", "potion-id");

    private List<PotionEffect> customEffects;

    CraftMetaPotion(final CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaPotion)) {
            return;
        }
        final CraftMetaPotion potionMeta = (CraftMetaPotion) meta;
        if (potionMeta.hasCustomEffects()) {
            this.customEffects = new ArrayList<PotionEffect>(potionMeta.customEffects);
        }
    }

    CraftMetaPotion(final net.minecraft.nbt.NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey(POTION_EFFECTS.NBT)) {
            final net.minecraft.nbt.NBTTagList list = tag.getTagList(POTION_EFFECTS.NBT);
            final int length = list.tagCount();
            if (length > 0) {
                customEffects = new ArrayList<PotionEffect>(length);

                for (int i = 0; i < length; i++) {
                    final net.minecraft.nbt.NBTTagCompound effect = (net.minecraft.nbt.NBTTagCompound) list.tagAt(i);
                    final PotionEffectType type = PotionEffectType.getById(effect.getByte(ID.NBT));
                    final int amp = effect.getByte(AMPLIFIER.NBT);
                    final int duration = effect.getInteger(DURATION.NBT);
                    final boolean ambient = effect.getBoolean(AMBIENT.NBT);
                    customEffects.add(new PotionEffect(type, duration, amp, ambient));
                }
            }
        }
    }

    CraftMetaPotion(final Map<String, Object> map) {
        super(map);

        final Iterable<?> rawEffectList = SerializableMeta.getObject(Iterable.class, map, POTION_EFFECTS.BUKKIT, true);
        if (rawEffectList == null) {
            return;
        }

        for (final Object obj : rawEffectList) {
            if (!(obj instanceof PotionEffect)) {
                throw new IllegalArgumentException("Object in effect list is not valid. " + obj.getClass());
            }
            addCustomEffect((PotionEffect) obj, true);
        }
    }

    @Override
    void applyToItem(final net.minecraft.nbt.NBTTagCompound tag) {
        super.applyToItem(tag);
        if (hasCustomEffects()) {
            final net.minecraft.nbt.NBTTagList effectList = new net.minecraft.nbt.NBTTagList();
            tag.setTag(POTION_EFFECTS.NBT, effectList);

            for (final PotionEffect effect : customEffects) {
                final net.minecraft.nbt.NBTTagCompound effectData = new net.minecraft.nbt.NBTTagCompound();
                effectData.setByte(ID.NBT, (byte) effect.getType().getId());
                effectData.setByte(AMPLIFIER.NBT, (byte) effect.getAmplifier());
                effectData.setInteger(DURATION.NBT, effect.getDuration());
                effectData.setBoolean(AMBIENT.NBT, effect.isAmbient());
                effectList.appendTag(effectData);
            }
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isPotionEmpty();
    }

    boolean isPotionEmpty() {
        return !(hasCustomEffects());
    }

    @Override
    boolean applicableTo(final Material type) {
        switch(type) {
            case POTION:
                return true;
            default:
                return false;
        }
    }

    @Override
    public CraftMetaPotion clone() {
        final CraftMetaPotion clone = (CraftMetaPotion) super.clone();
        if (this.customEffects != null) {
            clone.customEffects = new ArrayList<PotionEffect>(this.customEffects);
        }
        return clone;
    }

    public boolean hasCustomEffects() {
        return !(customEffects == null || customEffects.isEmpty());
    }

    public List<PotionEffect> getCustomEffects() {
        if (hasCustomEffects()) {
            return ImmutableList.copyOf(customEffects);
        }
        return ImmutableList.of();
    }

    public boolean addCustomEffect(final PotionEffect effect, final boolean overwrite) {
        Validate.notNull(effect, "Potion effect must not be null");

        final int index = indexOfEffect(effect.getType());
        if (index != -1) {
            if (overwrite) {
                final PotionEffect old = customEffects.get(index);
                if (old.getAmplifier() == effect.getAmplifier() && old.getDuration() == effect.getDuration() && old.isAmbient() == effect.isAmbient()) {
                    return false;
                }
                customEffects.set(index, effect);
                return true;
            } else {
                return false;
            }
        } else {
            if (customEffects == null) {
                customEffects = new ArrayList<PotionEffect>();
            }
            customEffects.add(effect);
            return true;
        }
    }

    public boolean removeCustomEffect(final PotionEffectType type) {
        Validate.notNull(type, "Potion effect type must not be null");

        if (!hasCustomEffects()) {
            return false;
        }

        boolean changed = false;
        final Iterator<PotionEffect> iterator = customEffects.iterator();
        while (iterator.hasNext()) {
            final PotionEffect effect = iterator.next();
            if (effect.getType() == type) {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    public boolean hasCustomEffect(final PotionEffectType type) {
        Validate.notNull(type, "Potion effect type must not be null");
        return indexOfEffect(type) != -1;
    }

    public boolean setMainEffect(final PotionEffectType type) {
        Validate.notNull(type, "Potion effect type must not be null");
        final int index = indexOfEffect(type);
        if (index == -1 || index == 0) {
            return false;
        }

        final PotionEffect old = customEffects.get(0);
        customEffects.set(0, customEffects.get(index));
        customEffects.set(index, old);
        return true;
    }

    private int indexOfEffect(final PotionEffectType type) {
        if (!hasCustomEffects()) {
            return -1;
        }

        for (int i = 0; i < customEffects.size(); i++) {
            if (customEffects.get(i).getType().equals(type)) {
                return i;
            }
        }
        return -1;
    }

    public boolean clearCustomEffects() {
        final boolean changed = hasCustomEffects();
        customEffects = null;
        return changed;
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasCustomEffects()) {
            hash = 73 * hash + customEffects.hashCode();
        }
        return original != hash ? CraftMetaPotion.class.hashCode() ^ hash : hash;
    }

    @Override
    public boolean equalsCommon(final CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaPotion) {
            final CraftMetaPotion that = (CraftMetaPotion) meta;

            return (this.hasCustomEffects() ? that.hasCustomEffects() && this.customEffects.equals(that.customEffects) : !that.hasCustomEffects());
        }
        return true;
    }

    @Override
    boolean notUncommon(final CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaPotion || isPotionEmpty());
    }

    @Override
    Builder<String, Object> serialize(final Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasCustomEffects()) {
            builder.put(POTION_EFFECTS.BUKKIT, ImmutableList.copyOf(this.customEffects));
        }

        return builder;
    }
}
