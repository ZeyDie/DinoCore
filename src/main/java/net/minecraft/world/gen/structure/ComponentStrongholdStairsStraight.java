package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentStrongholdStairsStraight extends ComponentStronghold
{
    public ComponentStrongholdStairsStraight() {}

    public ComponentStrongholdStairsStraight(final int par1, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox, final int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.field_143013_d = this.getRandomDoor(par2Random);
        this.boundingBox = par3StructureBoundingBox;
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    public void buildComponent(final StructureComponent par1StructureComponent, final List par2List, final Random par3Random)
    {
        this.getNextComponentNormal((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 1);
    }

    public static ComponentStrongholdStairsStraight findValidPlacement(final List par0List, final Random par1Random, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -7, 0, 5, 11, 8, par5);
        return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new ComponentStrongholdStairsStraight(par6, par1Random, structureboundingbox, par5) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox)
    {
        if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
        {
            return false;
        }
        else
        {
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 10, 7, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 1, 7, 0);
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, EnumDoor.OPENING, 1, 1, 7);
            final int i = this.getMetadataWithOffset(Block.stairsCobblestone.blockID, 2);

            for (int j = 0; j < 6; ++j)
            {
                this.placeBlockAtCurrentPosition(par1World, Block.stairsCobblestone.blockID, i, 1, 6 - j, 1 + j, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stairsCobblestone.blockID, i, 2, 6 - j, 1 + j, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stairsCobblestone.blockID, i, 3, 6 - j, 1 + j, par3StructureBoundingBox);

                if (j < 5)
                {
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 1, 5 - j, 1 + j, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 2, 5 - j, 1 + j, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 3, 5 - j, 1 + j, par3StructureBoundingBox);
                }
            }

            return true;
        }
    }
}
