package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import java.util.List;

public class BlockSandStone extends Block
{
    public static final String[] SAND_STONE_TYPES = {"default", "chiseled", "smooth"};
    private static final String[] field_94405_b = {"normal", "carved", "smooth"};
    @SideOnly(Side.CLIENT)
    private Icon[] field_94406_c;
    @SideOnly(Side.CLIENT)
    private Icon field_94403_cO;
    @SideOnly(Side.CLIENT)
    private Icon field_94404_cP;

    public BlockSandStone(final int par1)
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
        if (par1 != 1 && (par1 != 0 || par21 != 1 && par21 != 2))
        {
            if (par1 == 0)
            {
                return this.field_94404_cP;
            }
            else
            {
                if (par21 < 0 || par21 >= this.field_94406_c.length)
                {
                    par21 = 0;
                }

                return this.field_94406_c[par21];
            }
        }
        else
        {
            return this.field_94403_cO;
        }
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
        this.field_94406_c = new Icon[field_94405_b.length];

        for (int i = 0; i < this.field_94406_c.length; ++i)
        {
            this.field_94406_c[i] = par1IconRegister.registerIcon(this.getTextureName() + "_" + field_94405_b[i]);
        }

        this.field_94403_cO = par1IconRegister.registerIcon(this.getTextureName() + "_top");
        this.field_94404_cP = par1IconRegister.registerIcon(this.getTextureName() + "_bottom");
    }
}
