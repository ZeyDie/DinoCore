package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public abstract class BlockRotatedPillar extends Block
{
    @SideOnly(Side.CLIENT)
    protected Icon field_111051_a;

    protected BlockRotatedPillar(final int par1, final Material par2Material)
    {
        super(par1, par2Material);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 31;
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7, final float par8, final int par9)
    {
        final int j1 = par9 & 3;
        byte b0 = 0;

        switch (par5)
        {
            case 0:
            case 1:
                b0 = 0;
                break;
            case 2:
            case 3:
                b0 = 8;
                break;
            case 4:
            case 5:
                b0 = 4;
        }

        return j1 | b0;
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        final int k = par2 & 12;
        final int l = par2 & 3;
        return k == 0 && (par1 == 1 || par1 == 0) ? this.getEndIcon(l) : (k == 4 && (par1 == 5 || par1 == 4) ? this.getEndIcon(l) : (k == 8 && (par1 == 2 || par1 == 3) ? this.getEndIcon(l) : this.getSideIcon(l)));
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(final int par1)
    {
        return par1 & 3;
    }

    @SideOnly(Side.CLIENT)

    /**
     * The icon for the side of the block.
     */
    protected abstract Icon getSideIcon(int i);

    @SideOnly(Side.CLIENT)

    /**
     * The icon for the tops and bottoms of the block.
     */
    protected Icon getEndIcon(final int par1)
    {
        return this.field_111051_a;
    }

    public int func_111050_e(final int par1)
    {
        return par1 & 3;
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(final int par1)
    {
        return new ItemStack(this.blockID, 1, this.func_111050_e(par1));
    }
}
