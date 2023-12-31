package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import java.util.Random;

public class BlockMushroomCap extends Block
{
    private static final String[] field_94429_a = {"skin_brown", "skin_red"};

    /** The mushroom type. 0 for brown, 1 for red. */
    private final int mushroomType;
    @SideOnly(Side.CLIENT)
    private Icon[] iconArray;
    @SideOnly(Side.CLIENT)
    private Icon field_94426_cO;
    @SideOnly(Side.CLIENT)
    private Icon field_94427_cP;

    public BlockMushroomCap(final int par1, final Material par2Material, final int par3)
    {
        super(par1, par2Material);
        this.mushroomType = par3;
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return par2 == 10 && par1 > 1 ? this.field_94426_cO : (par2 >= 1 && par2 <= 9 && par1 == 1 ? this.iconArray[this.mushroomType] : (par2 >= 1 && par2 <= 3 && par1 == 2 ? this.iconArray[this.mushroomType] : (par2 >= 7 && par2 <= 9 && par1 == 3 ? this.iconArray[this.mushroomType] : ((par2 == 1 || par2 == 4 || par2 == 7) && par1 == 4 ? this.iconArray[this.mushroomType] : ((par2 == 3 || par2 == 6 || par2 == 9) && par1 == 5 ? this.iconArray[this.mushroomType] : (par2 == 14 ? this.iconArray[this.mushroomType] : (par2 == 15 ? this.field_94426_cO : this.field_94427_cP)))))));
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random par1Random)
    {
        int i = par1Random.nextInt(10) - 7;

        if (i < 0)
        {
            i = 0;
        }

        return i;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Block.mushroomBrown.blockID + this.mushroomType;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return Block.mushroomBrown.blockID + this.mushroomType;
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[field_94429_a.length];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = par1IconRegister.registerIcon(this.getTextureName() + "_" + field_94429_a[i]);
        }

        this.field_94427_cP = par1IconRegister.registerIcon(this.getTextureName() + "_" + "inside");
        this.field_94426_cO = par1IconRegister.registerIcon(this.getTextureName() + "_" + "skin_stem");
    }
}
