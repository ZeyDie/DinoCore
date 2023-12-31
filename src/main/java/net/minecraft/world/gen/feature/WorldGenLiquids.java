package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenLiquids extends WorldGenerator
{
    /** The ID of the liquid block used in this liquid generator. */
    private int liquidBlockId;

    public WorldGenLiquids(final int par1)
    {
        this.liquidBlockId = par1;
    }

    public boolean generate(final World par1World, final Random par2Random, final int par3, final int par4, final int par5)
    {
        if (par1World.getBlockId(par3, par4 + 1, par5) != Block.stone.blockID)
        {
            return false;
        }
        else if (par1World.getBlockId(par3, par4 - 1, par5) != Block.stone.blockID)
        {
            return false;
        }
        else if (par1World.getBlockId(par3, par4, par5) != 0 && par1World.getBlockId(par3, par4, par5) != Block.stone.blockID)
        {
            return false;
        }
        else
        {
            int l = 0;

            if (par1World.getBlockId(par3 - 1, par4, par5) == Block.stone.blockID)
            {
                ++l;
            }

            if (par1World.getBlockId(par3 + 1, par4, par5) == Block.stone.blockID)
            {
                ++l;
            }

            if (par1World.getBlockId(par3, par4, par5 - 1) == Block.stone.blockID)
            {
                ++l;
            }

            if (par1World.getBlockId(par3, par4, par5 + 1) == Block.stone.blockID)
            {
                ++l;
            }

            int i1 = 0;

            if (par1World.isAirBlock(par3 - 1, par4, par5))
            {
                ++i1;
            }

            if (par1World.isAirBlock(par3 + 1, par4, par5))
            {
                ++i1;
            }

            if (par1World.isAirBlock(par3, par4, par5 - 1))
            {
                ++i1;
            }

            if (par1World.isAirBlock(par3, par4, par5 + 1))
            {
                ++i1;
            }

            if (l == 3 && i1 == 1)
            {
                par1World.setBlock(par3, par4, par5, this.liquidBlockId, 0, 2);
                par1World.scheduledUpdatesAreImmediate = true;
                Block.blocksList[this.liquidBlockId].updateTick(par1World, par3, par4, par5, par2Random);
                par1World.scheduledUpdatesAreImmediate = false;
            }

            return true;
        }
    }
}
