package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentStrongholdStraight extends ComponentStronghold
{
    private boolean expandsX;
    private boolean expandsZ;

    public ComponentStrongholdStraight() {}

    public ComponentStrongholdStraight(final int par1, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox, final int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.field_143013_d = this.getRandomDoor(par2Random);
        this.boundingBox = par3StructureBoundingBox;
        this.expandsX = par2Random.nextInt(2) == 0;
        this.expandsZ = par2Random.nextInt(2) == 0;
    }

    protected void func_143012_a(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143012_a(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("Left", this.expandsX);
        par1NBTTagCompound.setBoolean("Right", this.expandsZ);
    }

    protected void func_143011_b(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143011_b(par1NBTTagCompound);
        this.expandsX = par1NBTTagCompound.getBoolean("Left");
        this.expandsZ = par1NBTTagCompound.getBoolean("Right");
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    public void buildComponent(final StructureComponent par1StructureComponent, final List par2List, final Random par3Random)
    {
        this.getNextComponentNormal((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 1);

        if (this.expandsX)
        {
            this.getNextComponentX((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 2);
        }

        if (this.expandsZ)
        {
            this.getNextComponentZ((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 2);
        }
    }

    public static ComponentStrongholdStraight findValidPlacement(final List par0List, final Random par1Random, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, 7, par5);
        return canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new ComponentStrongholdStraight(par6, par1Random, structureboundingbox, par5) : null;
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
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 6, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_143013_d, 1, 1, 0);
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, EnumDoor.OPENING, 1, 1, 6);
            this.randomlyPlaceBlock(par1World, par3StructureBoundingBox, par2Random, 0.1F, 1, 2, 1, Block.torchWood.blockID, 0);
            this.randomlyPlaceBlock(par1World, par3StructureBoundingBox, par2Random, 0.1F, 3, 2, 1, Block.torchWood.blockID, 0);
            this.randomlyPlaceBlock(par1World, par3StructureBoundingBox, par2Random, 0.1F, 1, 2, 5, Block.torchWood.blockID, 0);
            this.randomlyPlaceBlock(par1World, par3StructureBoundingBox, par2Random, 0.1F, 3, 2, 5, Block.torchWood.blockID, 0);

            if (this.expandsX)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 2, 0, 3, 4, 0, 0, false);
            }

            if (this.expandsZ)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 1, 2, 4, 3, 4, 0, 0, false);
            }

            return true;
        }
    }
}
