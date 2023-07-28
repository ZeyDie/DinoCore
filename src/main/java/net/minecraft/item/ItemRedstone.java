package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemRedstone extends Item
{
    public ItemRedstone(final int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabRedstone);
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
        if (par3World.getBlockId(par41, par51, par61) != Block.snow.blockID)
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

            if (!par3World.isAirBlock(par41, par51, par61))
            {
                return false;
            }
        }

        if (!par2EntityPlayer.canPlayerEdit(par41, par51, par61, par7, par1ItemStack))
        {
            return false;
        }
        else
        {
            if (Block.redstoneWire.canPlaceBlockAt(par3World, par41, par51, par61))
            {
                --par1ItemStack.stackSize;
                par3World.setBlock(par41, par51, par61, Block.redstoneWire.blockID);
            }

            return true;
        }
    }
}
