package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockWorkbench extends Block
{
    @SideOnly(Side.CLIENT)
    private Icon workbenchIconTop;
    @SideOnly(Side.CLIENT)
    private Icon workbenchIconFront;

    protected BlockWorkbench(final int par1)
    {
        super(par1, Material.wood);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return par1 == 1 ? this.workbenchIconTop : (par1 == 0 ? Block.planks.getBlockTextureFromSide(par1) : (par1 != 2 && par1 != 4 ? this.blockIcon : this.workbenchIconFront));
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
        this.workbenchIconTop = par1IconRegister.registerIcon(this.getTextureName() + "_top");
        this.workbenchIconFront = par1IconRegister.registerIcon(this.getTextureName() + "_front");
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9)
    {
        if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            par5EntityPlayer.displayGUIWorkbench(par2, par3, par4);
            return true;
        }
    }
}
