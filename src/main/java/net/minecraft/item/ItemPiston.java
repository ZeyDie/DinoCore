package net.minecraft.item;

public class ItemPiston extends ItemBlock
{
    public ItemPiston(final int par1)
    {
        super(par1);
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    public int getMetadata(final int par1)
    {
        return 7;
    }
}
