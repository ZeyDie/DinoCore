package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class EnchantmentProtection extends Enchantment
{
    /** Holds the name to be translated of each protection type. */
    private static final String[] protectionName = {"all", "fire", "fall", "explosion", "projectile"};

    /**
     * Holds the base factor of enchantability needed to be able to use the enchant.
     */
    private static final int[] baseEnchantability = {1, 10, 5, 5, 3};

    /**
     * Holds how much each level increased the enchantability factor to be able to use this enchant.
     */
    private static final int[] levelEnchantability = {11, 8, 6, 8, 6};

    /**
     * Used on the formula of base enchantability, this is the 'window' factor of values to be able to use thing
     * enchant.
     */
    private static final int[] thresholdEnchantability = {20, 12, 10, 12, 15};

    /**
     * Defines the type of protection of the enchantment, 0 = all, 1 = fire, 2 = fall (feather fall), 3 = explosion and
     * 4 = projectile.
     */
    public final int protectionType;

    public EnchantmentProtection(final int par1, final int par2, final int par3)
    {
        super(par1, par2, EnumEnchantmentType.armor);
        this.protectionType = par3;

        if (par3 == 2)
        {
            this.type = EnumEnchantmentType.armor_feet;
        }
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int par1)
    {
        return baseEnchantability[this.protectionType] + (par1 - 1) * levelEnchantability[this.protectionType];
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(final int par1)
    {
        return this.getMinEnchantability(par1) + thresholdEnchantability[this.protectionType];
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 4;
    }

    /**
     * Calculates de damage protection of the enchantment based on level and damage source passed.
     */
    public int calcModifierDamage(final int par1, final DamageSource par2DamageSource)
    {
        if (par2DamageSource.canHarmInCreative())
        {
            return 0;
        }
        else
        {
            final float f = (float)(6 + par1 * par1) / 3.0F;
            return this.protectionType == 0 ? MathHelper.floor_float(f * 0.75F) : (this.protectionType == 1 && par2DamageSource.isFireDamage() ? MathHelper.floor_float(f * 1.25F) : (this.protectionType == 2 && par2DamageSource == DamageSource.fall ? MathHelper.floor_float(f * 2.5F) : (this.protectionType == 3 && par2DamageSource.isExplosion() ? MathHelper.floor_float(f * 1.5F) : (this.protectionType == 4 && par2DamageSource.isProjectile() ? MathHelper.floor_float(f * 1.5F) : 0))));
        }
    }

    /**
     * Return the name of key in translation table of this enchantment.
     */
    public String getName()
    {
        return "enchantment.protect." + protectionName[this.protectionType];
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    public boolean canApplyTogether(final Enchantment par1Enchantment)
    {
        if (par1Enchantment instanceof EnchantmentProtection)
        {
            final EnchantmentProtection enchantmentprotection = (EnchantmentProtection)par1Enchantment;
            return enchantmentprotection.protectionType == this.protectionType ? false : this.protectionType == 2 || enchantmentprotection.protectionType == 2;
        }
        else
        {
            return super.canApplyTogether(par1Enchantment);
        }
    }

    /**
     * Gets the amount of ticks an entity should be set fire, adjusted for fire protection.
     */
    public static int getFireTimeForEntity(final Entity par0Entity, int par1)
    {
        int par11 = par1;
        final int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.fireProtection.effectId, par0Entity.getLastActiveItems());

        if (j > 0)
        {
            par11 -= MathHelper.floor_float((float) par11 * (float)j * 0.15F);
        }

        return par11;
    }

    public static double func_92092_a(final Entity par0Entity, double par1)
    {
        double par11 = par1;
        final int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.blastProtection.effectId, par0Entity.getLastActiveItems());

        if (i > 0)
        {
            par11 -= (double)MathHelper.floor_double(par11 * (double)((float)i * 0.15F));
        }

        return par11;
    }
}
