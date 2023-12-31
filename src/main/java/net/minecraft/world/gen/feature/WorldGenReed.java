package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenReed extends WorldGenerator
{
    public boolean generate(final World par1World, final Random par2Random, final int par3, final int par4, final int par5)
    {
        for (int l = 0; l < 20; ++l)
        {
            final int i1 = par3 + par2Random.nextInt(4) - par2Random.nextInt(4);
            final int j1 = par4;
            final int k1 = par5 + par2Random.nextInt(4) - par2Random.nextInt(4);

            if (par1World.isAirBlock(i1, par4, k1) && (par1World.getBlockMaterial(i1 - 1, par4 - 1, k1) == Material.water || par1World.getBlockMaterial(i1 + 1, par4 - 1, k1) == Material.water || par1World.getBlockMaterial(i1, par4 - 1, k1 - 1) == Material.water || par1World.getBlockMaterial(i1, par4 - 1, k1 + 1) == Material.water))
            {
                final int l1 = 2 + par2Random.nextInt(par2Random.nextInt(3) + 1);

                for (int i2 = 0; i2 < l1; ++i2)
                {
                    if (Block.reed.canBlockStay(par1World, i1, j1 + i2, k1))
                    {
                        par1World.setBlock(i1, j1 + i2, k1, Block.reed.blockID, 0, 2);
                    }
                }
            }
        }

        return true;
    }
}
