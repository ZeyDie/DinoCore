package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenHellLava extends WorldGenerator
{
    /** Stores the ID for WorldGenHellLava */
    private int hellLavaID;
    private boolean field_94524_b;

    public WorldGenHellLava(final int par1, final boolean par2)
    {
        this.hellLavaID = par1;
        this.field_94524_b = par2;
    }

    public boolean generate(final World par1World, final Random par2Random, final int par3, final int par4, final int par5)
    {
        if (par1World.getBlockId(par3, par4 + 1, par5) != Block.netherrack.blockID)
        {
            return false;
        }
        else if (par1World.getBlockId(par3, par4, par5) != 0 && par1World.getBlockId(par3, par4, par5) != Block.netherrack.blockID)
        {
            return false;
        }
        else
        {
            int l = 0;

            if (par1World.getBlockId(par3 - 1, par4, par5) == Block.netherrack.blockID)
            {
                ++l;
            }

            if (par1World.getBlockId(par3 + 1, par4, par5) == Block.netherrack.blockID)
            {
                ++l;
            }

            if (par1World.getBlockId(par3, par4, par5 - 1) == Block.netherrack.blockID)
            {
                ++l;
            }

            if (par1World.getBlockId(par3, par4, par5 + 1) == Block.netherrack.blockID)
            {
                ++l;
            }

            if (par1World.getBlockId(par3, par4 - 1, par5) == Block.netherrack.blockID)
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

            if (par1World.isAirBlock(par3, par4 - 1, par5))
            {
                ++i1;
            }

            if (!this.field_94524_b && l == 4 && i1 == 1 || l == 5)
            {
                par1World.setBlock(par3, par4, par5, this.hellLavaID, 0, 2);
                par1World.scheduledUpdatesAreImmediate = true;
                Block.blocksList[this.hellLavaID].updateTick(par1World, par3, par4, par5, par2Random);
                par1World.scheduledUpdatesAreImmediate = false;
            }

            return true;
        }
    }
}
