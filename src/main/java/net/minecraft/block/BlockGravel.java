package net.minecraft.block;

import net.minecraft.item.Item;

import java.util.Random;

public class BlockGravel extends BlockSand
{
    public BlockGravel(final int par1)
    {
        super(par1);
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, int par3)
    {
        int par31 = par3;
        if (par31 > 3)
        {
            par31 = 3;
        }

        return par2Random.nextInt(10 - par31 * 3) == 0 ? Item.flint.itemID : this.blockID;
    }
}
