package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.Random;

abstract class ComponentScatteredFeature extends StructureComponent
{
    /** The size of the bounding box for this feature in the X axis */
    protected int scatteredFeatureSizeX;

    /** The size of the bounding box for this feature in the Y axis */
    protected int scatteredFeatureSizeY;

    /** The size of the bounding box for this feature in the Z axis */
    protected int scatteredFeatureSizeZ;
    protected int field_74936_d = -1;

    public ComponentScatteredFeature() {}

    protected ComponentScatteredFeature(final Random par1Random, final int par2, final int par3, final int par4, final int par5, final int par6, final int par7)
    {
        super(0);
        this.scatteredFeatureSizeX = par5;
        this.scatteredFeatureSizeY = par6;
        this.scatteredFeatureSizeZ = par7;
        this.coordBaseMode = par1Random.nextInt(4);

        switch (this.coordBaseMode)
        {
            case 0:
            case 2:
                this.boundingBox = new StructureBoundingBox(par2, par3, par4, par2 + par5 - 1, par3 + par6 - 1, par4 + par7 - 1);
                break;
            default:
                this.boundingBox = new StructureBoundingBox(par2, par3, par4, par2 + par7 - 1, par3 + par6 - 1, par4 + par5 - 1);
        }
    }

    protected void func_143012_a(final NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("Width", this.scatteredFeatureSizeX);
        par1NBTTagCompound.setInteger("Height", this.scatteredFeatureSizeY);
        par1NBTTagCompound.setInteger("Depth", this.scatteredFeatureSizeZ);
        par1NBTTagCompound.setInteger("HPos", this.field_74936_d);
    }

    protected void func_143011_b(final NBTTagCompound par1NBTTagCompound)
    {
        this.scatteredFeatureSizeX = par1NBTTagCompound.getInteger("Width");
        this.scatteredFeatureSizeY = par1NBTTagCompound.getInteger("Height");
        this.scatteredFeatureSizeZ = par1NBTTagCompound.getInteger("Depth");
        this.field_74936_d = par1NBTTagCompound.getInteger("HPos");
    }

    protected boolean func_74935_a(final World par1World, final StructureBoundingBox par2StructureBoundingBox, final int par3)
    {
        if (this.field_74936_d >= 0)
        {
            return true;
        }
        else
        {
            int j = 0;
            int k = 0;

            for (int l = this.boundingBox.minZ; l <= this.boundingBox.maxZ; ++l)
            {
                for (int i1 = this.boundingBox.minX; i1 <= this.boundingBox.maxX; ++i1)
                {
                    if (par2StructureBoundingBox.isVecInside(i1, 64, l))
                    {
                        j += Math.max(par1World.getTopSolidOrLiquidBlock(i1, l), par1World.provider.getAverageGroundLevel());
                        ++k;
                    }
                }
            }

            if (k == 0)
            {
                return false;
            }
            else
            {
                this.field_74936_d = j / k;
                this.boundingBox.offset(0, this.field_74936_d - this.boundingBox.minY + par3, 0);
                return true;
            }
        }
    }
}
