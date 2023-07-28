package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import java.util.List;

public class BlockStoneBrick extends Block
{
    public static final String[] STONE_BRICK_TYPES = {"default", "mossy", "cracked", "chiseled"};
    public static final String[] field_94407_b = {null, "mossy", "cracked", "carved"};
    @SideOnly(Side.CLIENT)
    private Icon[] field_94408_c;

    public BlockStoneBrick(final int par1)
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
        if (par21 < 0 || par21 >= field_94407_b.length)
        {
            par21 = 0;
        }

        return this.field_94408_c[par21];
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(final int par1)
    {
        return par1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(final int par1, final CreativeTabs par2CreativeTabs, final List par3List)
    {
        for (int j = 0; j < 4; ++j)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.field_94408_c = new Icon[field_94407_b.length];

        for (int i = 0; i < this.field_94408_c.length; ++i)
        {
            String s = this.getTextureName();

            if (field_94407_b[i] != null)
            {
                s = s + "_" + field_94407_b[i];
            }

            this.field_94408_c[i] = par1IconRegister.registerIcon(s);
        }
    }
}
