package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemReed extends Item
{
    /** The ID of the block the reed will spawn when used from inventory bar. */
    private int spawnID;

    public ItemReed(final int par1, final Block par2Block)
    {
        super(par1);
        this.spawnID = par2Block.blockID;
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, int par4, int par5, int par6, int par7, final float par8, final float par9, final float par10)
    {
        int par71 = par7;
        int par51 = par5;
        int par61 = par6;
        int par41 = par4;
        final int i1 = par3World.getBlockId(par41, par51, par61);

        if (i1 == Block.snow.blockID && (par3World.getBlockMetadata(par41, par51, par61) & 7) < 1)
        {
            par71 = 1;
        }
        else if (i1 != Block.vine.blockID && i1 != Block.tallGrass.blockID && i1 != Block.deadBush.blockID)
        {
            if (par71 == 0)
            {
                --par51;
            }

            if (par71 == 1)
            {
                ++par51;
            }

            if (par71 == 2)
            {
                --par61;
            }

            if (par71 == 3)
            {
                ++par61;
            }

            if (par71 == 4)
            {
                --par41;
            }

            if (par71 == 5)
            {
                ++par41;
            }
        }

        if (!par2EntityPlayer.canPlayerEdit(par41, par51, par61, par71, par1ItemStack))
        {
            return false;
        }
        else if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else
        {
            if (par3World.canPlaceEntityOnSide(this.spawnID, par41, par51, par61, false, par71, (Entity)null, par1ItemStack))
            {
                final Block block = Block.blocksList[this.spawnID];
                final int j1 = block.onBlockPlaced(par3World, par41, par51, par61, par71, par8, par9, par10, 0);

                if (par3World.setBlock(par41, par51, par61, this.spawnID, j1, 3))
                {
                    if (par3World.getBlockId(par41, par51, par61) == this.spawnID)
                    {
                        Block.blocksList[this.spawnID].onBlockPlacedBy(par3World, par41, par51, par61, par2EntityPlayer, par1ItemStack);
                        Block.blocksList[this.spawnID].onPostBlockPlaced(par3World, par41, par51, par61, j1);
                    }

                    par3World.playSoundEffect((double)((float) par41 + 0.5F), (double)((float) par51 + 0.5F), (double)((float) par61 + 0.5F), block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                    --par1ItemStack.stackSize;
                }
            }

            return true;
        }
    }
}
