package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item
{
    public ItemBed(final int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, int par5, final int par6, final int par7, final float par8, final float par9, final float par10)
    {
        int par51 = par5;
        if (par3World.isRemote)
        {
            return true;
        }
        else if (par7 != 1)
        {
            return false;
        }
        else
        {
            ++par51;
            final BlockBed blockbed = (BlockBed)Block.bed;
            final int i1 = MathHelper.floor_double((double)(par2EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            byte b0 = 0;
            byte b1 = 0;

            if (i1 == 0)
            {
                b1 = 1;
            }

            if (i1 == 1)
            {
                b0 = -1;
            }

            if (i1 == 2)
            {
                b1 = -1;
            }

            if (i1 == 3)
            {
                b0 = 1;
            }

            if (par2EntityPlayer.canPlayerEdit(par4, par51, par6, par7, par1ItemStack) && par2EntityPlayer.canPlayerEdit(par4 + b0, par51, par6 + b1, par7, par1ItemStack))
            {
                if (par3World.isAirBlock(par4, par51, par6) && par3World.isAirBlock(par4 + b0, par51, par6 + b1) && par3World.doesBlockHaveSolidTopSurface(par4, par51 - 1, par6) && par3World.doesBlockHaveSolidTopSurface(par4 + b0, par51 - 1, par6 + b1))
                {
                    par3World.setBlock(par4, par51, par6, blockbed.blockID, i1, 3);

                    if (par3World.getBlockId(par4, par51, par6) == blockbed.blockID)
                    {
                        par3World.setBlock(par4 + b0, par51, par6 + b1, blockbed.blockID, i1 + 8, 3);
                    }

                    --par1ItemStack.stackSize;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
    }
}
