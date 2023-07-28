package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class BlockEnderChest extends BlockContainer
{
    protected BlockEnderChest(final int par1)
    {
        super(par1, Material.rock);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 22;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Block.obsidian.blockID;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random par1Random)
    {
        return 8;
    }

    /**
     * Return true if a player with Silk Touch can harvest this block directly, and not its normal drops.
     */
    protected boolean canSilkHarvest()
    {
        return true;
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(final World par1World, final int par2, final int par3, final int par4, final EntityLivingBase par5EntityLivingBase, final ItemStack par6ItemStack)
    {
        byte b0 = 0;
        final int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
        {
            b0 = 2;
        }

        if (l == 1)
        {
            b0 = 5;
        }

        if (l == 2)
        {
            b0 = 3;
        }

        if (l == 3)
        {
            b0 = 4;
        }

        par1World.setBlockMetadataWithNotify(par2, par3, par4, b0, 2);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9)
    {
        final InventoryEnderChest inventoryenderchest = par5EntityPlayer.getInventoryEnderChest();
        final TileEntityEnderChest tileentityenderchest = (TileEntityEnderChest)par1World.getBlockTileEntity(par2, par3, par4);

        if (inventoryenderchest != null && tileentityenderchest != null)
        {
            if (par1World.isBlockNormalCube(par2, par3 + 1, par4))
            {
                return true;
            }
            else if (par1World.isRemote)
            {
                return true;
            }
            else
            {
                inventoryenderchest.setAssociatedChest(tileentityenderchest);
                par5EntityPlayer.displayGUIChest(inventoryenderchest);
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World par1World)
    {
        return new TileEntityEnderChest();
    }

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        for (int l = 0; l < 3; ++l)
        {
            double d0 = (double)((float)par2 + par5Random.nextFloat());
            final double d1 = (double)((float)par3 + par5Random.nextFloat());
            d0 = (double)((float)par4 + par5Random.nextFloat());
            double d2 = 0.0D;
            double d3 = 0.0D;
            double d4 = 0.0D;
            final int i1 = par5Random.nextInt(2) * 2 - 1;
            final int j1 = par5Random.nextInt(2) * 2 - 1;
            d2 = ((double)par5Random.nextFloat() - 0.5D) * 0.125D;
            d3 = ((double)par5Random.nextFloat() - 0.5D) * 0.125D;
            d4 = ((double)par5Random.nextFloat() - 0.5D) * 0.125D;
            final double d5 = (double)par4 + 0.5D + 0.25D * (double)j1;
            d4 = (double)(par5Random.nextFloat() * 1.0F * (float)j1);
            final double d6 = (double)par2 + 0.5D + 0.25D * (double)i1;
            d2 = (double)(par5Random.nextFloat() * 1.0F * (float)i1);
            par1World.spawnParticle("portal", d6, d1, d5, d2, d3, d4);
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("obsidian");
    }
}
