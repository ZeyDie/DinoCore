package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentStrongholdCrossing extends ComponentStronghold
{
    private boolean field_74996_b;
    private boolean field_74997_c;
    private boolean field_74995_d;
    private boolean field_74999_h;

    public ComponentStrongholdCrossing() {}

    public ComponentStrongholdCrossing(final int par1, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox, final int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.field_143013_d = this.getRandomDoor(par2Random);
        this.boundingBox = par3StructureBoundingBox;
        this.field_74996_b = par2Random.nextBoolean();
        this.field_74997_c = par2Random.nextBoolean();
        this.field_74995_d = par2Random.nextBoolean();
        this.field_74999_h = par2Random.nextInt(3) > 0;
    }

    protected void func_143012_a(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143012_a(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("leftLow", this.field_74996_b);
        par1NBTTagCompound.setBoolean("leftHigh", this.field_74997_c);
        par1NBTTagCompound.setBoolean("rightLow", this.field_74995_d);
        par1NBTTagCompound.setBoolean("rightHigh", this.field_74999_h);
    }

    protected void func_143011_b(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143011_b(par1NBTTagCompound);
        this.field_74996_b = par1NBTTagCompound.getBoolean("leftLow");
        this.field_74997_c = par1NBTTagCompound.getBoolean("leftHigh");
        this.field_74995_d = par1NBTTagCompound.getBoolean("rightLow");
        this.field_74999_h = par1NBTTagCompound.getBoolean("rightHigh");
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    public void buildComponent(final StructureComponent par1StructureComponent, final List par2List, final Random par3Random)
    {
        int i = 3;
        int j = 5;

        if (this.coordBaseMode == 1 || this.coordBaseMode == 2)
        {
            i = 8 - i;
            j = 8 - j;
        }

        this.getNextComponentNormal((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 5, 1);

        if (this.field_74996_b)
        {
            this.getNextComponentX((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, i, 1);
        }

        if (this.field_74997_c)
        {
            this.getNextComponentX((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, j, 7);
        }

        if (this.field_74995_d)
        {
            this.getNextComponentZ((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, i, 1);
        }

        if (this.field_74999_h)
        {
            this.getNextComponentZ((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, j, 7);
        }
    }

    public static ComponentStrongholdCrossing findValidPlacement(final List par0List, final Random par1Random, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -3, 0, 10, 9, 11, par5);
        return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new ComponentStrongholdCrossing(par6, par1Random, structureboundingbox, par5) : null;
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
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 9, 8, 10, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 4, 3, 0);

            if (this.field_74996_b)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 3, 1, 0, 5, 3, 0, 0, false);
            }

            if (this.field_74995_d)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 9, 3, 1, 9, 5, 3, 0, 0, false);
            }

            if (this.field_74997_c)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 5, 7, 0, 7, 9, 0, 0, false);
            }

            if (this.field_74999_h)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 9, 5, 7, 9, 7, 9, 0, 0, false);
            }

            this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 1, 10, 7, 3, 10, 0, 0, false);
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 2, 1, 8, 2, 6, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 5, 4, 4, 9, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 8, 1, 5, 8, 4, 9, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 4, 7, 3, 4, 9, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 3, 5, 3, 3, 6, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 3, 4, 3, 3, 4, Block.stoneSingleSlab.blockID, Block.stoneSingleSlab.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 4, 6, 3, 4, 6, Block.stoneSingleSlab.blockID, Block.stoneSingleSlab.blockID, false);
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 5, 1, 7, 7, 1, 8, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 1, 9, 7, 1, 9, Block.stoneSingleSlab.blockID, Block.stoneSingleSlab.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 2, 7, 7, 2, 7, Block.stoneSingleSlab.blockID, Block.stoneSingleSlab.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 5, 7, 4, 5, 9, Block.stoneSingleSlab.blockID, Block.stoneSingleSlab.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 8, 5, 7, 8, 5, 9, Block.stoneSingleSlab.blockID, Block.stoneSingleSlab.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 5, 7, 7, 5, 9, Block.stoneDoubleSlab.blockID, Block.stoneDoubleSlab.blockID, false);
            this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 6, 5, 6, par3StructureBoundingBox);
            return true;
        }
    }
}
