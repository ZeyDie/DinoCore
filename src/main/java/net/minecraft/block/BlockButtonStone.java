package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Icon;

public class BlockButtonStone extends BlockButton
{
    protected BlockButtonStone(final int par1)
    {
        super(par1, false);
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return Block.stone.getBlockTextureFromSide(1);
    }
}
