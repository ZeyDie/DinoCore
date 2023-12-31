package net.minecraft.enchantment;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandom;

import java.util.*;

public class EnchantmentHelper
{
    /** Is the random seed of enchantment effects. */
    private static final Random enchantmentRand = new Random();

    /**
     * Used to calculate the extra armor of enchantments on armors equipped on player.
     */
    private static final EnchantmentModifierDamage enchantmentModifierDamage = new EnchantmentModifierDamage((Empty3)null);

    /**
     * Used to calculate the (magic) extra damage done by enchantments on current equipped item of player.
     */
    private static final EnchantmentModifierLiving enchantmentModifierLiving = new EnchantmentModifierLiving((Empty3)null);

    /**
     * Returns the level of enchantment on the ItemStack passed.
     */
    public static int getEnchantmentLevel(final int par0, final ItemStack par1ItemStack)
    {
        if (par1ItemStack == null)
        {
            return 0;
        }
        else
        {
            final NBTTagList nbttaglist = par1ItemStack.getEnchantmentTagList();

            if (nbttaglist == null)
            {
                return 0;
            }
            else
            {
                for (int j = 0; j < nbttaglist.tagCount(); ++j)
                {
                    final short short1 = ((NBTTagCompound)nbttaglist.tagAt(j)).getShort("id");
                    final short short2 = ((NBTTagCompound)nbttaglist.tagAt(j)).getShort("lvl");

                    if (short1 == par0)
                    {
                        return short2;
                    }
                }

                return 0;
            }
        }
    }

    /**
     * Return the enchantments for the specified stack.
     */
    public static Map getEnchantments(final ItemStack par0ItemStack)
    {
        final LinkedHashMap linkedhashmap = new LinkedHashMap();
        final NBTTagList nbttaglist = par0ItemStack.itemID == Item.enchantedBook.itemID ? Item.enchantedBook.func_92110_g(par0ItemStack) : par0ItemStack.getEnchantmentTagList();

        if (nbttaglist != null)
        {
            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                final short short1 = ((NBTTagCompound)nbttaglist.tagAt(i)).getShort("id");
                final short short2 = ((NBTTagCompound)nbttaglist.tagAt(i)).getShort("lvl");
                linkedhashmap.put(Integer.valueOf(short1), Integer.valueOf(short2));
            }
        }

        return linkedhashmap;
    }

    /**
     * Set the enchantments for the specified stack.
     */
    public static void setEnchantments(final Map par0Map, final ItemStack par1ItemStack)
    {
        final NBTTagList nbttaglist = new NBTTagList();
        final Iterator iterator = par0Map.keySet().iterator();

        while (iterator.hasNext())
        {
            final int i = ((Integer)iterator.next()).intValue();
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short)i);
            nbttagcompound.setShort("lvl", (short)((Integer)par0Map.get(Integer.valueOf(i))).intValue());
            nbttaglist.appendTag(nbttagcompound);

            if (par1ItemStack.itemID == Item.enchantedBook.itemID)
            {
                Item.enchantedBook.addEnchantment(par1ItemStack, new EnchantmentData(i, ((Integer)par0Map.get(Integer.valueOf(i))).intValue()));
            }
        }

        if (nbttaglist.tagCount() > 0)
        {
            if (par1ItemStack.itemID != Item.enchantedBook.itemID)
            {
                par1ItemStack.setTagInfo("ench", nbttaglist);
            }
        }
        else if (par1ItemStack.hasTagCompound())
        {
            par1ItemStack.getTagCompound().removeTag("ench");
        }
    }

    /**
     * Returns the biggest level of the enchantment on the array of ItemStack passed.
     */
    public static int getMaxEnchantmentLevel(final int par0, final ItemStack[] par1ArrayOfItemStack)
    {
        if (par1ArrayOfItemStack == null)
        {
            return 0;
        }
        else
        {
            int j = 0;
            final ItemStack[] aitemstack1 = par1ArrayOfItemStack;
            final int k = par1ArrayOfItemStack.length;

            for (int l = 0; l < k; ++l)
            {
                final ItemStack itemstack = aitemstack1[l];
                final int i1 = getEnchantmentLevel(par0, itemstack);

                if (i1 > j)
                {
                    j = i1;
                }
            }

            return j;
        }
    }

    /**
     * Executes the enchantment modifier on the ItemStack passed.
     */
    private static void applyEnchantmentModifier(final IEnchantmentModifier par0IEnchantmentModifier, final ItemStack par1ItemStack)
    {
        if (par1ItemStack != null)
        {
            final NBTTagList nbttaglist = par1ItemStack.getEnchantmentTagList();

            if (nbttaglist != null)
            {
                for (int i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    final short short1 = ((NBTTagCompound)nbttaglist.tagAt(i)).getShort("id");
                    final short short2 = ((NBTTagCompound)nbttaglist.tagAt(i)).getShort("lvl");

                    if (Enchantment.enchantmentsList[short1] != null)
                    {
                        par0IEnchantmentModifier.calculateModifier(Enchantment.enchantmentsList[short1], short2);
                    }
                }
            }
        }
    }

    /**
     * Executes the enchantment modifier on the array of ItemStack passed.
     */
    private static void applyEnchantmentModifierArray(final IEnchantmentModifier par0IEnchantmentModifier, final ItemStack[] par1ArrayOfItemStack)
    {
        final ItemStack[] aitemstack1 = par1ArrayOfItemStack;
        final int i = par1ArrayOfItemStack.length;

        for (int j = 0; j < i; ++j)
        {
            final ItemStack itemstack = aitemstack1[j];
            applyEnchantmentModifier(par0IEnchantmentModifier, itemstack);
        }
    }

    /**
     * Returns the modifier of protection enchantments on armors equipped on player.
     */
    public static int getEnchantmentModifierDamage(final ItemStack[] par0ArrayOfItemStack, final DamageSource par1DamageSource)
    {
        enchantmentModifierDamage.damageModifier = 0;
        enchantmentModifierDamage.source = par1DamageSource;
        applyEnchantmentModifierArray(enchantmentModifierDamage, par0ArrayOfItemStack);

        if (enchantmentModifierDamage.damageModifier > 25)
        {
            enchantmentModifierDamage.damageModifier = 25;
        }

        return (enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((enchantmentModifierDamage.damageModifier >> 1) + 1);
    }

    /**
     * Return the (magic) extra damage of the enchantments on player equipped item.
     */
    public static float getEnchantmentModifierLiving(final EntityLivingBase par0EntityLivingBase, final EntityLivingBase par1EntityLivingBase)
    {
        enchantmentModifierLiving.livingModifier = 0.0F;
        enchantmentModifierLiving.entityLiving = par1EntityLivingBase;
        applyEnchantmentModifier(enchantmentModifierLiving, par0EntityLivingBase.getHeldItem());
        return enchantmentModifierLiving.livingModifier;
    }

    /**
     * Returns the knockback value of enchantments on equipped player item.
     */
    public static int getKnockbackModifier(final EntityLivingBase par0EntityLivingBase, final EntityLivingBase par1EntityLivingBase)
    {
        return getEnchantmentLevel(Enchantment.knockback.effectId, par0EntityLivingBase.getHeldItem());
    }

    public static int getFireAspectModifier(final EntityLivingBase par0EntityLivingBase)
    {
        return getEnchantmentLevel(Enchantment.fireAspect.effectId, par0EntityLivingBase.getHeldItem());
    }

    /**
     * Returns the 'Water Breathing' modifier of enchantments on player equipped armors.
     */
    public static int getRespiration(final EntityLivingBase par0EntityLivingBase)
    {
        return getMaxEnchantmentLevel(Enchantment.respiration.effectId, par0EntityLivingBase.getLastActiveItems());
    }

    /**
     * Return the extra efficiency of tools based on enchantments on equipped player item.
     */
    public static int getEfficiencyModifier(final EntityLivingBase par0EntityLivingBase)
    {
        return getEnchantmentLevel(Enchantment.efficiency.effectId, par0EntityLivingBase.getHeldItem());
    }

    /**
     * Returns the silk touch status of enchantments on current equipped item of player.
     */
    public static boolean getSilkTouchModifier(final EntityLivingBase par0EntityLivingBase)
    {
        return getEnchantmentLevel(Enchantment.silkTouch.effectId, par0EntityLivingBase.getHeldItem()) > 0;
    }

    /**
     * Returns the fortune enchantment modifier of the current equipped item of player.
     */
    public static int getFortuneModifier(final EntityLivingBase par0EntityLivingBase)
    {
        return getEnchantmentLevel(Enchantment.fortune.effectId, par0EntityLivingBase.getHeldItem());
    }

    /**
     * Returns the looting enchantment modifier of the current equipped item of player.
     */
    public static int getLootingModifier(final EntityLivingBase par0EntityLivingBase)
    {
        return getEnchantmentLevel(Enchantment.looting.effectId, par0EntityLivingBase.getHeldItem());
    }

    /**
     * Returns the aqua affinity status of enchantments on current equipped item of player.
     */
    public static boolean getAquaAffinityModifier(final EntityLivingBase par0EntityLivingBase)
    {
        return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, par0EntityLivingBase.getLastActiveItems()) > 0;
    }

    public static int func_92098_i(final EntityLivingBase par0EntityLivingBase)
    {
        return getMaxEnchantmentLevel(Enchantment.thorns.effectId, par0EntityLivingBase.getLastActiveItems());
    }

    public static ItemStack func_92099_a(final Enchantment par0Enchantment, final EntityLivingBase par1EntityLivingBase)
    {
        final ItemStack[] aitemstack = par1EntityLivingBase.getLastActiveItems();
        final int i = aitemstack.length;

        for (int j = 0; j < i; ++j)
        {
            final ItemStack itemstack = aitemstack[j];

            if (itemstack != null && getEnchantmentLevel(par0Enchantment.effectId, itemstack) > 0)
            {
                return itemstack;
            }
        }

        return null;
    }

    /**
     * Returns the enchantability of itemstack, it's uses a singular formula for each index (2nd parameter: 0, 1 and 2),
     * cutting to the max enchantability power of the table (3rd parameter)
     */
    public static int calcItemStackEnchantability(final Random par0Random, final int par1, int par2, final ItemStack par3ItemStack)
    {
        int par21 = par2;
        final Item item = par3ItemStack.getItem();
        final int k = item.getItemEnchantability();

        if (k <= 0)
        {
            return 0;
        }
        else
        {
            if (par21 > 15)
            {
                par21 = 15;
            }

            final int l = par0Random.nextInt(8) + 1 + (par21 >> 1) + par0Random.nextInt(par21 + 1);
            return par1 == 0 ? Math.max(l / 3, 1) : (par1 == 1 ? l * 2 / 3 + 1 : Math.max(l, par21 * 2));
        }
    }

    /**
     * Adds a random enchantment to the specified item. Args: random, itemStack, enchantabilityLevel
     */
    public static ItemStack addRandomEnchantment(final Random par0Random, final ItemStack par1ItemStack, final int par2)
    {
        final List list = buildEnchantmentList(par0Random, par1ItemStack, par2);
        final boolean flag = par1ItemStack.itemID == Item.book.itemID;

        if (flag)
        {
            par1ItemStack.itemID = Item.enchantedBook.itemID;
        }

        if (list != null)
        {
            final Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                final EnchantmentData enchantmentdata = (EnchantmentData)iterator.next();

                if (flag)
                {
                    Item.enchantedBook.addEnchantment(par1ItemStack, enchantmentdata);
                }
                else
                {
                    par1ItemStack.addEnchantment(enchantmentdata.enchantmentobj, enchantmentdata.enchantmentLevel);
                }
            }
        }

        return par1ItemStack;
    }

    /**
     * Create a list of random EnchantmentData (enchantments) that can be added together to the ItemStack, the 3rd
     * parameter is the total enchantability level.
     */
    public static List buildEnchantmentList(final Random par0Random, final ItemStack par1ItemStack, final int par2)
    {
        final Item item = par1ItemStack.getItem();
        int j = item.getItemEnchantability();

        if (j <= 0)
        {
            return null;
        }
        else
        {
            j /= 2;
            j = 1 + par0Random.nextInt((j >> 1) + 1) + par0Random.nextInt((j >> 1) + 1);
            final int k = j + par2;
            final float f = (par0Random.nextFloat() + par0Random.nextFloat() - 1.0F) * 0.15F;
            int l = (int)((float)k * (1.0F + f) + 0.5F);

            if (l < 1)
            {
                l = 1;
            }

            ArrayList arraylist = null;
            final Map map = mapEnchantmentData(l, par1ItemStack);

            if (map != null && !map.isEmpty())
            {
                final EnchantmentData enchantmentdata = (EnchantmentData)WeightedRandom.getRandomItem(par0Random, map.values());

                if (enchantmentdata != null)
                {
                    arraylist = new ArrayList();
                    arraylist.add(enchantmentdata);

                    for (int i1 = l; par0Random.nextInt(50) <= i1; i1 >>= 1)
                    {
                        final Iterator iterator = map.keySet().iterator();

                        while (iterator.hasNext())
                        {
                            final Integer integer = (Integer)iterator.next();
                            boolean flag = true;
                            final Iterator iterator1 = arraylist.iterator();

                            while (true)
                            {
                                if (iterator1.hasNext())
                                {
                                    final EnchantmentData enchantmentdata1 = (EnchantmentData)iterator1.next();

                                    if (enchantmentdata1.enchantmentobj.canApplyTogether(Enchantment.enchantmentsList[integer.intValue()]))
                                    {
                                        continue;
                                    }

                                    flag = false;
                                }

                                if (!flag)
                                {
                                    iterator.remove();
                                }

                                break;
                            }
                        }

                        if (!map.isEmpty())
                        {
                            final EnchantmentData enchantmentdata2 = (EnchantmentData)WeightedRandom.getRandomItem(par0Random, map.values());
                            arraylist.add(enchantmentdata2);
                        }
                    }
                }
            }

            return arraylist;
        }
    }

    /**
     * Creates a 'Map' of EnchantmentData (enchantments) possible to add on the ItemStack and the enchantability level
     * passed.
     */
    public static Map mapEnchantmentData(final int par0, final ItemStack par1ItemStack)
    {
        final Item item = par1ItemStack.getItem();
        HashMap hashmap = null;
        boolean flag = par1ItemStack.itemID == Item.book.itemID;
        final Enchantment[] aenchantment = Enchantment.enchantmentsList;
        final int j = aenchantment.length;

        for (int k = 0; k < j; ++k)
        {
            final Enchantment enchantment = aenchantment[k];

            if (enchantment == null) continue;

            flag = (par1ItemStack.itemID == Item.book.itemID) && enchantment.isAllowedOnBooks();
            if (enchantment.canApplyAtEnchantingTable(par1ItemStack) || flag)
            {
                for (int l = enchantment.getMinLevel(); l <= enchantment.getMaxLevel(); ++l)
                {
                    if (par0 >= enchantment.getMinEnchantability(l) && par0 <= enchantment.getMaxEnchantability(l))
                    {
                        if (hashmap == null)
                        {
                            hashmap = new HashMap();
                        }

                        hashmap.put(Integer.valueOf(enchantment.effectId), new EnchantmentData(enchantment, l));
                    }
                }
            }
        }

        return hashmap;
    }
}
