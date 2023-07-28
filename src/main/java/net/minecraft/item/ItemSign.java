package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSign extends Item
{
    public ItemSign(final int par1)
    {
        super(par1);
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabDecorations);
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
        if (par7 == 0)
        {
            return false;
        }
        else if (!par3World.getBlockMaterial(par41, par51, par61).isSolid())
        {
            return false;
        }
        else
        {
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
            else if (!Block.signPost.canPlaceBlockAt(par3World, par41, par51, par61))
            {
                return false;
            }
            else if (par3World.isRemote)
            {
                return true;
            }
            else
            {
                if (par7 == 1)
                {
                    final int i1 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    par3World.setBlock(par41, par51, par61, Block.signPost.blockID, i1, 3);
                }
                else
                {
                    par3World.setBlock(par41, par51, par61, Block.signWall.blockID, par7, 3);
                }

                --par1ItemStack.stackSize;
                final TileEntitySign tileentitysign = (TileEntitySign)par3World.getBlockTileEntity(par41, par51, par61);

                if (tileentitysign != null)
                {
                    par2EntityPlayer.displayGUIEditSign(tileentitysign);
                }

                return true;
            }
        }
    }
}
