package net.minecraft.block;

import net.minecraft.block.material.Material;

public abstract class BlockDirectional extends Block
{
    protected BlockDirectional(final int par1, final Material par2Material)
    {
        super(par1, par2Material);
    }

    /**
     * Returns the orentation value from the specified metadata
     */
    public static int getDirection(final int par0)
    {
        return par0 & 3;
    }
}
