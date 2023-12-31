package net.minecraft.world.gen.structure;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ComponentNetherBridgeEnd extends ComponentNetherBridgePiece
{
    private int fillSeed;

    public ComponentNetherBridgeEnd() {}

    public ComponentNetherBridgeEnd(final int par1, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox, final int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
        this.fillSeed = par2Random.nextInt();
    }

    public static ComponentNetherBridgeEnd func_74971_a(final List par0List, final Random par1Random, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -3, 0, 5, 10, 8, par5);
        return isAboveGround(structureboundingbox) && StructureComponent.findIntersecting(par0List, structureboundingbox) == null ? new ComponentNetherBridgeEnd(par6, par1Random, structureboundingbox, par5) : null;
    }

    protected void func_143011_b(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143011_b(par1NBTTagCompound);
        this.fillSeed = par1NBTTagCompound.getInteger("Seed");
    }

    protected void func_143012_a(final NBTTagCompound par1NBTTagCompound)
    {
        super.func_143012_a(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Seed", this.fillSeed);
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox)
    {
        final Random random1 = new Random((long)this.fillSeed);
        int i;
        int j;
        int k;

        for (i = 0; i <= 4; ++i)
        {
            for (j = 3; j <= 4; ++j)
            {
                k = random1.nextInt(8);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, i, j, 0, i, j, k, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
            }
        }

        i = random1.nextInt(8);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 5, 0, 0, 5, i, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        i = random1.nextInt(8);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 5, 0, 4, 5, i, Block.netherBrick.blockID, Block.netherBrick.blockID, false);

        for (i = 0; i <= 4; ++i)
        {
            j = random1.nextInt(5);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, i, 2, 0, i, 2, j, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        }

        for (i = 0; i <= 4; ++i)
        {
            for (j = 0; j <= 1; ++j)
            {
                k = random1.nextInt(3);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, i, j, 0, i, j, k, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
            }
        }

        return true;
    }
}
