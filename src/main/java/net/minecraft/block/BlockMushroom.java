package net.minecraft.block;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraftforge.common.ForgeDirection;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockSpreadEvent;

import java.util.Random;

// CraftBukkit start
// CraftBukkit end

public class BlockMushroom extends BlockFlower
{
    protected BlockMushroom(final int par1)
    {
        super(par1);
        final float f = 0.2F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setTickRandomly(true);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, int par2, int par3, int par4, final Random par5Random)
    {
        int par21 = par2;
        int par31 = par3;
        int par41 = par4;
        final int sourceX = par21, sourceY = par31, sourceZ = par41; // CraftBukkit

        if (par5Random.nextInt(Math.max(1, (int) par1World.growthOdds / par1World.spigotConfig.mushroomModifier * 25)) == 0) // Spigot
        {
            final byte b0 = 4;
            int l = 5;
            int i1;
            int j1;
            int k1;

            for (i1 = par21 - b0; i1 <= par21 + b0; ++i1)
            {
                for (j1 = par41 - b0; j1 <= par41 + b0; ++j1)
                {
                    for (k1 = par31 - 1; k1 <= par31 + 1; ++k1)
                    {
                        if (par1World.getBlockId(i1, k1, j1) == this.blockID)
                        {
                            --l;

                            if (l <= 0)
                            {
                                return;
                            }
                        }
                    }
                }
            }

            i1 = par21 + par5Random.nextInt(3) - 1;
            j1 = par31 + par5Random.nextInt(2) - par5Random.nextInt(2);
            k1 = par41 + par5Random.nextInt(3) - 1;

            for (int l1 = 0; l1 < 4; ++l1)
            {
                if (par1World.isAirBlock(i1, j1, k1) && this.canBlockStay(par1World, i1, j1, k1))
                {
                    par21 = i1;
                    par31 = j1;
                    par41 = k1;
                }

                i1 = par21 + par5Random.nextInt(3) - 1;
                j1 = par31 + par5Random.nextInt(2) - par5Random.nextInt(2);
                k1 = par41 + par5Random.nextInt(3) - 1;
            }

            if (par1World.isAirBlock(i1, j1, k1) && this.canBlockStay(par1World, i1, j1, k1))
            {
                // CraftBukkit start
                final org.bukkit.World bworld = par1World.getWorld();
                final BlockState blockState = bworld.getBlockAt(i1, j1, k1).getState();
                blockState.setTypeId(this.blockID);
                final BlockSpreadEvent event = new BlockSpreadEvent(blockState.getBlock(), bworld.getBlockAt(sourceX, sourceY, sourceZ), blockState);
                par1World.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled())
                {
                    blockState.update(true);
                }
                // CraftBukkit end
            }
        }
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4)
    {
        return super.canPlaceBlockAt(par1World, par2, par3, par4) && this.canBlockStay(par1World, par2, par3, par4);
    }

    /**
     * Gets passed in the blockID of the block below and supposed to return true if its allowed to grow on the type of
     * blockID passed in. Args: blockID
     */
    protected boolean canThisPlantGrowOnThisBlockID(final int par1)
    {
        return Block.opaqueCubeLookup[par1];
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(final World par1World, final int par2, final int par3, final int par4)
    {
        if (par3 >= 0 && par3 < 256)
        {
            final int l = par1World.getBlockId(par2, par3 - 1, par4);
            final Block soil = Block.blocksList[l];
            return (l == Block.mycelium.blockID || par1World.getFullBlockLightValue(par2, par3, par4) < 13) &&
                   (soil != null && soil.canSustainPlant(par1World, par2, par3 - 1, par4, ForgeDirection.UP, this));
        }
        else
        {
            return false;
        }
    } 

    /**
     * Fertilize the mushroom.
     */
    public boolean fertilizeMushroom(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        final int l = par1World.getBlockMetadata(par2, par3, par4);
        par1World.setBlockToAir(par2, par3, par4);
        WorldGenBigMushroom worldgenbigmushroom = null;

        if (this.blockID == Block.mushroomBrown.blockID) {
            BlockSapling.treeType = TreeType.BROWN_MUSHROOM; // CraftBukkit
            worldgenbigmushroom = new WorldGenBigMushroom(0);
        } else if (this.blockID == Block.mushroomRed.blockID) {
            BlockSapling.treeType = TreeType.RED_MUSHROOM; // CraftBukkit
            worldgenbigmushroom = new WorldGenBigMushroom(1);
        }

        if (worldgenbigmushroom != null && worldgenbigmushroom.generate(par1World, par5Random, par2, par3, par4)) 
        {
            return true;
        } 
        else
        {
            par1World.setBlock(par2, par3, par4, this.blockID, l, 3);
            return false;
        }     
    }
}
