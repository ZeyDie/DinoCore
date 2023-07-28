package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;

import java.util.Random;

class StructureScatteredFeatureStones extends StructurePieceBlockSelector
{
    private StructureScatteredFeatureStones() {}

    /**
     * picks Block Ids and Metadata (Silverfish)
     */
    public void selectBlocks(final Random par1Random, final int par2, final int par3, final int par4, final boolean par5)
    {
        if (par1Random.nextFloat() < 0.4F)
        {
            this.selectedBlockId = Block.cobblestone.blockID;
        }
        else
        {
            this.selectedBlockId = Block.cobblestoneMossy.blockID;
        }
    }

    StructureScatteredFeatureStones(final ComponentScatteredFeaturePieces2 par1ComponentScatteredFeaturePieces2)
    {
        this();
    }
}
