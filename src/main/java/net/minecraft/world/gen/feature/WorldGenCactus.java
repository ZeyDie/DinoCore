package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenCactus extends WorldGenerator
{
    public boolean generate(final World par1World, final Random par2Random, final int par3, final int par4, final int par5)
    {
        for (int l = 0; l < 10; ++l)
        {
            final int i1 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
            final int j1 = par4 + par2Random.nextInt(4) - par2Random.nextInt(4);
            final int k1 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);

            if (par1World.isAirBlock(i1, j1, k1))
            {
                final int l1 = 1 + par2Random.nextInt(par2Random.nextInt(3) + 1);

                for (int i2 = 0; i2 < l1; ++i2)
                {
                    if (Block.cactus.canBlockStay(par1World, i1, j1 + i2, k1))
                    {
                        par1World.setBlock(i1, j1 + i2, k1, Block.cactus.blockID, 0, 2);
                    }
                }
            }
        }

        return true;
    }
}
