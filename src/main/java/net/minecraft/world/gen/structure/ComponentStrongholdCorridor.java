package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentStrongholdCorridor extends ComponentStronghold
{
    private int field_74993_a;

    public ComponentStrongholdCorridor() {}

    public ComponentStrongholdCorridor(final int par1, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox, final int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
        this.field_74993_a = par4 != 2 && par4 != 0 ? par3StructureBoundingBox.getXSize() : par3StructureBoundingBox.getZSize();
    }

    protected void func_143012_a(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143012_a(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Steps", this.field_74993_a);
    }

    protected void func_143011_b(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143011_b(par1NBTTagCompound);
        this.field_74993_a = par1NBTTagCompound.getInteger("Steps");
    }

    public static StructureBoundingBox func_74992_a(final List par0List, final Random par1Random, final int par2, final int par3, final int par4, final int par5)
    {
        final boolean flag = true;
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, 4, par5);
        final StructureComponent structurecomponent = StructureComponent.findIntersecting(par0List, structureboundingbox);

        if (structurecomponent == null)
        {
            return null;
        }
        else
        {
            if (structurecomponent.getBoundingBox().minY == structureboundingbox.minY)
            {
                for (int i1 = 3; i1 >= 1; --i1)
                {
                    structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, i1 - 1, par5);

                    if (!structurecomponent.getBoundingBox().intersectsWith(structureboundingbox))
                    {
                        return StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, i1, par5);
                    }
                }
            }

            return null;
        }
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
            for (int i = 0; i < this.field_74993_a; ++i)
            {
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 0, 0, i, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 1, 0, i, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 2, 0, i, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 3, 0, i, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 4, 0, i, par3StructureBoundingBox);

                for (int j = 1; j <= 3; ++j)
                {
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 0, j, i, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, 0, 0, 1, j, i, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, 0, 0, 2, j, i, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, 0, 0, 3, j, i, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 4, j, i, par3StructureBoundingBox);
                }

                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 0, 4, i, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 1, 4, i, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 2, 4, i, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 3, 4, i, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 4, 4, i, par3StructureBoundingBox);
            }

            return true;
        }
    }
}
