package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemSkull extends Item
{
    private static final String[] skullTypes = {"skeleton", "wither", "zombie", "char", "creeper"};
    public static final String[] field_94587_a = {"skeleton", "wither", "zombie", "steve", "creeper"};
    @SideOnly(Side.CLIENT)
    private Icon[] field_94586_c;

    public ItemSkull(final int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, int par4, int par5, int par6, final int par7, final float par8, final float par9, final float par10)
    {
        int par51 = par5;
        int par61 = par6;
        int par41 = par4;
        if (par7 == 0)
        {
            return false;
        }
        else if (!par3World.getBlockMaterial(par41, par51, par61).isSolid())
        {
            return false;
        }
        else
        {
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

            if (!par2EntityPlayer.canPlayerEdit(par41, par51, par61, par7, par1ItemStack))
            {
                return false;
            }
            else if (!Block.skull.canPlaceBlockAt(par3World, par41, par51, par61))
            {
                return false;
            }
            else
            {
                par3World.setBlock(par41, par51, par61, Block.skull.blockID, par7, 2);
                int i1 = 0;

                if (par7 == 1)
                {
                    i1 = MathHelper.floor_double((double)(par2EntityPlayer.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
                }

                final TileEntity tileentity = par3World.getBlockTileEntity(par41, par51, par61);

                if (tileentity != null && tileentity instanceof TileEntitySkull)
                {
                    String s = "";

                    if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("SkullOwner"))
                    {
                        s = par1ItemStack.getTagCompound().getString("SkullOwner");
                    }

                    ((TileEntitySkull)tileentity).setSkullType(par1ItemStack.getItemDamage(), s);
                    ((TileEntitySkull)tileentity).setSkullRotation(i1);
                    ((BlockSkull)Block.skull).makeWither(par3World, par41, par51, par61, (TileEntitySkull)tileentity);
                }

                --par1ItemStack.stackSize;
                return true;
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(final int par1, final CreativeTabs par2CreativeTabs, final List par3List)
    {
        for (int j = 0; j < skullTypes.length; ++j)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    public int getMetadata(final int par1)
    {
        return par1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets an icon index based on an item's damage value
     */
    public Icon getIconFromDamage(int par1)
    {
        int par11 = par1;
        if (par11 < 0 || par11 >= skullTypes.length)
        {
            par11 = 0;
        }

        return this.field_94586_c[par11];
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(final ItemStack par1ItemStack)
    {
        int i = par1ItemStack.getItemDamage();

        if (i < 0 || i >= skullTypes.length)
        {
            i = 0;
        }

        return super.getUnlocalizedName() + "." + skullTypes[i];
    }

    public String getItemDisplayName(final ItemStack par1ItemStack)
    {
        return par1ItemStack.getItemDamage() == 3 && par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("SkullOwner") ? StatCollector.translateToLocalFormatted("item.skull.player.name", new Object[] {par1ItemStack.getTagCompound().getString("SkullOwner")}): super.getItemDisplayName(par1ItemStack);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.field_94586_c = new Icon[field_94587_a.length];

        for (int i = 0; i < field_94587_a.length; ++i)
        {
            this.field_94586_c[i] = par1IconRegister.registerIcon(this.getIconString() + "_" + field_94587_a[i]);
        }
    }
}
