package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import java.util.ArrayList;
import java.util.Random;

public class BlockNetherStalk extends BlockFlower
{
    @SideOnly(Side.CLIENT)
    private Icon[] iconArray;
    private static final String[] field_94373_a = {"netherStalk_0", "netherStalk_1", "netherStalk_2"};

    protected BlockNetherStalk(final int par1)
    {
        super(par1);
        this.setTickRandomly(true);
        final float f = 0.5F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab((CreativeTabs)null);
    }

    /**
     * Gets passed in the blockID of the block below and supposed to return true if its allowed to grow on the type of
     * blockID passed in. Args: blockID
     */
    protected boolean canThisPlantGrowOnThisBlockID(final int par1)
    {
        return par1 == Block.slowSand.blockID;
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(final World par1World, final int par2, final int par3, final int par4)
    {
        final Block block = Block.blocksList[par1World.getBlockId(par2, par3 - 1, par4)];
        return (block != null && block.canSustainPlant(par1World, par2, par3 - 1, par4, ForgeDirection.UP, this));
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        int l = par1World.getBlockMetadata(par2, par3, par4);

        if (l < 3 && par5Random.nextInt(10) == 0)
        {
            org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.handleBlockGrowEvent(par1World, par2, par3, par4, this.blockID, ++l); // CraftBukkit
        }

        super.updateTick(par1World, par2, par3, par4, par5Random);
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return par2 >= 3 ? this.iconArray[2] : (par2 > 0 ? this.iconArray[1] : this.iconArray[0]);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 6;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final int par7)
    {
        super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, par7);
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return 0;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random par1Random)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return Item.netherStalkSeeds.itemID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[3];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = par1IconRegister.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(final World world, final int x, final int y, final int z, final int metadata, final int fortune)
    {
        final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        int count = 1;

        if (metadata >= 3)
        {
            count = 2 + world.rand.nextInt(3) + (fortune > 0 ? world.rand.nextInt(fortune + 1) : 0);
        }

        for (int i = 0; i < count; i++)
        {
            ret.add(new ItemStack(Item.netherStalkSeeds));
        }

        return ret;
    }
}
