package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;

public class ItemSeeds extends Item implements IPlantable
{
    /**
     * The type of block this seed turns into (wheat or pumpkin stems for instance)
     */
    private int blockType;

    /** BlockID of the block the seeds can be planted on. */
    private int soilBlockID;

    public ItemSeeds(final int par1, final int par2, final int par3)
    {
        super(par1);
        this.blockType = par2;
        this.soilBlockID = par3;
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8, final float par9, final float par10)
    {
        if (par7 != 1)
        {
            return false;
        }
        else if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack))
        {
            final int i1 = par3World.getBlockId(par4, par5, par6);
            final Block soil = Block.blocksList[i1];

            if (soil != null && soil.canSustainPlant(par3World, par4, par5, par6, ForgeDirection.UP, this) && par3World.isAirBlock(par4, par5 + 1, par6))
            {
                par3World.setBlock(par4, par5 + 1, par6, this.blockType);
                --par1ItemStack.stackSize;
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public EnumPlantType getPlantType(final World world, final int x, final int y, final int z)
    {
        return (blockType == Block.netherStalk.blockID ? EnumPlantType.Nether : EnumPlantType.Crop);
    }

    @Override
    public int getPlantID(final World world, final int x, final int y, final int z)
    {
        return blockType;
    }

    @Override
    public int getPlantMetadata(final World world, final int x, final int y, final int z)
    {
        return 0;
    }
}
