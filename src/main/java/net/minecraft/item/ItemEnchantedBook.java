package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;

import java.util.List;
import java.util.Random;

public class ItemEnchantedBook extends Item
{
    public ItemEnchantedBook(final int par1)
    {
        super(par1);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(final ItemStack par1ItemStack)
    {
        return true;
    }

    /**
     * Checks isDamagable and if it cannot be stacked
     */
    public boolean isItemTool(final ItemStack par1ItemStack)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(final ItemStack par1ItemStack)
    {
        return this.func_92110_g(par1ItemStack).tagCount() > 0 ? EnumRarity.uncommon : super.getRarity(par1ItemStack);
    }

    public NBTTagList func_92110_g(final ItemStack par1ItemStack)
    {
        return par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.hasKey("StoredEnchantments") ? (NBTTagList)par1ItemStack.stackTagCompound.getTag("StoredEnchantments") : new NBTTagList();
    }

    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4)
    {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        final NBTTagList nbttaglist = this.func_92110_g(par1ItemStack);

        if (nbttaglist != null)
        {
            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                final short short1 = ((NBTTagCompound)nbttaglist.tagAt(i)).getShort("id");
                final short short2 = ((NBTTagCompound)nbttaglist.tagAt(i)).getShort("lvl");

                if (Enchantment.enchantmentsList[short1] != null)
                {
                    par3List.add(Enchantment.enchantmentsList[short1].getTranslatedName(short2));
                }
            }
        }
    }

    /**
     * Adds an stored enchantment to an enchanted book ItemStack
     */
    public void addEnchantment(final ItemStack par1ItemStack, final EnchantmentData par2EnchantmentData)
    {
        final NBTTagList nbttaglist = this.func_92110_g(par1ItemStack);
        boolean flag = true;

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);

            if (nbttagcompound.getShort("id") == par2EnchantmentData.enchantmentobj.effectId)
            {
                if (nbttagcompound.getShort("lvl") < par2EnchantmentData.enchantmentLevel)
                {
                    nbttagcompound.setShort("lvl", (short)par2EnchantmentData.enchantmentLevel);
                }

                flag = false;
                break;
            }
        }

        if (flag)
        {
            final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setShort("id", (short)par2EnchantmentData.enchantmentobj.effectId);
            nbttagcompound1.setShort("lvl", (short)par2EnchantmentData.enchantmentLevel);
            nbttaglist.appendTag(nbttagcompound1);
        }

        if (!par1ItemStack.hasTagCompound())
        {
            par1ItemStack.setTagCompound(new NBTTagCompound());
        }

        par1ItemStack.getTagCompound().setTag("StoredEnchantments", nbttaglist);
    }

    /**
     * Returns the ItemStack of an enchanted version of this item.
     */
    public ItemStack getEnchantedItemStack(final EnchantmentData par1EnchantmentData)
    {
        final ItemStack itemstack = new ItemStack(this);
        this.addEnchantment(itemstack, par1EnchantmentData);
        return itemstack;
    }

    @SideOnly(Side.CLIENT)
    public void func_92113_a(final Enchantment par1Enchantment, final List par2List)
    {
        for (int i = par1Enchantment.getMinLevel(); i <= par1Enchantment.getMaxLevel(); ++i)
        {
            par2List.add(this.getEnchantedItemStack(new EnchantmentData(par1Enchantment, i)));
        }
    }

    public WeightedRandomChestContent func_92114_b(final Random par1Random)
    {
        return this.func_92112_a(par1Random, 1, 1, 1);
    }

    public WeightedRandomChestContent func_92112_a(final Random par1Random, final int par2, final int par3, final int par4)
    {
        final Enchantment enchantment = Enchantment.enchantmentsBookList[par1Random.nextInt(Enchantment.enchantmentsBookList.length)];
        final ItemStack itemstack = new ItemStack(this.itemID, 1, 0);
        final int l = MathHelper.getRandomIntegerInRange(par1Random, enchantment.getMinLevel(), enchantment.getMaxLevel());
        this.addEnchantment(itemstack, new EnchantmentData(enchantment, l));
        return new WeightedRandomChestContent(itemstack, par2, par3, par4);
    }
}
