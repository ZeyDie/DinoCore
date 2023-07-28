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
import java.util.Random;

public class BlockLog extends BlockRotatedPillar
{
    /** The type of tree this log came from. */
    public static final String[] woodType = {"oak", "spruce", "birch", "jungle"};
    @SideOnly(Side.CLIENT)
    private Icon[] field_111052_c;
    @SideOnly(Side.CLIENT)
    private Icon[] tree_top;

    protected BlockLog(final int par1)
    {
        super(par1, Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
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
        return Block.wood.blockID;
    }

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final byte b0 = 4;
        final int j1 = b0 + 1;

        if (par1World.checkChunksExist(par2 - j1, par3 - j1, par4 - j1, par2 + j1, par3 + j1, par4 + j1))
        {
            for (int k1 = -b0; k1 <= b0; ++k1)
            {
                for (int l1 = -b0; l1 <= b0; ++l1)
                {
                    for (int i2 = -b0; i2 <= b0; ++i2)
                    {
                        final int j2 = par1World.getBlockId(par2 + k1, par3 + l1, par4 + i2);

                        if (Block.blocksList[j2] != null)
                        {
                            Block.blocksList[j2].beginLeavesDecay(par1World, par2 + k1, par3 + l1, par4 + i2);
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * The icon for the side of the block.
     */
    protected Icon getSideIcon(final int par1)
    {
        return this.field_111052_c[par1];
    }

    @SideOnly(Side.CLIENT)

    /**
     * The icon for the tops and bottoms of the block.
     */
    protected Icon getEndIcon(final int par1)
    {
        return this.tree_top[par1];
    }

    /**
     * returns a number between 0 and 3
     */
    public static int limitToValidMetadata(final int par0)
    {
        return par0 & 3;
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
        par3List.add(new ItemStack(par1, 1, 3));
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.field_111052_c = new Icon[woodType.length];
        this.tree_top = new Icon[woodType.length];

        for (int i = 0; i < this.field_111052_c.length; ++i)
        {
            this.field_111052_c[i] = par1IconRegister.registerIcon(this.getTextureName() + "_" + woodType[i]);
            this.tree_top[i] = par1IconRegister.registerIcon(this.getTextureName() + "_" + woodType[i] + "_top");
        }
    }

    @Override
    public boolean canSustainLeaves(final World world, final int x, final int y, final int z)
    {
        return true;
    }

    @Override
    public boolean isWood(final World world, final int x, final int y, final int z)
    {
        return true;
    }
}
