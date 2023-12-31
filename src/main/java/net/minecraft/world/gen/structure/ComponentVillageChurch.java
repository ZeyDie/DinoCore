package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentVillageChurch extends ComponentVillage
{
    public ComponentVillageChurch() {}

    public ComponentVillageChurch(final ComponentVillageStartPiece par1ComponentVillageStartPiece, final int par2, final Random par3Random, final StructureBoundingBox par4StructureBoundingBox, final int par5)
    {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
    }

    public static ComponentVillageChurch func_74919_a(final ComponentVillageStartPiece par0ComponentVillageStartPiece, final List par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6, final int par7)
    {
        final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 5, 12, 9, par6);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new ComponentVillageChurch(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
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

            this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 12 - 1, 0);
        }

        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, 1, 3, 3, 7, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 5, 1, 3, 9, 3, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 0, 3, 0, 8, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, 0, 3, 10, 0, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 10, 3, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 10, 3, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 4, 0, 4, 7, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 0, 4, 4, 4, 7, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, 8, 3, 4, 8, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 5, 4, 3, 10, 4, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 5, 5, 3, 5, 7, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 9, 0, 4, 9, 4, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 4, 0, 4, 4, 4, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 0, 11, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 4, 11, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 2, 11, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 2, 11, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 1, 1, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 1, 1, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 2, 1, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 3, 1, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 3, 1, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairsCobblestone.blockID, this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 3), 1, 1, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairsCobblestone.blockID, this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 3), 2, 1, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairsCobblestone.blockID, this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 3), 3, 1, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairsCobblestone.blockID, this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 1), 1, 2, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairsCobblestone.blockID, this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 0), 3, 2, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 2, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 3, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 4, 2, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 4, 3, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 6, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 7, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 4, 6, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 4, 7, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 2, 6, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 2, 7, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 2, 6, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 2, 7, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 3, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 4, 3, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 2, 3, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 2, 4, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 1, 4, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 3, 4, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 2, 4, 5, par3StructureBoundingBox);
        final int i = this.getMetadataWithOffset(Block.ladder.blockID, 4);
        int j;

        for (j = 1; j <= 9; ++j)
        {
            this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, i, 3, j, 3, par3StructureBoundingBox);
        }

        this.placeBlockAtCurrentPosition(par1World, 0, 0, 2, 1, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, 0, 0, 2, 2, 0, par3StructureBoundingBox);
        this.placeDoorAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 2, 1, 0, this.getMetadataWithOffset(Block.doorWood.blockID, 1));

        if (this.getBlockIdAtCurrentPosition(par1World, 2, 0, -1, par3StructureBoundingBox) == 0 && this.getBlockIdAtCurrentPosition(par1World, 2, -1, -1, par3StructureBoundingBox) != 0)
        {
            this.placeBlockAtCurrentPosition(par1World, Block.stairsCobblestone.blockID, this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 3), 2, 0, -1, par3StructureBoundingBox);
        }

        for (j = 0; j < 9; ++j)
        {
            for (int k = 0; k < 5; ++k)
            {
                this.clearCurrentPositionBlocksUpwards(par1World, k, 12, j, par3StructureBoundingBox);
                this.fillCurrentPositionBlocksDownwards(par1World, Block.cobblestone.blockID, 0, k, -1, j, par3StructureBoundingBox);
            }
        }

        this.spawnVillagers(par1World, par3StructureBoundingBox, 2, 1, 2, 1);
        return true;
    }

    /**
     * Returns the villager type to spawn in this component, based on the number of villagers already spawned.
     */
    protected int getVillagerType(final int par1)
    {
        return 2;
    }
}
