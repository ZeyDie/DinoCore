package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ItemNameTag extends Item
{
    public ItemNameTag(final int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final EntityLivingBase par3EntityLivingBase)
    {
        if (!par1ItemStack.hasDisplayName())
        {
            return false;
        }
        else if (par3EntityLivingBase instanceof EntityLiving)
        {
            final EntityLiving entityliving = (EntityLiving)par3EntityLivingBase;
            entityliving.setCustomNameTag(par1ItemStack.getDisplayName());
            entityliving.func_110163_bv();
            --par1ItemStack.stackSize;
            return true;
        }
        else
        {
            return super.itemInteractionForEntity(par1ItemStack, par2EntityPlayer, par3EntityLivingBase);
        }
    }
}
