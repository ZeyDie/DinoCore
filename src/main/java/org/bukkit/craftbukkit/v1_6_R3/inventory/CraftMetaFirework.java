package org.bukkit.craftbukkit.v1_6_R3.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftMetaItem.ItemMetaKey.Specific;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftMetaItem.ItemMetaKey.Specific.To;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaFirework extends CraftMetaItem implements FireworkMeta {
    /*
       "Fireworks", "Explosion", "Explosions", "Flight", "Type", "Trail", "Flicker", "Colors", "FadeColors";

        Fireworks
        - Compound: Fireworks
        -- Byte: Flight
        -- List: Explosions
        --- Compound: Explosion
        ---- IntArray: Colors
        ---- Byte: Type
        ---- Boolean: Trail
        ---- Boolean: Flicker
        ---- IntArray: FadeColors
     */

    @Specific(To.NBT)
    static final ItemMetaKey FIREWORKS = new ItemMetaKey("Fireworks");
    static final ItemMetaKey FLIGHT = new ItemMetaKey("Flight", "power");
    static final ItemMetaKey EXPLOSIONS = new ItemMetaKey("Explosions", "firework-effects");
    @Specific(To.NBT)
    static final ItemMetaKey EXPLOSION_COLORS = new ItemMetaKey("Colors");
    @Specific(To.NBT)
    static final ItemMetaKey EXPLOSION_TYPE = new ItemMetaKey("Type");
    @Specific(To.NBT)
    static final ItemMetaKey EXPLOSION_TRAIL = new ItemMetaKey("Trail");
    @Specific(To.NBT)
    static final ItemMetaKey EXPLOSION_FLICKER = new ItemMetaKey("Flicker");
    @Specific(To.NBT)
    static final ItemMetaKey EXPLOSION_FADE = new ItemMetaKey("FadeColors");

    private List<FireworkEffect> effects;
    private int power;

    CraftMetaFirework(final CraftMetaItem meta) {
        super(meta);

        if (!(meta instanceof CraftMetaFirework)) {
            return;
        }

        final CraftMetaFirework that = (CraftMetaFirework) meta;

        this.power = that.power;

        if (that.hasEffects()) {
            this.effects = new ArrayList<FireworkEffect>(that.effects);
        }
    }

    CraftMetaFirework(final net.minecraft.nbt.NBTTagCompound tag) {
        super(tag);

        if (!tag.hasKey(FIREWORKS.NBT)) {
            return;
        }

        final net.minecraft.nbt.NBTTagCompound fireworks = tag.getCompoundTag(FIREWORKS.NBT);

        power = 0xff & fireworks.getByte(FLIGHT.NBT);

        if (!fireworks.hasKey(EXPLOSIONS.NBT)) {
            return;
        }

        final net.minecraft.nbt.NBTTagList fireworkEffects = fireworks.getTagList(EXPLOSIONS.NBT);
        final List<FireworkEffect> effects = this.effects = new ArrayList<FireworkEffect>(fireworkEffects.tagCount());

        for (int i = 0; i < fireworkEffects.tagCount(); i++) {
            effects.add(getEffect((net.minecraft.nbt.NBTTagCompound) fireworkEffects.tagAt(i)));
        }
    }

    static FireworkEffect getEffect(final net.minecraft.nbt.NBTTagCompound explosion) {
        final FireworkEffect.Builder effect = FireworkEffect.builder()
                .flicker(explosion.getBoolean(EXPLOSION_FLICKER.NBT))
                .trail(explosion.getBoolean(EXPLOSION_TRAIL.NBT))
                .with(getEffectType(0xff & explosion.getByte(EXPLOSION_TYPE.NBT)));

        for (final int color : explosion.getIntArray(EXPLOSION_COLORS.NBT)) {
            effect.withColor(Color.fromRGB(color));
        }

        for (final int color : explosion.getIntArray(EXPLOSION_FADE.NBT)) {
            effect.withFade(Color.fromRGB(color));
        }

        return effect.build();
    }

    static net.minecraft.nbt.NBTTagCompound getExplosion(final FireworkEffect effect) {
        final net.minecraft.nbt.NBTTagCompound explosion = new net.minecraft.nbt.NBTTagCompound();

        if (effect.hasFlicker()) {
            explosion.setBoolean(EXPLOSION_FLICKER.NBT, true);
        }

        if (effect.hasTrail()) {
            explosion.setBoolean(EXPLOSION_TRAIL.NBT, true);
        }

        addColors(explosion, EXPLOSION_COLORS, effect.getColors());
        addColors(explosion, EXPLOSION_FADE, effect.getFadeColors());

        explosion.setByte(EXPLOSION_TYPE.NBT, (byte) getNBT(effect.getType()));

        return explosion;
    }

    static int getNBT(final Type type) {
        switch (type) {
            case BALL:
                return 0;
            case BALL_LARGE:
                return 1;
            case STAR:
                return 2;
            case CREEPER:
                return 3;
            case BURST:
                return 4;
            default:
                throw new IllegalStateException(type.toString()); // Spigot
        }
    }

    static Type getEffectType(final int nbt) {
        switch (nbt) {
            case 0:
                return Type.BALL;
            case 1:
                return Type.BALL_LARGE;
            case 2:
                return Type.STAR;
            case 3:
                return Type.CREEPER;
            case 4:
                return Type.BURST;
            default:
                throw new IllegalStateException(Integer.toString(nbt)); // Spigot
        }
    }

    CraftMetaFirework(final Map<String, Object> map) {
        super(map);

        final Integer power = SerializableMeta.getObject(Integer.class, map, FLIGHT.BUKKIT, true);
        if (power != null) {
            setPower(power);
        }

        final Iterable<?> effects = SerializableMeta.getObject(Iterable.class, map, EXPLOSIONS.BUKKIT, true);
        safelyAddEffects(effects);
    }

    public boolean hasEffects() {
        return !(effects == null || effects.isEmpty());
    }

    void safelyAddEffects(final Iterable<?> collection) {
        if (collection == null || (collection instanceof Collection && ((Collection<?>) collection).isEmpty())) {
            return;
        }

        List<FireworkEffect> effects = this.effects;
        if (effects == null) {
            effects = this.effects = new ArrayList<FireworkEffect>();
        }

        for (final Object obj : collection) {
            if (obj instanceof FireworkEffect) {
                effects.add((FireworkEffect) obj);
            } else {
                throw new IllegalArgumentException(obj + " in " + collection + " is not a FireworkEffect");
            }
        }
    }

    @Override
    void applyToItem(final net.minecraft.nbt.NBTTagCompound itemTag) {
        super.applyToItem(itemTag);
        if (isFireworkEmpty()) {
            return;
        }

        final net.minecraft.nbt.NBTTagCompound fireworks = itemTag.getCompoundTag(FIREWORKS.NBT);
        itemTag.setCompoundTag(FIREWORKS.NBT, fireworks);

        if (hasEffects()) {
            final net.minecraft.nbt.NBTTagList effects = new net.minecraft.nbt.NBTTagList(EXPLOSIONS.NBT);
            for (final FireworkEffect effect : this.effects) {
                effects.appendTag(getExplosion(effect));
            }

            if (effects.tagCount() > 0) {
                fireworks.setTag(EXPLOSIONS.NBT, effects);
            }
        }

        if (hasPower()) {
            fireworks.setByte(FLIGHT.NBT, (byte) power);
        }
    }

    static void addColors(final net.minecraft.nbt.NBTTagCompound compound, final ItemMetaKey key, final List<Color> colors) {
        if (colors.isEmpty()) {
            return;
        }

        final int[] colorArray = new int[colors.size()];
        int i = 0;
        for (final Color color : colors) {
            colorArray[i++] = color.asRGB();
        }

        compound.setIntArray(key.NBT, colorArray);
    }

    @Override
    boolean applicableTo(final Material type) {
        switch(type) {
            case FIREWORK:
                return true;
            default:
                return false;
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isFireworkEmpty();
    }

    boolean isFireworkEmpty() {
        return  !(hasEffects() || hasPower());
    }

    boolean hasPower() {
        return power != 0;
    }

    @Override
    boolean equalsCommon(final CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }

        if (meta instanceof CraftMetaFirework) {
            final CraftMetaFirework that = (CraftMetaFirework) meta;

            return (hasPower() ? that.hasPower() && this.power == that.power : !that.hasPower())
                    && (hasEffects() ? that.hasEffects() && this.effects.equals(that.effects) : !that.hasEffects());
        }

        return true;
    }

    @Override
    boolean notUncommon(final CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaFirework || isFireworkEmpty());
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasPower()) {
            hash = 61 * hash + power;
        }
        if (hasEffects()) {
            hash = 61 * hash + 13 * effects.hashCode();
        }
        return hash != original ? CraftMetaFirework.class.hashCode() ^ hash : hash;
    }

    @Override
    Builder<String, Object> serialize(final Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasEffects()) {
            builder.put(EXPLOSIONS.BUKKIT, ImmutableList.copyOf(effects));
        }

        if (hasPower()) {
            builder.put(FLIGHT.BUKKIT, power);
        }

        return builder;
    }

    @Override
    public CraftMetaFirework clone() {
        final CraftMetaFirework meta = (CraftMetaFirework) super.clone();

        if (this.effects != null) {
            meta.effects = new ArrayList<FireworkEffect>(this.effects);
        }

        return meta;
    }

    public void addEffect(final FireworkEffect effect) {
        Validate.notNull(effect, "Effect cannot be null");
        if (this.effects == null) {
            this.effects = new ArrayList<FireworkEffect>();
        }
        this.effects.add(effect);
    }

    public void addEffects(final FireworkEffect...effects) {
        Validate.notNull(effects, "Effects cannot be null");
        if (effects.length == 0) {
            return;
        }

        List<FireworkEffect> list = this.effects;
        if (list == null) {
            list = this.effects = new ArrayList<FireworkEffect>();
        }

        for (final FireworkEffect effect : effects) {
            Validate.notNull(effect, "Effect cannot be null");
            list.add(effect);
        }
    }

    public void addEffects(final Iterable<FireworkEffect> effects) {
        Validate.notNull(effects, "Effects cannot be null");
        safelyAddEffects(effects);
    }

    public List<FireworkEffect> getEffects() {
        return this.effects == null ? ImmutableList.<FireworkEffect>of() : ImmutableList.copyOf(this.effects);
    }

    public int getEffectsSize() {
        return this.effects == null ? 0 : this.effects.size();
    }

    public void removeEffect(final int index) {
        if (this.effects == null) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
        } else {
            this.effects.remove(index);
        }
    }

    public void clearEffects() {
        this.effects = null;
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(final int power) {
        Validate.isTrue(power >= 0, "Power cannot be less than zero: ", power);
        Validate.isTrue(power < 0x80, "Power cannot be more than 127: ", power);
        this.power = power;
    }
}
