package net.minecraft.enchantment;

public class EnchantmentArrowInfinite extends Enchantment
{
    public EnchantmentArrowInfinite(final int par1, final int par2)
    {
        super(par1, par2, EnumEnchantmentType.bow);
        this.setName("arrowInfinite");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int par1)
    {
        return 20;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(final int par1)
    {
        return 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 1;
    }
}
