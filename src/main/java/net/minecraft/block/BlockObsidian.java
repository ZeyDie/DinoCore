package net.minecraft.block;

import java.util.Random;

public class BlockObsidian extends BlockStone
{
    public BlockObsidian(final int par1)
    {
        super(par1);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random par1Random)
    {
        return 1;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Block.obsidian.blockID;
    }
}
