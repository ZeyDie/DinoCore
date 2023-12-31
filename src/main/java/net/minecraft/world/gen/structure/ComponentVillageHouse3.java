package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentVillageHouse3 extends ComponentVillage
{
    public ComponentVillageHouse3() {}

    public ComponentVillageHouse3(final ComponentVillageStartPiece par1ComponentVillageStartPiece, final int par2, final Random par3Random, final StructureBoundingBox par4StructureBoundingBox, final int par5)
    {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
    }

    public static ComponentVillageHouse3 func_74921_a(final ComponentVillageStartPiece par0ComponentVillageStartPiece, final List par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6, final int par7)
    {
        final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 9, 7, 12, par6);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new ComponentVillageHouse3(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox)
    {
        if (this.field_143015_k < 0)
        {
            this.field_143015_k = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

            if (this.field_143015_k < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 7 - 1, 0);
        }

        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, 1, 7, 4, 4, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 2, 1, 6, 8, 4, 10, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 2, 0, 5, 8, 0, 10, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 1, 7, 0, 4, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 0, 3, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 8, 0, 0, 8, 3, 10, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 0, 7, 2, 0, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 5, 2, 1, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 2, 0, 6, 2, 3, 10, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 0, 10, 7, 3, 10, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 2, 0, 7, 3, 0, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 2, 5, 2, 3, 5, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 4, 1, 8, 4, 1, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 4, 4, 3, 4, 4, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 5, 2, 8, 5, 3, Block.planks.blockID, Block.planks.blockID, false);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 0, 4, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 0, 4, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 8, 4, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 8, 4, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 8, 4, 4, par3StructureBoundingBox);
        final int i = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 3);
        final int j = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 2);
        int k;
        int l;

        for (k = -1; k <= 2; ++k)
        {
            for (l = 0; l <= 8; ++l)
            {
                this.placeBlockAtCurrentPosition(par1World, Block.stairsWoodOak.blockID, i, l, 4 + k, k, par3StructureBoundingBox);

                if ((k > -1 || l <= 1) && (k > 0 || l <= 3) && (k > 1 || l <= 4 || l >= 6))
                {
                    this.placeBlockAtCurrentPosition(par1World, Block.stairsWoodOak.blockID, j, l, 4 + k, 5 - k, par3StructureBoundingBox);
                }
            }
        }

        this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 4, 5, 3, 4, 10, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 4, 2, 7, 4, 10, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 5, 4, 4, 5, 10, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 5, 4, 6, 5, 10, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 6, 3, 5, 6, 10, Block.planks.blockID, Block.planks.blockID, false);
        k = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 0);
        int i1;

        for (l = 4; l >= 1; --l)
        {
            this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, l, 2 + l, 7 - l, par3StructureBoundingBox);

            for (i1 = 8 - l; i1 <= 10; ++i1)
            {
                this.placeBlockAtCurrentPosition(par1World, Block.stairsWoodOak.blockID, k, l, 2 + l, i1, par3StructureBoundingBox);
            }
        }

        l = this.getMetadataWithOffset(Block.stairsWoodOak.blockID, 1);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 6, 6, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 7, 5, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairsWoodOak.blockID, l, 6, 6, 4, par3StructureBoundingBox);
        int j1;

        for (i1 = 6; i1 <= 8; ++i1)
        {
            for (j1 = 5; j1 <= 10; ++j1)
            {
                this.placeBlockAtCurrentPosition(par1World, Block.stairsWoodOak.blockID, l, i1, 12 - i1, j1, par3StructureBoundingBox);
            }
        }

        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 0, 2, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 0, 2, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 2, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 2, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 4, 2, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 5, 2, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 6, 2, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 8, 2, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 8, 2, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 8, 2, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 8, 2, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 8, 2, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 8, 2, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 8, 2, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 8, 2, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 8, 2, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 2, 2, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 2, 2, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 2, 2, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 2, 2, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 4, 4, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 5, 4, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.wood.blockID, 0, 6, 4, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 5, 5, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, 0, 0, 2, 1, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, 0, 0, 2, 2, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 2, 3, 1, par3StructureBoundingBox);
        this.placeDoorAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 2, 1, 0, this.getMetadataWithOffset(Block.doorWood.blockID, 1));
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, -1, 3, 2, -1, 0, 0, false);

        if (this.getBlockIdAtCurrentPosition(par1World, 2, 0, -1, par3StructureBoundingBox) == 0 && this.getBlockIdAtCurrentPosition(par1World, 2, -1, -1, par3StructureBoundingBox) != 0)
        {
            this.placeBlockAtCurrentPosition(par1World, Block.stairsCobblestone.blockID, this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 3), 2, 0, -1, par3StructureBoundingBox);
        }

        for (i1 = 0; i1 < 5; ++i1)
        {
            for (j1 = 0; j1 < 9; ++j1)
            {
                this.clearCurrentPositionBlocksUpwards(par1World, j1, 7, i1, par3StructureBoundingBox);
                this.fillCurrentPositionBlocksDownwards(par1World, Block.cobblestone.blockID, 0, j1, -1, i1, par3StructureBoundingBox);
            }
        }

        for (i1 = 5; i1 < 11; ++i1)
        {
            for (j1 = 2; j1 < 9; ++j1)
            {
                this.clearCurrentPositionBlocksUpwards(par1World, j1, 7, i1, par3StructureBoundingBox);
                this.fillCurrentPositionBlocksDownwards(par1World, Block.cobblestone.blockID, 0, j1, -1, i1, par3StructureBoundingBox);
            }
        }

        this.spawnVillagers(par1World, par3StructureBoundingBox, 4, 1, 2, 2);
        return true;
    }
}
