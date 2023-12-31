package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;

import java.util.Random;

class StructureStrongholdStones extends StructurePieceBlockSelector
{
    private StructureStrongholdStones() {}

    /**
     * picks Block Ids and Metadata (Silverfish)
     */
    public void selectBlocks(final Random par1Random, final int par2, final int par3, final int par4, final boolean par5)
    {
        if (par5)
        {
            this.selectedBlockId = Block.stoneBrick.blockID;
            final float f = par1Random.nextFloat();

            if (f < 0.2F)
            {
                this.selectedBlockMetaData = 2;
            }
            else if (f < 0.5F)
            {
                this.selectedBlockMetaData = 1;
            }
            else if (f < 0.55F)
            {
                this.selectedBlockId = Block.silverfish.blockID;
                this.selectedBlockMetaData = 2;
            }
            else
            {
                this.selectedBlockMetaData = 0;
            }
        }
        else
        {
            this.selectedBlockId = 0;
            this.selectedBlockMetaData = 0;
        }
    }

    StructureStrongholdStones(final StructureStrongholdPieceWeight2 par1StructureStrongholdPieceWeight2)
    {
        this();
    }
}
