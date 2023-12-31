package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnchantmentUntouching extends Enchantment
{
    protected EnchantmentUntouching(final int par1, final int par2)
    {
        super(par1, par2, EnumEnchantmentType.digger);
        this.setName("untouching");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int par1)
    {
        return 15;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(final int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 1;
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    public boolean canApplyTogether(final Enchantment par1Enchantment)
    {
        return super.canApplyTogether(par1Enchantment) && par1Enchantment.effectId != fortune.effectId;
    }

    public boolean canApply(final ItemStack par1ItemStack)
    {
        return par1ItemStack.getItem().itemID == Item.shears.itemID ? true : super.canApply(par1ItemStack);
    }
}
