package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenMinable extends WorldGenerator
{
    /** The block ID of the ore to be placed using this generator. */
    private int minableBlockId;
    private int minableBlockMeta = 0;

    /** The number of blocks to generate. */
    private int numberOfBlocks;

    /** The block ID of the block to be replaced with the ore (usually stone) */
    private int blockToReplace;

    public WorldGenMinable(final int par1, final int par2)
    {
        this(par1, par2, Block.stone.blockID);
    }

    public WorldGenMinable(final int par1, final int par2, final int par3)
    {
        this.minableBlockId = par1;
        this.numberOfBlocks = par2;
        this.blockToReplace = par3;
    }

    public WorldGenMinable(final int id, final int meta, final int number, final int target)
    {
        this(id, number, target);
        this.minableBlockMeta = meta;
    }

    public boolean generate(final World par1World, final Random par2Random, final int par3, final int par4, final int par5)
    {
        final float f = par2Random.nextFloat() * (float)Math.PI;
        final double d0 = (double)((float)(par3 + 8) + MathHelper.sin(f) * (float)this.numberOfBlocks / 8.0F);
        final double d1 = (double)((float)(par3 + 8) - MathHelper.sin(f) * (float)this.numberOfBlocks / 8.0F);
        final double d2 = (double)((float)(par5 + 8) + MathHelper.cos(f) * (float)this.numberOfBlocks / 8.0F);
        final double d3 = (double)((float)(par5 + 8) - MathHelper.cos(f) * (float)this.numberOfBlocks / 8.0F);
        final double d4 = (double)(par4 + par2Random.nextInt(3) - 2);
        final double d5 = (double)(par4 + par2Random.nextInt(3) - 2);

        for (int l = 0; l <= this.numberOfBlocks; ++l)
        {
            final double d6 = d0 + (d1 - d0) * (double)l / (double)this.numberOfBlocks;
            final double d7 = d4 + (d5 - d4) * (double)l / (double)this.numberOfBlocks;
            final double d8 = d2 + (d3 - d2) * (double)l / (double)this.numberOfBlocks;
            final double d9 = par2Random.nextDouble() * (double)this.numberOfBlocks / 16.0D;
            final double d10 = (double)(MathHelper.sin((float)l * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * d9 + 1.0D;
            final double d11 = (double)(MathHelper.sin((float)l * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * d9 + 1.0D;
            final int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
            final int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
            final int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
            final int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
            final int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
            final int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

            for (int k2 = i1; k2 <= l1; ++k2)
            {
                final double d12 = ((double)k2 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D)
                {
                    for (int l2 = j1; l2 <= i2; ++l2)
                    {
                        final double d13 = ((double)l2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D)
                        {
                            for (int i3 = k1; i3 <= j2; ++i3)
                            {
                                final double d14 = ((double)i3 + 0.5D - d8) / (d10 / 2.0D);

                                final Block block = Block.blocksList[par1World.getBlockId(k2, l2, i3)];
                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && (block != null && block.isGenMineableReplaceable(par1World, k2, l2, i3, this.blockToReplace)))
                                {
                                    par1World.setBlock(k2, l2, i3, this.minableBlockId, minableBlockMeta, 2);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
