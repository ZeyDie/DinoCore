package net.minecraft.enchantment;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class EnchantmentDurability extends Enchantment
{
    protected EnchantmentDurability(final int par1, final int par2)
    {
        super(par1, par2, EnumEnchantmentType.digger);
        this.setName("durability");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(final int par1)
    {
        return 5 + (par1 - 1) * 8;
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
        return 3;
    }

    public boolean canApply(final ItemStack par1ItemStack)
    {
        return par1ItemStack.isItemStackDamageable() ? true : super.canApply(par1ItemStack);
    }

    /**
     * Used by ItemStack.attemptDamageItem. Randomly determines if a point of damage should be negated using the
     * enchantment level (par1). If the ItemStack is Armor then there is a flat 60% chance for damage to be negated no
     * matter the enchantment level, otherwise there is a 1-(par/1) chance for damage to be negated.
     */
    public static boolean negateDamage(final ItemStack par0ItemStack, final int par1, final Random par2Random)
    {
        return par0ItemStack.getItem() instanceof ItemArmor && par2Random.nextFloat() < 0.6F ? false : par2Random.nextInt(par1 + 1) > 0;
    }
}
