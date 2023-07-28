package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Iterator;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate
{
    /** The maximum number of items the plate weights. */
    private final int maxItemsWeighted;

    protected BlockPressurePlateWeighted(final int par1, final String par2Str, final Material par3Material, final int par4)
    {
        super(par1, par2Str, par3Material);
        this.maxItemsWeighted = par4;
    }

    /**
     * Returns the current state of the pressure plate. Returns a value between 0 and 15 based on the number of items on
     * it.
     */
    protected int getPlateState(final World par1World, final int par2, final int par3, final int par4)
    {
        int l = 0;
        final Iterator iterator = par1World.getEntitiesWithinAABB(EntityItem.class, this.getSensitiveAABB(par2, par3, par4)).iterator();

        while (iterator.hasNext())
        {
            final EntityItem entityitem = (EntityItem)iterator.next();
            l += entityitem.getEntityItem().stackSize;

            if (l >= this.maxItemsWeighted)
            {
                break;
            }
        }

        if (l <= 0)
        {
            return 0;
        }
        else
        {
            final float f = (float)Math.min(this.maxItemsWeighted, l) / (float)this.maxItemsWeighted;
            return MathHelper.ceiling_float_int(f * 15.0F);
        }
    }

    /**
     * Argument is metadata. Returns power level (0-15)
     */
    protected int getPowerSupply(final int par1)
    {
        return par1;
    }

    /**
     * Argument is weight (0-15). Return the metadata to be set because of it.
     */
    protected int getMetaFromWeight(final int par1)
    {
        return par1;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World par1World)
    {
        return 10;
    }
}
