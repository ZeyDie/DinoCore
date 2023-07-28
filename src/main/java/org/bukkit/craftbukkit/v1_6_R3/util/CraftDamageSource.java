package org.bukkit.craftbukkit.v1_6_R3.util;


// Util class to create custom DamageSources.
public final class CraftDamageSource extends net.minecraft.util.DamageSource {
    public static net.minecraft.util.DamageSource copyOf(final net.minecraft.util.DamageSource original) {
        final CraftDamageSource newSource = new CraftDamageSource(original.damageType);

        // Check ignoresArmor
        if (original.isUnblockable()) {
            newSource.setDamageBypassesArmor();
        }

        // Check magic
        if (original.isMagicDamage()) {
            newSource.setMagicDamage();
        }

        // Check fire
        if (original.isExplosion()) {
            newSource.setFireDamage();
        }

        return newSource;
    }

    private CraftDamageSource(final String identifier) {
        super(identifier);
    }
}
