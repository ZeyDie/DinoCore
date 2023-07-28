package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemFireball extends Item
{
    public ItemFireball(final int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, int par4, int par5, int par6, final int par7, final float par8, final float par9, final float par10)
    {
        int par51 = par5;
        int par61 = par6;
        int par41 = par4;
        if (par3World.isRemote)
        {
            return true;
        }
        else
        {
            if (par7 == 0)
            {
                --par51;
            }

            if (par7 == 1)
            {
                ++par51;
            }

            if (par7 == 2)
            {
                --par61;
            }

            if (par7 == 3)
            {
                ++par61;
            }

            if (par7 == 4)
            {
                --par41;
            }

            if (par7 == 5)
            {
                ++par41;
            }

            if (!par2EntityPlayer.canPlayerEdit(par41, par51, par61, par7, par1ItemStack))
            {
                return false;
            }
            else
            {
                final int i1 = par3World.getBlockId(par41, par51, par61);

                if (i1 == 0)
                {
                    // CraftBukkit start
                    if (org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callBlockIgniteEvent(par3World, par41, par51, par61, org.bukkit.event.block.BlockIgniteEvent.IgniteCause.FIREBALL, par2EntityPlayer).isCancelled())
                    {
                        if (!par2EntityPlayer.capabilities.isCreativeMode)
                        {
                            --par1ItemStack.stackSize;
                        }

                        return false;
                    }

                    // CraftBukkit end
                    par3World.playSoundEffect((double) par41 + 0.5D, (double) par51 + 0.5D, (double) par61 + 0.5D, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                    par3World.setBlock(par41, par51, par61, Block.fire.blockID);
                }

                if (!par2EntityPlayer.capabilities.isCreativeMode)
                {
                    --par1ItemStack.stackSize;
                }

                return true;
            }
        }
    }
}
