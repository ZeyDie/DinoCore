package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;

import java.util.List;

public class ItemCoal extends Item
{
    @SideOnly(Side.CLIENT)
    private Icon field_111220_a;

    public ItemCoal(final int par1)
    {
        super(par1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(final ItemStack par1ItemStack)
    {
        return par1ItemStack.getItemDamage() == 1 ? "item.charcoal" : "item.coal";
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(final int par1, final CreativeTabs par2CreativeTabs, final List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets an icon index based on an item's damage value
     */
    public Icon getIconFromDamage(final int par1)
    {
        return par1 == 1 ? this.field_111220_a : super.getIconFromDamage(par1);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IconRegister par1IconRegister)
    {
        super.registerIcons(par1IconRegister);
        this.field_111220_a = par1IconRegister.registerIcon("charcoal");
    }
}
