package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.util.Icon;

public class ItemMultiTextureTile extends ItemBlock
{
    private final Block theBlock;
    private final String[] field_82804_b;

    public ItemMultiTextureTile(final int par1, final Block par2Block, final String[] par3ArrayOfStr)
    {
        super(par1);
        this.theBlock = par2Block;
        this.field_82804_b = par3ArrayOfStr;
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

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(final ItemStack par1ItemStack)
    {
        int i = par1ItemStack.getItemDamage();

        if (i < 0 || i >= this.field_82804_b.length)
        {
            i = 0;
        }

        return super.getUnlocalizedName() + "." + this.field_82804_b[i];
    }
}
