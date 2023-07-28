package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentNetherBridgeCrossing2 extends ComponentNetherBridgePiece
{
    public ComponentNetherBridgeCrossing2() {}

    public ComponentNetherBridgeCrossing2(final int par1, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox, final int par4)
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
        this.getNextComponentNormal((ComponentNetherBridgeStartPiece)par1StructureComponent, par2List, par3Random, 1, 0, true);
        this.getNextComponentX((ComponentNetherBridgeStartPiece)par1StructureComponent, par2List, par3Random, 0, 1, true);
        this.getNextComponentZ((ComponentNetherBridgeStartPiece)par1StructureComponent, par2List, par3Random, 0, 1, true);
    }

    /**
     * Creates and returns a new component piece. Or null if it could not find enough room to place it.
     */
    public static ComponentNetherBridgeCrossing2 createValidComponent(final List par0List, final Random par1Random, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, 0, 0, 5, 7, 5, par5);
        return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new ComponentNetherBridgeCrossing2(par6, par1Random, structureboundingbox, par5) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox)
    {
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 1, 4, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 0, 4, 5, 4, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 0, 0, 5, 0, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 2, 0, 4, 5, 0, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 4, 0, 5, 4, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 2, 4, 4, 5, 4, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 6, 0, 4, 6, 4, Block.netherBrick.blockID, Block.netherBrick.blockID, false);

        for (int i = 0; i <= 4; ++i)
        {
            for (int j = 0; j <= 4; ++j)
            {
                this.fillCurrentPositionBlocksDownwards(par1World, Block.netherBrick.blockID, 0, i, -1, j, par3StructureBoundingBox);
            }
        }

        return true;
    }
}
