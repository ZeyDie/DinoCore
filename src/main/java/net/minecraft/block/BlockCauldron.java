package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockCauldron extends Block
{
    @SideOnly(Side.CLIENT)
    private Icon field_94378_a;
    @SideOnly(Side.CLIENT)
    private Icon cauldronTopIcon;
    @SideOnly(Side.CLIENT)
    private Icon cauldronBottomIcon;

    public BlockCauldron(final int par1)
    {
        super(par1, Material.iron);
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return par1 == 1 ? this.cauldronTopIcon : (par1 == 0 ? this.cauldronBottomIcon : this.blockIcon);
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.field_94378_a = par1IconRegister.registerIcon(this.getTextureName() + "_" + "inner");
        this.cauldronTopIcon = par1IconRegister.registerIcon(this.getTextureName() + "_top");
        this.cauldronBottomIcon = par1IconRegister.registerIcon(this.getTextureName() + "_" + "bottom");
        this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    public void addCollisionBoxesToList(final World par1World, final int par2, final int par3, final int par4, final AxisAlignedBB par5AxisAlignedBB, final List par6List, final Entity par7Entity)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        final float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBoundsForItemRender();
    }

    @SideOnly(Side.CLIENT)
    public static Icon getCauldronIcon(final String par0Str)
    {
        return par0Str.equals("inner") ? Block.cauldron.field_94378_a : (par0Str.equals("bottom") ? Block.cauldron.cauldronBottomIcon : null);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 24;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
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
            final ItemStack itemstack = par5EntityPlayer.inventory.getCurrentItem();

            if (itemstack == null)
            {
                return true;
            }
            else
            {
                final int i1 = par1World.getBlockMetadata(par2, par3, par4);
                final int j1 = func_111045_h_(i1);

                if (itemstack.itemID == Item.bucketWater.itemID)
                {
                    if (j1 < 3)
                    {
                        if (!par5EntityPlayer.capabilities.isCreativeMode)
                        {
                            par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, new ItemStack(Item.bucketEmpty));
                        }

                        par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
                        par1World.func_96440_m(par2, par3, par4, this.blockID);
                    }

                    return true;
                }
                else
                {
                    if (itemstack.itemID == Item.glassBottle.itemID)
                    {
                        if (j1 > 0)
                        {
                            final ItemStack itemstack1 = new ItemStack(Item.potion, 1, 0);

                            if (!par5EntityPlayer.inventory.addItemStackToInventory(itemstack1))
                            {
                                par1World.spawnEntityInWorld(new EntityItem(par1World, (double)par2 + 0.5D, (double)par3 + 1.5D, (double)par4 + 0.5D, itemstack1));
                            }
                            else if (par5EntityPlayer instanceof EntityPlayerMP)
                            {
                                ((EntityPlayerMP)par5EntityPlayer).sendContainerToPlayer(par5EntityPlayer.inventoryContainer);
                            }

                            --itemstack.stackSize;

                            if (itemstack.stackSize <= 0)
                            {
                                par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, (ItemStack)null);
                            }

                            par1World.setBlockMetadataWithNotify(par2, par3, par4, j1 - 1, 2);
                            par1World.func_96440_m(par2, par3, par4, this.blockID);
                        }
                    }
                    else if (j1 > 0 && itemstack.getItem() instanceof ItemArmor && ((ItemArmor)itemstack.getItem()).getArmorMaterial() == EnumArmorMaterial.CLOTH)
                    {
                        final ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
                        itemarmor.removeColor(itemstack);
                        par1World.setBlockMetadataWithNotify(par2, par3, par4, j1 - 1, 2);
                        par1World.func_96440_m(par2, par3, par4, this.blockID);
                        return true;
                    }

                    return true;
                }
            }
        }
    }

    /**
     * currently only used by BlockCauldron to incrament meta-data during rain
     */
    public void fillWithRain(final World par1World, final int par2, final int par3, final int par4)
    {
        if (par1World.rand.nextInt(20) == 1)
        {
            final int l = par1World.getBlockMetadata(par2, par3, par4);

            if (l < 3)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, l + 1, 2);
            }
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Item.cauldron.itemID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return Item.cauldron.itemID;
    }

    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    public int getComparatorInputOverride(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        final int i1 = par1World.getBlockMetadata(par2, par3, par4);
        return func_111045_h_(i1);
    }

    public static int func_111045_h_(final int par0)
    {
        return par0;
    }
}
