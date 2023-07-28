package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGeneratorBonusChest extends WorldGenerator
{
    /**
     * Instance of WeightedRandomChestContent what will randomly generate items into the Bonus Chest.
     */
    private final WeightedRandomChestContent[] theBonusChestGenerator;

    /**
     * Value of this int will determine how much items gonna generate in Bonus Chest.
     */
    private final int itemsToGenerateInBonusChest;

    public WorldGeneratorBonusChest(final WeightedRandomChestContent[] par1ArrayOfWeightedRandomChestContent, final int par2)
    {
        this.theBonusChestGenerator = par1ArrayOfWeightedRandomChestContent;
        this.itemsToGenerateInBonusChest = par2;
    }

    public boolean generate(final World par1World, final Random par2Random, final int par3, int par4, final int par5)
    {
        int par41 = par4;
        int l;

        for (final boolean flag = false; ((l = par1World.getBlockId(par3, par41, par5)) == 0 || l == Block.leaves.blockID) && par41 > 1; --par41)
        {
            ;
        }

        if (par41 < 1)
        {
            return false;
        }
        else
        {
            ++par41;

            for (int i1 = 0; i1 < 4; ++i1)
            {
                final int j1 = par3 + par2Random.nextInt(4) - par2Random.nextInt(4);
                final int k1 = par41 + par2Random.nextInt(3) - par2Random.nextInt(3);
                final int l1 = par5 + par2Random.nextInt(4) - par2Random.nextInt(4);

                if (par1World.isAirBlock(j1, k1, l1) && par1World.doesBlockHaveSolidTopSurface(j1, k1 - 1, l1))
                {
                    par1World.setBlock(j1, k1, l1, Block.chest.blockID, 0, 2);
                    final TileEntityChest tileentitychest = (TileEntityChest)par1World.getBlockTileEntity(j1, k1, l1);

                    if (tileentitychest != null && tileentitychest != null)
                    {
                        WeightedRandomChestContent.generateChestContents(par2Random, this.theBonusChestGenerator, tileentitychest, this.itemsToGenerateInBonusChest);
                    }

                    if (par1World.isAirBlock(j1 - 1, k1, l1) && par1World.doesBlockHaveSolidTopSurface(j1 - 1, k1 - 1, l1))
                    {
                        par1World.setBlock(j1 - 1, k1, l1, Block.torchWood.blockID, 0, 2);
                    }

                    if (par1World.isAirBlock(j1 + 1, k1, l1) && par1World.doesBlockHaveSolidTopSurface(j1 - 1, k1 - 1, l1))
                    {
                        par1World.setBlock(j1 + 1, k1, l1, Block.torchWood.blockID, 0, 2);
                    }

                    if (par1World.isAirBlock(j1, k1, l1 - 1) && par1World.doesBlockHaveSolidTopSurface(j1 - 1, k1 - 1, l1))
                    {
                        par1World.setBlock(j1, k1, l1 - 1, Block.torchWood.blockID, 0, 2);
                    }

                    if (par1World.isAirBlock(j1, k1, l1 + 1) && par1World.doesBlockHaveSolidTopSurface(j1 - 1, k1 - 1, l1))
                    {
                        par1World.setBlock(j1, k1, l1 + 1, Block.torchWood.blockID, 0, 2);
                    }

                    return true;
                }
            }

            return false;
        }
    }
}
