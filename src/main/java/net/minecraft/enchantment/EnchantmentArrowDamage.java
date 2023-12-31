package net.minecraft.enchantment;

public class EnchantmentArrowDamage extends Enchantment
{
    public EnchantmentArrowDamage(final int par1, final int par2)
    {
        super(par1, par2, EnumEnchantmentType.bow);
        this.setName("arrowDamage");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int par1)
    {
        return 1 + (par1 - 1) * 10;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(final int par1)
    {
        return this.getMinEnchantability(par1) + 15;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 5;
    }
}
