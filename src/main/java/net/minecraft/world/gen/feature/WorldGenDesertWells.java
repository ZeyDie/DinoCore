package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenDesertWells extends WorldGenerator
{
    public boolean generate(final World par1World, final Random par2Random, final int par3, int par4, final int par5)
    {
        int par41 = par4;
        while (par1World.isAirBlock(par3, par41, par5) && par41 > 2)
        {
            --par41;
        }

        final int l = par1World.getBlockId(par3, par41, par5);

        if (l != Block.sand.blockID)
        {
            return false;
        }
        else
        {
            int i1;
            int j1;

            for (i1 = -2; i1 <= 2; ++i1)
            {
                for (j1 = -2; j1 <= 2; ++j1)
                {
                    if (par1World.isAirBlock(par3 + i1, par41 - 1, par5 + j1) && par1World.isAirBlock(par3 + i1, par41 - 2, par5 + j1))
                    {
                        return false;
                    }
                }
            }

            for (i1 = -1; i1 <= 0; ++i1)
            {
                for (j1 = -2; j1 <= 2; ++j1)
                {
                    for (int k1 = -2; k1 <= 2; ++k1)
                    {
                        par1World.setBlock(par3 + j1, par41 + i1, par5 + k1, Block.sandStone.blockID, 0, 2);
                    }
                }
            }

            par1World.setBlock(par3, par41, par5, Block.waterMoving.blockID, 0, 2);
            par1World.setBlock(par3 - 1, par41, par5, Block.waterMoving.blockID, 0, 2);
            par1World.setBlock(par3 + 1, par41, par5, Block.waterMoving.blockID, 0, 2);
            par1World.setBlock(par3, par41, par5 - 1, Block.waterMoving.blockID, 0, 2);
            par1World.setBlock(par3, par41, par5 + 1, Block.waterMoving.blockID, 0, 2);

            for (i1 = -2; i1 <= 2; ++i1)
            {
                for (j1 = -2; j1 <= 2; ++j1)
                {
                    if (i1 == -2 || i1 == 2 || j1 == -2 || j1 == 2)
                    {
                        par1World.setBlock(par3 + i1, par41 + 1, par5 + j1, Block.sandStone.blockID, 0, 2);
                    }
                }
            }

            par1World.setBlock(par3 + 2, par41 + 1, par5, Block.stoneSingleSlab.blockID, 1, 2);
            par1World.setBlock(par3 - 2, par41 + 1, par5, Block.stoneSingleSlab.blockID, 1, 2);
            par1World.setBlock(par3, par41 + 1, par5 + 2, Block.stoneSingleSlab.blockID, 1, 2);
            par1World.setBlock(par3, par41 + 1, par5 - 2, Block.stoneSingleSlab.blockID, 1, 2);

            for (i1 = -1; i1 <= 1; ++i1)
            {
                for (j1 = -1; j1 <= 1; ++j1)
                {
                    if (i1 == 0 && j1 == 0)
                    {
                        par1World.setBlock(par3 + i1, par41 + 4, par5 + j1, Block.sandStone.blockID, 0, 2);
                    }
                    else
                    {
                        par1World.setBlock(par3 + i1, par41 + 4, par5 + j1, Block.stoneSingleSlab.blockID, 1, 2);
                    }
                }
            }

            for (i1 = 1; i1 <= 3; ++i1)
            {
                par1World.setBlock(par3 - 1, par41 + i1, par5 - 1, Block.sandStone.blockID, 0, 2);
                par1World.setBlock(par3 - 1, par41 + i1, par5 + 1, Block.sandStone.blockID, 0, 2);
                par1World.setBlock(par3 + 1, par41 + i1, par5 - 1, Block.sandStone.blockID, 0, 2);
                par1World.setBlock(par3 + 1, par41 + i1, par5 + 1, Block.sandStone.blockID, 0, 2);
            }

            return true;
        }
    }
}
