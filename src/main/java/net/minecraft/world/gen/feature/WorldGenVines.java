package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenVines extends WorldGenerator
{
    public boolean generate(final World par1World, final Random par2Random, int par3, int par4, int par5)
    {
        int par41 = par4;
        int par31 = par3;
        int par51 = par5;
        final int l = par31;

        for (final int i1 = par51; par41 < 128; ++par41)
        {
            if (par1World.isAirBlock(par31, par41, par51))
            {
                for (int j1 = 2; j1 <= 5; ++j1)
                {
                    if (Block.vine.canPlaceBlockOnSide(par1World, par31, par41, par51, j1))
                    {
                        par1World.setBlock(par31, par41, par51, Block.vine.blockID, 1 << Direction.facingToDirection[Facing.oppositeSide[j1]], 2);
                        break;
                    }
                }
            }
            else
            {
                par31 = l + par2Random.nextInt(4) - par2Random.nextInt(4);
                par51 = i1 + par2Random.nextInt(4) - par2Random.nextInt(4);
            }
        }

        return true;
    }
}
