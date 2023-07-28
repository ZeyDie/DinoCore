package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import java.util.List;

public class ItemBlock extends Item
{
    /** The block ID of the Block associated with this ItemBlock */
    private int blockID;
    @SideOnly(Side.CLIENT)
    private Icon field_94588_b;

    public ItemBlock(final int par1)
    {
        super(par1);
        this.blockID = par1 + 256;
    }

    /**
     * Returns the blockID for this Item
     */
    public int getBlockID()
    {
        return this.blockID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns 0 for /terrain.png, 1 for /gui/items.png
     */
    public int getSpriteNumber()
    {
        return Block.blocksList[this.blockID].getItemIconName() != null ? 1 : 0;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets an icon index based on an item's damage value
     */
    public Icon getIconFromDamage(final int par1)
    {
        return this.field_94588_b != null ? this.field_94588_b : Block.blocksList[this.blockID].getBlockTextureFromSide(1);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, int par4, int par5, int par6, int par7, final float par8, final float par9, final float par10)
    {
        int par71 = par7;
        int par51 = par5;
        int par61 = par6;
        int par41 = par4;
        final int i1 = par3World.getBlockId(par41, par51, par61);

        if (i1 == Block.snow.blockID && (par3World.getBlockMetadata(par41, par51, par61) & 7) < 1)
        {
            par71 = 1;
        }
        else if (i1 != Block.vine.blockID && i1 != Block.tallGrass.blockID && i1 != Block.deadBush.blockID
                && (Block.blocksList[i1] == null || !Block.blocksList[i1].isBlockReplaceable(par3World, par41, par51, par61)))
        {
            if (par71 == 0)
            {
                --par51;
            }

            if (par71 == 1)
            {
                ++par51;
            }

            if (par71 == 2)
            {
                --par61;
            }

            if (par71 == 3)
            {
                ++par61;
            }

            if (par71 == 4)
            {
                --par41;
            }

            if (par71 == 5)
            {
                ++par41;
            }
        }

        if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else if (!par2EntityPlayer.canPlayerEdit(par41, par51, par61, par71, par1ItemStack))
        {
            return false;
        }
        else if (par51 == 255 && Block.blocksList[this.blockID].blockMaterial.isSolid())
        {
            return false;
        }
        else if (par3World.canPlaceEntityOnSide(this.blockID, par41, par51, par61, false, par71, par2EntityPlayer, par1ItemStack))
        {
            final Block block = Block.blocksList[this.blockID];
            final int j1 = this.getMetadata(par1ItemStack.getItemDamage());
            final int k1 = Block.blocksList[this.blockID].onBlockPlaced(par3World, par41, par51, par61, par71, par8, par9, par10, j1);

            if (placeBlockAt(par1ItemStack, par2EntityPlayer, par3World, par41, par51, par61, par71, par8, par9, par10, k1))
            {
                par3World.playSoundEffect((double)((float) par41 + 0.5F), (double)((float) par51 + 0.5F), (double)((float) par61 + 0.5F), block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                --par1ItemStack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given ItemBlock can be placed on the given side of the given block position.
     */
    public boolean canPlaceItemBlockOnSide(final World par1World, int par2, int par3, int par4, int par5, final EntityPlayer par6EntityPlayer, final ItemStack par7ItemStack)
    {
        int par51 = par5;
        int par31 = par3;
        int par41 = par4;
        int par21 = par2;
        final int i1 = par1World.getBlockId(par21, par31, par41);

        if (i1 == Block.snow.blockID)
        {
            par51 = 1;
        }
        else if (i1 != Block.vine.blockID && i1 != Block.tallGrass.blockID && i1 != Block.deadBush.blockID
                && (Block.blocksList[i1] == null || !Block.blocksList[i1].isBlockReplaceable(par1World, par21, par31, par41)))
        {
            if (par51 == 0)
            {
                --par31;
            }

            if (par51 == 1)
            {
                ++par31;
            }

            if (par51 == 2)
            {
                --par41;
            }

            if (par51 == 3)
            {
                ++par41;
            }

            if (par51 == 4)
            {
                --par21;
            }

            if (par51 == 5)
            {
                ++par21;
            }
        }

        return par1World.canPlaceEntityOnSide(this.getBlockID(), par21, par31, par41, false, par51, (Entity)null, par7ItemStack);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(final ItemStack par1ItemStack)
    {
        return Block.blocksList[this.blockID].getUnlocalizedName();
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getUnlocalizedName()
    {
        return Block.blocksList[this.blockID].getUnlocalizedName();
    }

    @SideOnly(Side.CLIENT)

    /**
     * gets the CreativeTab this item is displayed on
     */
    public CreativeTabs getCreativeTab()
    {
        return Block.blocksList[this.blockID].getCreativeTabToDisplayOn();
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(final int par1, final CreativeTabs par2CreativeTabs, final List par3List)
    {
        Block.blocksList[this.blockID].getSubBlocks(par1, par2CreativeTabs, par3List);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IconRegister par1IconRegister)
    {
        final String s = Block.blocksList[this.blockID].getItemIconName();

        if (s != null)
        {
            this.field_94588_b = par1IconRegister.registerIcon(s);
        }
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(final ItemStack stack, final EntityPlayer player, final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ, final int metadata)
    {
       if (!world.setBlock(x, y, z, this.blockID, metadata, 3))
       {
           return false;
       }

       if (world.getBlockId(x, y, z) == this.blockID)
       {
           Block.blocksList[this.blockID].onBlockPlacedBy(world, x, y, z, player, stack);
           Block.blocksList[this.blockID].onPostBlockPlaced(world, x, y, z, metadata);
       }

       return true;
    }
}
