package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;

import java.util.Random;

public class BlockStationary extends BlockFluid
{
    protected BlockStationary(final int par1, final Material par2Material)
    {
        super(par1, par2Material);
        this.setTickRandomly(false);

        if (par2Material == Material.lava)
        {
            this.setTickRandomly(true);
        }
    }

    public boolean getBlocksMovement(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        return this.blockMaterial != Material.lava;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);

        if (par1World.getBlockId(par2, par3, par4) == this.blockID)
        {
            this.setNotStationary(par1World, par2, par3, par4);
        }
    }

    /**
     * Changes the block ID to that of an updating fluid.
     */
    private void setNotStationary(final World par1World, final int par2, final int par3, final int par4)
    {
        final int l = par1World.getBlockMetadata(par2, par3, par4);
        par1World.setBlock(par2, par3, par4, this.blockID - 1, l, 2);
        par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID - 1, this.tickRate(par1World));
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, int par2, int par3, int par4, final Random par5Random)
    {
        int par31 = par3;
        int par21 = par2;
        int par41 = par4;
        if (this.blockMaterial == Material.lava)
        {
            final int l = par5Random.nextInt(3);
            int i1;
            int j1;
            // CraftBukkit start - Prevent lava putting something on fire, remember igniter block coords
            final int x = par21;
            final int y = par31;
            final int z = par41;
            // CraftBukkit end

            for (i1 = 0; i1 < l; ++i1)
            {
                par21 += par5Random.nextInt(3) - 1;
                ++par31;
                par41 += par5Random.nextInt(3) - 1;
                j1 = par1World.getBlockId(par21, par31, par41);

                if (j1 == 0)
                {
                    if (this.isFlammable(par1World, par21 - 1, par31, par41) || this.isFlammable(par1World, par21 + 1, par31, par41) || this.isFlammable(par1World, par21, par31, par41 - 1) || this.isFlammable(par1World, par21, par31, par41 + 1) || this.isFlammable(par1World, par21, par31 - 1, par41) || this.isFlammable(par1World, par21, par31 + 1, par41))
                    {
                        // CraftBukkit start - Prevent lava putting something on fire
                        if (par1World.getBlockId(par21, par31, par41) != Block.fire.blockID)
                        {
                            if (CraftEventFactory.callBlockIgniteEvent(par1World, par21, par31, par41, x, y, z).isCancelled())
                            {
                                continue;
                            }
                        }
                        // CraftBukkit end
                        par1World.setBlock(par21, par31, par41, Block.fire.blockID);
                        return;
                    }
                }
                else if (Block.blocksList[j1].blockMaterial.blocksMovement())
                {
                    return;
                }
            }

            if (l == 0)
            {
                i1 = par21;
                j1 = par41;

                for (int k1 = 0; k1 < 3; ++k1)
                {
                    par21 = i1 + par5Random.nextInt(3) - 1;
                    par41 = j1 + par5Random.nextInt(3) - 1;

                    if (par1World.isAirBlock(par21, par31 + 1, par41) && this.isFlammable(par1World, par21, par31, par41))
                    {
                        // CraftBukkit start - Prevent lava putting something on fire
                        if (par1World.getBlockId(par21, par31 + 1, par41) != Block.fire.blockID)
                        {
                            if (CraftEventFactory.callBlockIgniteEvent(par1World, par21, par31 + 1, par41, x, y, z).isCancelled())
                            {
                                continue;
                            }
                        }
                        // CraftBukkit end
                        par1World.setBlock(par21, par31 + 1, par41, Block.fire.blockID);
                    }
                }
            }
        }
    }

    /**
     * Checks to see if the block is flammable.
     */
    private boolean isFlammable(final World par1World, final int par2, final int par3, final int par4)
    {
        return par1World.getBlockMaterial(par2, par3, par4).getCanBurn();
    }
}
