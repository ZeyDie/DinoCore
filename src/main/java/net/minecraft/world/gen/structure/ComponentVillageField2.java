package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentVillageField2 extends ComponentVillage
{
    /** First crop type for this field. */
    private int cropTypeA;

    /** Second crop type for this field. */
    private int cropTypeB;

    public ComponentVillageField2() {}

    public ComponentVillageField2(final ComponentVillageStartPiece par1ComponentVillageStartPiece, final int par2, final Random par3Random, final StructureBoundingBox par4StructureBoundingBox, final int par5)
    {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
        this.cropTypeA = this.pickRandomCrop(par3Random);
        this.cropTypeB = this.pickRandomCrop(par3Random);
    }

    protected void func_143012_a(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143012_a(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("CA", this.cropTypeA);
        par1NBTTagCompound.setInteger("CB", this.cropTypeB);
    }

    protected void func_143011_b(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143011_b(par1NBTTagCompound);
        this.cropTypeA = par1NBTTagCompound.getInteger("CA");
        this.cropTypeB = par1NBTTagCompound.getInteger("CB");
    }

    /**
     * Returns a crop type to be planted on this field.
     */
    private int pickRandomCrop(final Random par1Random)
    {
        switch (par1Random.nextInt(5))
        {
            case 0:
                return Block.carrot.blockID;
            case 1:
                return Block.potato.blockID;
            default:
                return Block.crops.blockID;
        }
    }

    public static ComponentVillageField2 func_74902_a(final ComponentVillageStartPiece par0ComponentVillageStartPiece, final List par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6, final int par7)
    {
        final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 7, 4, 9, par6);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par1List, structureboundingbox) == null ? new ComponentVillageField2(par0ComponentVillageStartPiece, par7, par2Random, structureboundingbox, par6) : null;
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

            this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 4 - 1, 0);
        }

        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 0, 6, 4, 8, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 1, 2, 0, 7, Block.tilledField.blockID, Block.tilledField.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 0, 1, 5, 0, 7, Block.tilledField.blockID, Block.tilledField.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 0, 0, 8, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 0, 0, 6, 0, 8, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 0, 5, 0, 0, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 8, 5, 0, 8, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 0, 1, 3, 0, 7, Block.waterMoving.blockID, Block.waterMoving.blockID, false);
        int i;

        for (i = 1; i <= 7; ++i)
        {
            this.placeBlockAtCurrentPosition(par1World, this.cropTypeA, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 1, 1, i, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, this.cropTypeA, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 2, 1, i, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, this.cropTypeB, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 4, 1, i, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, this.cropTypeB, MathHelper.getRandomIntegerInRange(par2Random, 2, 7), 5, 1, i, par3StructureBoundingBox);
        }

        for (i = 0; i < 9; ++i)
        {
            for (int j = 0; j < 7; ++j)
            {
                this.clearCurrentPositionBlocksUpwards(par1World, j, 4, i, par3StructureBoundingBox);
                this.fillCurrentPositionBlocksDownwards(par1World, Block.dirt.blockID, 0, j, -1, i, par3StructureBoundingBox);
            }
        }

        return true;
    }
}
