package org.bukkit.craftbukkit.v1_6_R3.potion;

import com.google.common.collect.Maps;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CraftPotionBrewer implements PotionBrewer {
    private static final Map<Integer, Collection<PotionEffect>> cache = Maps.newHashMap();

    public Collection<PotionEffect> getEffectsFromDamage(final int damage) {
        if (cache.containsKey(damage))
            return cache.get(damage);

        final List<?> mcEffects = net.minecraft.potion.PotionHelper.getPotionEffects(damage, false);
        final List<PotionEffect> effects = new ArrayList<PotionEffect>();
        if (mcEffects == null)
            return effects;

        for (final Object raw : mcEffects) {
            if (raw == null || !(raw instanceof net.minecraft.potion.PotionEffect))
                continue;
            final net.minecraft.potion.PotionEffect mcEffect = (net.minecraft.potion.PotionEffect) raw;
            final PotionEffect effect = new PotionEffect(PotionEffectType.getById(mcEffect.getPotionID()),
                    mcEffect.getDuration(), mcEffect.getAmplifier());
            // Minecraft PotionBrewer applies duration modifiers automatically.
            effects.add(effect);
        }

        cache.put(damage, effects);

        return effects;
    }

    public PotionEffect createEffect(final PotionEffectType potion, final int duration, final int amplifier) {
        return new PotionEffect(potion, potion.isInstant() ? 1 : (int) (duration * potion.getDurationModifier()),
                amplifier);
    }
}
