package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import java.util.List;

public class BlockQuartz extends Block
{
    public static final String[] quartzBlockTypes = {"default", "chiseled", "lines"};
    private static final String[] quartzBlockTextureTypes = {"side", "chiseled", "lines", null, null};
    @SideOnly(Side.CLIENT)
    private Icon[] quartzblockIcons;
    @SideOnly(Side.CLIENT)
    private Icon quartzblock_chiseled_top;
    @SideOnly(Side.CLIENT)
    private Icon quartzblock_lines_top;
    @SideOnly(Side.CLIENT)
    private Icon quartzblock_top;
    @SideOnly(Side.CLIENT)
    private Icon quartzblock_bottom;

    public BlockQuartz(final int par1)
    {
        super(par1, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, int par2)
    {
        int par21 = par2;
        if (par21 != 2 && par21 != 3 && par21 != 4)
        {
            if (par1 != 1 && (par1 != 0 || par21 != 1))
            {
                if (par1 == 0)
                {
                    return this.quartzblock_bottom;
                }
                else
                {
                    if (par21 < 0 || par21 >= this.quartzblockIcons.length)
                    {
                        par21 = 0;
                    }

                    return this.quartzblockIcons[par21];
                }
            }
            else
            {
                return par21 == 1 ? this.quartzblock_chiseled_top : this.quartzblock_top;
            }
        }
        else
        {
            return par21 == 2 && (par1 == 1 || par1 == 0) ? this.quartzblock_lines_top : (par21 == 3 && (par1 == 5 || par1 == 4) ? this.quartzblock_lines_top : (par21 == 4 && (par1 == 2 || par1 == 3) ? this.quartzblock_lines_top : this.quartzblockIcons[par21]));
        }
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7, final float par8, int par9)
    {
        int par91 = par9;
        if (par91 == 2)
        {
            switch (par5)
            {
                case 0:
                case 1:
                    par91 = 2;
                    break;
                case 2:
                case 3:
                    par91 = 4;
                    break;
                case 4:
                case 5:
                    par91 = 3;
            }
        }

        return par91;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(final int par1)
    {
        return par1 != 3 && par1 != 4 ? par1 : 2;
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(final int par1)
    {
        return par1 != 3 && par1 != 4 ? super.createStackedBlock(par1) : new ItemStack(this.blockID, 1, 2);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 39;
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(final int par1, final CreativeTabs par2CreativeTabs, final List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.quartzblockIcons = new Icon[quartzBlockTextureTypes.length];

        for (int i = 0; i < this.quartzblockIcons.length; ++i)
        {
            if (quartzBlockTextureTypes[i] == null)
            {
                this.quartzblockIcons[i] = this.quartzblockIcons[i - 1];
            }
            else
            {
                this.quartzblockIcons[i] = par1IconRegister.registerIcon(this.getTextureName() + "_" + quartzBlockTextureTypes[i]);
            }
        }

        this.quartzblock_top = par1IconRegister.registerIcon(this.getTextureName() + "_" + "top");
        this.quartzblock_chiseled_top = par1IconRegister.registerIcon(this.getTextureName() + "_" + "chiseled_top");
        this.quartzblock_lines_top = par1IconRegister.registerIcon(this.getTextureName() + "_" + "lines_top");
        this.quartzblock_bottom = par1IconRegister.registerIcon(this.getTextureName() + "_" + "bottom");
    }
}
