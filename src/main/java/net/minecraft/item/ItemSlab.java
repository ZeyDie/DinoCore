package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock
{
    private final boolean isFullBlock;

    /** Instance of BlockHalfSlab. */
    private final BlockHalfSlab theHalfSlab;

    /** The double-slab block corresponding to this item. */
    private final BlockHalfSlab doubleSlab;

    public ItemSlab(final int par1, final BlockHalfSlab par2BlockHalfSlab, final BlockHalfSlab par3BlockHalfSlab, final boolean par4)
    {
        super(par1);
        this.theHalfSlab = par2BlockHalfSlab;
        this.doubleSlab = par3BlockHalfSlab;
        this.isFullBlock = par4;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets an icon index based on an item's damage value
     */
    public Icon getIconFromDamage(final int par1)
    {
        return Block.blocksList[this.itemID].getIcon(2, par1);
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
        return this.theHalfSlab.getFullSlabName(par1ItemStack.getItemDamage());
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8, final float par9, final float par10)
    {
        if (this.isFullBlock)
        {
            return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
        }
        else if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
        {
            return false;
        }
        else
        {
            final int i1 = par3World.getBlockId(par4, par5, par6);
            final int j1 = par3World.getBlockMetadata(par4, par5, par6);
            final int k1 = j1 & 7;
            final boolean flag = (j1 & 8) != 0;

            if ((par7 == 1 && !flag || par7 == 0 && flag) && i1 == this.theHalfSlab.blockID && k1 == par1ItemStack.getItemDamage())
            {
                if (par3World.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBoxFromPool(par3World, par4, par5, par6)) && par3World.setBlock(par4, par5, par6, this.doubleSlab.blockID, k1, 3))
                {
                    par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.doubleSlab.stepSound.getPlaceSound(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getPitch() * 0.8F);
                    --par1ItemStack.stackSize;
                }

                return true;
            }
            else
            {
                return this.func_77888_a(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7) ? true : super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given ItemBlock can be placed on the given side of the given block position.
     */
    public boolean canPlaceItemBlockOnSide(final World par1World, int par2, int par3, int par4, final int par5, final EntityPlayer par6EntityPlayer, final ItemStack par7ItemStack)
    {
        int par31 = par3;
        int par41 = par4;
        int par21 = par2;
        final int i1 = par21;
        final int j1 = par31;
        final int k1 = par41;
        int l1 = par1World.getBlockId(par21, par31, par41);
        int i2 = par1World.getBlockMetadata(par21, par31, par41);
        int j2 = i2 & 7;
        boolean flag = (i2 & 8) != 0;

        if ((par5 == 1 && !flag || par5 == 0 && flag) && l1 == this.theHalfSlab.blockID && j2 == par7ItemStack.getItemDamage())
        {
            return true;
        }
        else
        {
            if (par5 == 0)
            {
                --par31;
            }

            if (par5 == 1)
            {
                ++par31;
            }

            if (par5 == 2)
            {
                --par41;
            }

            if (par5 == 3)
            {
                ++par41;
            }

            if (par5 == 4)
            {
                --par21;
            }

            if (par5 == 5)
            {
                ++par21;
            }

            l1 = par1World.getBlockId(par21, par31, par41);
            i2 = par1World.getBlockMetadata(par21, par31, par41);
            j2 = i2 & 7;
            flag = (i2 & 8) != 0;
            return l1 == this.theHalfSlab.blockID && j2 == par7ItemStack.getItemDamage() ? true : super.canPlaceItemBlockOnSide(par1World, i1, j1, k1, par5, par6EntityPlayer, par7ItemStack);
        }
    }

    private boolean func_77888_a(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, int par4, int par5, int par6, final int par7)
    {
        int par51 = par5;
        int par61 = par6;
        int par41 = par4;
        if (par7 == 0)
        {
            --par51;
        }

        if (par7 == 1)
        {
            ++par51;
        }

        if (par7 == 2)
        {
            --par61;
        }

        if (par7 == 3)
        {
            ++par61;
        }

        if (par7 == 4)
        {
            --par41;
        }

        if (par7 == 5)
        {
            ++par41;
        }

        final int i1 = par3World.getBlockId(par41, par51, par61);
        final int j1 = par3World.getBlockMetadata(par41, par51, par61);
        final int k1 = j1 & 7;

        if (i1 == this.theHalfSlab.blockID && k1 == par1ItemStack.getItemDamage())
        {
            if (par3World.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBoxFromPool(par3World, par41, par51, par61)) && par3World.setBlock(par41, par51, par61, this.doubleSlab.blockID, k1, 3))
            {
                par3World.playSoundEffect((double)((float) par41 + 0.5F), (double)((float) par51 + 0.5F), (double)((float) par61 + 0.5F), this.doubleSlab.stepSound.getPlaceSound(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getPitch() * 0.8F);
                --par1ItemStack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
