package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.util.Icon;

public class ItemBlockWithMetadata extends ItemBlock
{
    private Block theBlock;

    public ItemBlockWithMetadata(final int par1, final Block par2Block)
    {
        super(par1);
        this.theBlock = par2Block;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets an icon index based on an item's damage value
     */
    public Icon getIconFromDamage(final int par1)
    {
        return this.theBlock.getIcon(2, par1);
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    public int getMetadata(final int par1)
    {
        return par1;
    }
}
