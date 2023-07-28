package net.minecraft.world.biome;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class BiomeGenHills extends BiomeGenBase
{
    private WorldGenerator theWorldGenerator;

    protected BiomeGenHills(final int par1)
    {
        super(par1);
        this.theWorldGenerator = new WorldGenMinable(Block.silverfish.blockID, 8);
    }

    public void decorate(final World par1World, final Random par2Random, final int par3, final int par4)
    {
        super.decorate(par1World, par2Random, par3, par4);
        int k = 3 + par2Random.nextInt(6);
        int l;
        int i1;
        int j1;

        for (l = 0; l < k; ++l)
        {
            i1 = par3 + par2Random.nextInt(16);
            j1 = par2Random.nextInt(28) + 4;
            final int k1 = par4 + par2Random.nextInt(16);
            final int l1 = par1World.getBlockId(i1, j1, k1);

            if (l1 == Block.stone.blockID)
            {
                par1World.setBlock(i1, j1, k1, Block.oreEmerald.blockID, 0, 2);
            }
        }

        for (k = 0; k < 7; ++k)
        {
            l = par3 + par2Random.nextInt(16);
            i1 = par2Random.nextInt(64);
            j1 = par4 + par2Random.nextInt(16);
            this.theWorldGenerator.generate(par1World, par2Random, l, i1, j1);
        }
    }
}
