package net.minecraft.enchantment;

import net.minecraft.util.WeightedRandomItem;

public class EnchantmentData extends WeightedRandomItem
{
    /** Enchantment object associated with this EnchantmentData */
    public final Enchantment enchantmentobj;

    /** Enchantment level associated with this EnchantmentData */
    public final int enchantmentLevel;

    public EnchantmentData(final Enchantment par1Enchantment, final int par2)
    {
        super(par1Enchantment.getWeight());
        this.enchantmentobj = par1Enchantment;
        this.enchantmentLevel = par2;
    }

    public EnchantmentData(final int par1, final int par2)
    {
        this(Enchantment.enchantmentsList[par1], par2);
    }
}
