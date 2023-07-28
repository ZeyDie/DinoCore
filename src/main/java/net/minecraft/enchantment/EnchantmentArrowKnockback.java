package net.minecraft.enchantment;

public class EnchantmentArrowKnockback extends Enchantment
{
    public EnchantmentArrowKnockback(final int par1, final int par2)
    {
        super(par1, par2, EnumEnchantmentType.bow);
        this.setName("arrowKnockback");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int par1)
    {
        return 12 + (par1 - 1) * 20;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(final int par1)
    {
        return this.getMinEnchantability(par1) + 25;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 2;
    }
}
