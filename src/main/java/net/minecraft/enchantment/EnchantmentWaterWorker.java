package net.minecraft.enchantment;

public class EnchantmentWaterWorker extends Enchantment
{
    public EnchantmentWaterWorker(final int par1, final int par2)
    {
        super(par1, par2, EnumEnchantmentType.armor_head);
        this.setName("waterWorker");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int par1)
    {
        return 1;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(final int par1)
    {
        return this.getMinEnchantability(par1) + 40;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 1;
    }
}
