package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;

public class ItemAnvilBlock extends ItemMultiTextureTile
{
    public ItemAnvilBlock(final Block par1Block)
    {
        super(par1Block.blockID - 256, par1Block, BlockAnvil.statuses);
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    public int getMetadata(final int par1)
    {
        return par1 << 2;
    }
}
