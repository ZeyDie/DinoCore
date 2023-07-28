package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;
import java.util.Random;

public class ItemShears extends Item
{
    public ItemShears(final int par1)
    {
        super(par1);
        this.setMaxStackSize(1);
        this.setMaxDamage(238);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public boolean onBlockDestroyed(final ItemStack par1ItemStack, final World par2World, final int par3, final int par4, final int par5, final int par6, final EntityLivingBase par7EntityLivingBase)
    {
        if (par3 != Block.leaves.blockID && par3 != Block.web.blockID && par3 != Block.tallGrass.blockID && par3 != Block.vine.blockID && par3 != Block.tripWire.blockID && !(Block.blocksList[par3] instanceof IShearable))
        {
            return super.onBlockDestroyed(par1ItemStack, par2World, par3, par4, par5, par6, par7EntityLivingBase);
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns if the item (tool) can harvest results from the block type.
     */
    public boolean canHarvestBlock(final Block par1Block)
    {
        return par1Block.blockID == Block.web.blockID || par1Block.blockID == Block.redstoneWire.blockID || par1Block.blockID == Block.tripWire.blockID;
    }

    /**
     * Returns the strength of the stack against a given block. 1.0F base, (Quality+1)*2 if correct blocktype, 1.5F if
     * sword
     */
    public float getStrVsBlock(final ItemStack par1ItemStack, final Block par2Block)
    {
        return par2Block.blockID != Block.web.blockID && par2Block.blockID != Block.leaves.blockID ? (par2Block.blockID == Block.cloth.blockID ? 5.0F : super.getStrVsBlock(par1ItemStack, par2Block)) : 15.0F;
    }

    @Override
    public boolean itemInteractionForEntity(final ItemStack itemstack, final EntityPlayer player, final EntityLivingBase entity)
    {
        if (entity.worldObj.isRemote)
        {
            return false;
        }
        if (entity instanceof IShearable)
        {
            final IShearable target = (IShearable)entity;
            if (target.isShearable(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ))
            {
                final ArrayList<ItemStack> drops = target.onSheared(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ,
                        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));

                final Random rand = new Random();
                for(final ItemStack stack : drops)
                {
                    final EntityItem ent = entity.entityDropItem(stack, 1.0F);
                    ent.motionY += rand.nextFloat() * 0.05F;
                    ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                    ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                }
                itemstack.damageItem(1, entity);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockStartBreak(final ItemStack itemstack, final int x, final int y, final int z, final EntityPlayer player)
    {
        if (player.worldObj.isRemote)
        {
            return false;
        }
        final int id = player.worldObj.getBlockId(x, y, z);
        if (Block.blocksList[id] instanceof IShearable)
        {
            final IShearable target = (IShearable)Block.blocksList[id];
            if (target.isShearable(itemstack, player.worldObj, x, y, z))
            {
                final ArrayList<ItemStack> drops = target.onSheared(itemstack, player.worldObj, x, y, z,
                        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
                final Random rand = new Random();

                for(final ItemStack stack : drops)
                {
                    final float f = 0.7F;
                    final double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    final double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    final double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    final EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
                    entityitem.delayBeforeCanPickup = 10;
                    player.worldObj.spawnEntityInWorld(entityitem);
                }

                itemstack.damageItem(1, player);
                player.addStat(StatList.mineBlockStatArray[id], 1);
            }
        }
        return false;
    }
}
