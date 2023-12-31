package net.minecraft.enchantment;

import net.minecraft.util.DamageSource;

final class EnchantmentModifierDamage implements IEnchantmentModifier
{
    /**
     * Used to calculate the damage modifier (extra armor) on enchantments that the player have on equipped armors.
     */
    public int damageModifier;

    /**
     * Used as parameter to calculate the damage modifier (extra armor) on enchantments that the player have on equipped
     * armors.
     */
    public DamageSource source;

    private EnchantmentModifierDamage() {}

    /**
     * Generic method use to calculate modifiers of offensive or defensive enchantment values.
     */
    public void calculateModifier(final Enchantment par1Enchantment, final int par2)
    {
        this.damageModifier += par1Enchantment.calcModifierDamage(par2, this.source);
    }

    EnchantmentModifierDamage(final Empty3 par1Empty3)
    {
        this();
    }
}
