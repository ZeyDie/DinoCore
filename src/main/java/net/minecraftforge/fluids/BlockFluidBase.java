package net.minecraftforge.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This is a base implementation for Fluid blocks.
 *
 * It is highly recommended that you extend this class or one of the Forge-provided child classes.
 *
 * @author King Lemming, OvermindDL1
 *
 */
public abstract class BlockFluidBase extends Block implements IFluidBlock
{
    protected final static Map<Integer, Boolean> defaultDisplacementIds = new HashMap<Integer, Boolean>();

    static
    {
        defaultDisplacementIds.put(Block.doorWood.blockID, false);
        defaultDisplacementIds.put(Block.doorIron.blockID, false);
        defaultDisplacementIds.put(Block.signPost.blockID, false);
        defaultDisplacementIds.put(Block.signWall.blockID, false);
        defaultDisplacementIds.put(Block.reed.blockID,     false);
    }
    protected Map<Integer, Boolean> displacementIds = new HashMap<Integer, Boolean>();

    protected int quantaPerBlock = 8;
    protected float quantaPerBlockFloat = 8.0F;
    protected int density = 1;
    protected int densityDir = -1;
	protected int temperature = 295;

    protected int tickRate = 20;
    protected int renderPass = 1;
    protected int maxScaledLight = 0;

    protected final String fluidName;

    public BlockFluidBase(final int id, final Fluid fluid, final Material material)
    {
        super(id, material);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setTickRandomly(true);
        this.disableStats();

        this.fluidName = fluid.getName();
        this.density = fluid.density;
        this.temperature = fluid.temperature;
        this.maxScaledLight = fluid.luminosity;
        this.tickRate = fluid.viscosity / 200;
        this.densityDir = fluid.density > 0 ? -1 : 1;
        fluid.setBlockID(id);

        displacementIds.putAll(defaultDisplacementIds);
    }

    public BlockFluidBase setQuantaPerBlock(int quantaPerBlock)
    {
        int quantaPerBlock1 = quantaPerBlock;
        if (quantaPerBlock1 > 16 || quantaPerBlock1 < 1) quantaPerBlock1 = 8;
        this.quantaPerBlock = quantaPerBlock1;
        this.quantaPerBlockFloat = quantaPerBlock1;
        return this;
    }

    public BlockFluidBase setDensity(int density)
    {
        int density1 = density;
        if (density1 == 0) density1 = 1;
        this.density = density1;
        this.densityDir = density1 > 0 ? -1 : 1;
        return this;
    }

    public BlockFluidBase setTemperature(final int temperature)
    {
        this.temperature = temperature;
        return this;
    }

    public BlockFluidBase setTickRate(int tickRate)
    {
        int tickRate1 = tickRate;
        if (tickRate1 <= 0) tickRate1 = 20;
        this.tickRate = tickRate1;
        return this;
    }

    public BlockFluidBase setRenderPass(final int renderPass)
    {
        this.renderPass = renderPass;
        return this;
    }

    public BlockFluidBase setMaxScaledLight(final int maxScaledLight)
    {
        this.maxScaledLight = maxScaledLight;
        return this;
    }

    /**
     * Returns true if the block at (x, y, z) is displaceable. Does not displace the block.
     */
    public boolean canDisplace(final IBlockAccess world, final int x, final int y, final int z)
    {
        if (world.isAirBlock(x, y, z)) return true;

        final int bId = world.getBlockId(x, y, z);

        if (bId == blockID)
        {
            return false;
        }

        if (displacementIds.containsKey(bId))
        {
            return displacementIds.get(bId);
        }

        final Material material = Block.blocksList[bId].blockMaterial;
        if (material.blocksMovement() || material == Material.portal)
        {
            return false;
        }

        final int density = getDensity(world, x, y, z);
        if (density == Integer.MAX_VALUE) 
        {
        	 return true;
        }
        
        if (this.density > density)
        {
        	return true;
        }
        else
        {
        	return false;
        }
    }

    /**
     * Attempt to displace the block at (x, y, z), return true if it was displaced.
     */
    public boolean displaceIfPossible(final World world, final int x, final int y, final int z)
    {
        if (world.isAirBlock(x, y, z))
        {
            return true;
        }

        final int bId = world.getBlockId(x, y, z);
        if (bId == blockID)
        {
            return false;
        }

        if (displacementIds.containsKey(bId))
        {
            if (displacementIds.get(bId))
            {
                Block.blocksList[bId].dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
                return true;
            }
            return false;
        }

        final Material material = Block.blocksList[bId].blockMaterial;
        if (material.blocksMovement() || material == Material.portal)
        {
            return false;
        }

        final int density = getDensity(world, x, y, z);
        if (density == Integer.MAX_VALUE) 
        {
        	 Block.blocksList[bId].dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        	 return true;
        }
        
        if (this.density > density)
        {
        	return true;
        }
        else
        {
        	return false;
        }
    }

    public abstract int getQuantaValue(IBlockAccess world, int x, int y, int z);

    @Override
    public abstract boolean canCollideCheck(int meta, boolean fullHit);

    public abstract int getMaxRenderHeightMeta();

    /* BLOCK FUNCTIONS */
    @Override
    public void onBlockAdded(final World world, final int x, final int y, final int z)
    {
        world.scheduleBlockUpdate(x, y, z, blockID, tickRate);
    }

    @Override
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final int blockId)
    {
        world.scheduleBlockUpdate(x, y, z, blockID, tickRate);
    }

    // Used to prevent updates on chunk generation
    @Override
    public boolean func_82506_l()
    {
        return false;
    }

    @Override
    public boolean getBlocksMovement(final IBlockAccess world, final int x, final int y, final int z)
    {
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int x, final int y, final int z)
    {
        return null;
    }

    @Override
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return 0;
    }

    @Override
    public int quantityDropped(final Random par1Random)
    {
        return 0;
    }

    @Override
    public int tickRate(final World world)
    {
        return tickRate;
    }

    @Override
    public void velocityToAddToEntity(final World world, final int x, final int y, final int z, final Entity entity, final Vec3 vec)
    {
        if (densityDir > 0) return;
        final Vec3 vec_flow = this.getFlowVector(world, x, y, z);
        vec.xCoord += vec_flow.xCoord * (quantaPerBlock * 4);
        vec.yCoord += vec_flow.yCoord * (quantaPerBlock * 4);
        vec.zCoord += vec_flow.zCoord * (quantaPerBlock * 4);
    }

    @Override
    public int getLightValue(final IBlockAccess world, final int x, final int y, final int z)
    {
        if (maxScaledLight == 0)
        {
            return super.getLightValue(world, x, y, z);
        }
        final int data = world.getBlockMetadata(x, y, z);
        return (int) (data / quantaPerBlockFloat * maxScaledLight);
    }

    @Override
    public int getRenderType()
    {
        return FluidRegistry.renderIdFluid;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public float getBlockBrightness(final IBlockAccess world, final int x, final int y, final int z)
    {
        final float lightThis = world.getLightBrightness(x, y, z);
        final float lightUp = world.getLightBrightness(x, y + 1, z);
        return lightThis > lightUp ? lightThis : lightUp;
    }

    @Override
    public int getMixedBrightnessForBlock(final IBlockAccess world, final int x, final int y, final int z)
    {
        final int lightThis     = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
        final int lightUp       = world.getLightBrightnessForSkyBlocks(x, y + 1, z, 0);
        final int lightThisBase = lightThis & 255;
        final int lightUpBase   = lightUp & 255;
        final int lightThisExt  = lightThis >> 16 & 255;
        final int lightUpExt    = lightUp >> 16 & 255;
        return (lightThisBase > lightUpBase ? lightThisBase : lightUpBase) |
               ((lightThisExt > lightUpExt ? lightThisExt : lightUpExt) << 16);
    }

    @Override
    public int getRenderBlockPass()
    {
        return renderPass;
    }

    @Override
    public boolean shouldSideBeRendered(final IBlockAccess world, final int x, final int y, final int z, final int side)
    {
        if (world.getBlockId(x, y, z) != blockID)
        {
            return !world.isBlockOpaqueCube(x, y, z);
        }
        final Material mat = world.getBlockMaterial(x, y, z);
        return mat == this.blockMaterial ? false : super.shouldSideBeRendered(world, x, y, z, side);
    }

    /* FLUID FUNCTIONS */
    public static final int getDensity(final IBlockAccess world, final int x, final int y, final int z)
    {
        final Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (!(block instanceof BlockFluidBase))
        {
            return Integer.MAX_VALUE;
        }
        return ((BlockFluidBase)block).density;
    }
	
    public static final int getTemperature(final IBlockAccess world, final int x, final int y, final int z)
    {
        final Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (!(block instanceof BlockFluidBase))
        {
            return Integer.MAX_VALUE;
        }
        return ((BlockFluidBase)block).temperature;
    }

    public static double getFlowDirection(final IBlockAccess world, final int x, final int y, final int z)
    {
        final Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (!world.getBlockMaterial(x, y, z).isLiquid())
        {
            return -1000.0;
        }
        final Vec3 vec = ((BlockFluidBase) block).getFlowVector(world, x, y, z);
        return vec.xCoord == 0.0D && vec.zCoord == 0.0D ? -1000.0D : Math.atan2(vec.zCoord, vec.xCoord) - Math.PI / 2.0D;
    }

    public final int getQuantaValueBelow(final IBlockAccess world, final int x, final int y, final int z, final int belowThis)
    {
        final int quantaRemaining = getQuantaValue(world, x, y, z);
        if (quantaRemaining >= belowThis)
        {
            return -1;
        }
        return quantaRemaining;
    }

    public final int getQuantaValueAbove(final IBlockAccess world, final int x, final int y, final int z, final int aboveThis)
    {
        final int quantaRemaining = getQuantaValue(world, x, y, z);
        if (quantaRemaining <= aboveThis)
        {
            return -1;
        }
        return quantaRemaining;
    }

    public final float getQuantaPercentage(final IBlockAccess world, final int x, final int y, final int z)
    {
        final int quantaRemaining = getQuantaValue(world, x, y, z);
        return quantaRemaining / quantaPerBlockFloat;
    }

    public Vec3 getFlowVector(final IBlockAccess world, final int x, final int y, final int z)
    {
        Vec3 vec = world.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);
        final int decay = quantaPerBlock - getQuantaValue(world, x, y, z);

        for (int side = 0; side < 4; ++side)
        {
            int x2 = x;
            int z2 = z;

            switch (side)
            {
                case 0: --x2; break;
                case 1: --z2; break;
                case 2: ++x2; break;
                case 3: ++z2; break;
            }

            int otherDecay = quantaPerBlock - getQuantaValue(world, x2, y, z2);
            if (otherDecay >= quantaPerBlock)
            {
                if (!world.getBlockMaterial(x2, y, z2).blocksMovement())
                {
                    otherDecay = quantaPerBlock - getQuantaValue(world, x2, y - 1, z2);
                    if (otherDecay >= 0)
                    {
                        final int power = otherDecay - (decay - quantaPerBlock);
                        vec = vec.addVector((x2 - x) * power, (y - y) * power, (z2 - z) * power);
                    }
                }
            }
            else if (otherDecay >= 0)
            {
                final int power = otherDecay - decay;
                vec = vec.addVector((x2 - x) * power, (y - y) * power, (z2 - z) * power);
            }
        }

        if (world.getBlockId(x, y + 1, z) == blockID)
        {
            final boolean flag =
                isBlockSolid(world, x,     y,     z - 1, 2) ||
                isBlockSolid(world, x,     y,     z + 1, 3) ||
                isBlockSolid(world, x - 1, y,     z,     4) ||
                isBlockSolid(world, x + 1, y,     z,     5) ||
                isBlockSolid(world, x,     y + 1, z - 1, 2) ||
                isBlockSolid(world, x,     y + 1, z + 1, 3) ||
                isBlockSolid(world, x - 1, y + 1, z,     4) ||
                isBlockSolid(world, x + 1, y + 1, z,     5);

            if (flag)
            {
                vec = vec.normalize().addVector(0.0D, -6.0D, 0.0D);
            }
        }
        vec = vec.normalize();
        return vec;
    }

    /* IFluidBlock */
    @Override
    public Fluid getFluid()
    {
        return FluidRegistry.getFluid(fluidName);
    }

    @Override
    public float getFilledPercentage(final World world, final int x, final int y, final int z)
    {
        final int quantaRemaining = getQuantaValue(world, x, y, z) + 1;
        float remaining = quantaRemaining / quantaPerBlockFloat;
        if (remaining > 1) remaining = 1.0f;
        return remaining * (density > 0 ? 1 : -1);
    }
}
