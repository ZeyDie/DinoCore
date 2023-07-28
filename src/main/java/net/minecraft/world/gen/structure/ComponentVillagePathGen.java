package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentVillagePathGen extends ComponentVillageRoadPiece
{
    private int averageGroundLevel;

    public ComponentVillagePathGen() {}

    public ComponentVillagePathGen(final ComponentVillageStartPiece par1ComponentVillageStartPiece, final int par2, final Random par3Random, final StructureBoundingBox par4StructureBoundingBox, final int par5)
    {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
        this.averageGroundLevel = Math.max(par4StructureBoundingBox.getXSize(), par4StructureBoundingBox.getZSize());
    }

    protected void func_143012_a(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143012_a(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Length", this.averageGroundLevel);
    }

    protected void func_143011_b(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143011_b(par1NBTTagCompound);
        this.averageGroundLevel = par1NBTTagCompound.getInteger("Length");
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    public void buildComponent(final StructureComponent par1StructureComponent, final List par2List, final Random par3Random)
    {
        boolean flag = false;
        int i;
        StructureComponent structurecomponent1;

        for (i = par3Random.nextInt(5); i < this.averageGroundLevel - 8; i += 2 + par3Random.nextInt(5))
        {
            structurecomponent1 = this.getNextComponentNN((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, 0, i);

            if (structurecomponent1 != null)
            {
                i += Math.max(structurecomponent1.boundingBox.getXSize(), structurecomponent1.boundingBox.getZSize());
                flag = true;
            }
        }

        for (i = par3Random.nextInt(5); i < this.averageGroundLevel - 8; i += 2 + par3Random.nextInt(5))
        {
            structurecomponent1 = this.getNextComponentPP((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, 0, i);

            if (structurecomponent1 != null)
            {
                i += Math.max(structurecomponent1.boundingBox.getXSize(), structurecomponent1.boundingBox.getZSize());
                flag = true;
            }
        }

        if (flag && par3Random.nextInt(3) > 0)
        {
            switch (this.coordBaseMode)
            {
                case 0:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, 1, this.getComponentType());
                    break;
                case 1:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, this.getComponentType());
                    break;
                case 2:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, 1, this.getComponentType());
                    break;
                case 3:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, this.getComponentType());
            }
        }

        if (flag && par3Random.nextInt(3) > 0)
        {
            switch (this.coordBaseMode)
            {
                case 0:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, 3, this.getComponentType());
                    break;
                case 1:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, this.getComponentType());
                    break;
                case 2:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, 3, this.getComponentType());
                    break;
                case 3:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, this.getComponentType());
            }
        }
    }

    public static StructureBoundingBox func_74933_a(final ComponentVillageStartPiece par0ComponentVillageStartPiece, final List par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6)
    {
        for (int i1 = 7 * MathHelper.getRandomIntegerInRange(par2Random, 3, 5); i1 >= 7; i1 -= 7)
        {
            final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 3, 3, i1, par6);

            if (StructureComponent.findIntersecting(par1List, structureboundingbox) == null)
            {
                return structureboundingbox;
            }
        }

        return null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox)
    {
        final int i = this.getBiomeSpecificBlock(Block.gravel.blockID, 0);

        for (int j = this.boundingBox.minX; j <= this.boundingBox.maxX; ++j)
        {
            for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k)
            {
                if (par3StructureBoundingBox.isVecInside(j, 64, k))
                {
                    final int l = par1World.getTopSolidOrLiquidBlock(j, k) - 1;
                    par1World.setBlock(j, l, k, i, 0, 2);
                }
            }
        }

        return true;
    }
}
