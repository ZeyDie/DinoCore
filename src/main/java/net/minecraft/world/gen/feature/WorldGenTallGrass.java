package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenTallGrass extends WorldGenerator
{
    /** Stores ID for WorldGenTallGrass */
    private int tallGrassID;
    private int tallGrassMetadata;

    public WorldGenTallGrass(final int par1, final int par2)
    {
        this.tallGrassID = par1;
        this.tallGrassMetadata = par2;
    }

    public boolean generate(final World par1World, final Random par2Random, final int par3, int par4, final int par5)
    {
        int par41 = par4;
        int l;

        Block block = null;
        do 
        {
            block = Block.blocksList[par1World.getBlockId(par3, par41, par5)];
            if (block != null && !block.isLeaves(par1World, par3, par41, par5))
            {
                break;
            }
            par41--;
        } while (par41 > 0);

        for (int i1 = 0; i1 < 128; ++i1)
        {
            final int j1 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
            final int k1 = par41 + par2Random.nextInt(4) - par2Random.nextInt(4);
            final int l1 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);

            if (par1World.isAirBlock(j1, k1, l1) && Block.blocksList[this.tallGrassID].canBlockStay(par1World, j1, k1, l1))
            {
                par1World.setBlock(j1, k1, l1, this.tallGrassID, this.tallGrassMetadata, 2);
            }
        }

        return true;
    }
}
