package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentNetherBridgeStairs extends ComponentNetherBridgePiece
{
    public ComponentNetherBridgeStairs() {}

    public ComponentNetherBridgeStairs(final int par1, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox, final int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    public void buildComponent(final StructureComponent par1StructureComponent, final List par2List, final Random par3Random)
    {
        this.getNextComponentZ((ComponentNetherBridgeStartPiece)par1StructureComponent, par2List, par3Random, 6, 2, false);
    }

    /**
     * Creates and returns a new component piece. Or null if it could not find enough room to place it.
     */
    public static ComponentNetherBridgeStairs createValidComponent(final List par0List, final Random par1Random, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -2, 0, 0, 7, 11, 7, par5);
        return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new ComponentNetherBridgeStairs(par6, par1Random, structureboundingbox, par5) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox)
    {
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 6, 1, 6, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 0, 6, 10, 6, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 0, 1, 8, 0, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 2, 0, 6, 8, 0, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 1, 0, 8, 6, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 2, 1, 6, 8, 6, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 2, 6, 5, 8, 6, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 3, 2, 0, 5, 4, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 3, 2, 6, 5, 2, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 3, 4, 6, 5, 4, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.placeBlockAtCurrentPosition(par1World, Block.netherBrick.blockID, 0, 5, 2, 5, par3StructureBoundingBox);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 2, 5, 4, 3, 5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 2, 5, 3, 4, 5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 2, 2, 5, 2, 5, 5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 2, 5, 1, 6, 5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 7, 1, 5, 7, 4, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 8, 2, 6, 8, 4, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 2, 6, 0, 4, 8, 0, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 2, 5, 0, 4, 5, 0, Block.netherFence.blockID, Block.netherFence.blockID, false);

        for (int i = 0; i <= 6; ++i)
        {
            for (int j = 0; j <= 6; ++j)
            {
                this.fillCurrentPositionBlocksDownwards(par1World, Block.netherBrick.blockID, 0, i, -1, j, par3StructureBoundingBox);
            }
        }

        return true;
    }
}
