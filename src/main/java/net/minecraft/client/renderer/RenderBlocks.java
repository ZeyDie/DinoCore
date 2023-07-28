package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.src.FMLRenderAccessLibrary;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import static net.minecraftforge.common.ForgeDirection.*;

@SideOnly(Side.CLIENT)
public class RenderBlocks
{
    /** The IBlockAccess used by this instance of RenderBlocks */
    public IBlockAccess blockAccess;

    /**
     * If set to >=0, all block faces will be rendered using this texture index
     */
    public Icon overrideBlockTexture;

    /**
     * Set to true if the texture should be flipped horizontally during render*Face
     */
    public boolean flipTexture;

    /**
     * If true, renders all faces on all blocks rather than using the logic in Block.shouldSideBeRendered.  Unused.
     */
    public boolean renderAllFaces;

    /** Fancy grass side matching biome */
    public static boolean fancyGrass = true;
    public boolean useInventoryTint = true;

    /** The minimum X value for rendering (default 0.0). */
    public double renderMinX;

    /** The maximum X value for rendering (default 1.0). */
    public double renderMaxX;

    /** The minimum Y value for rendering (default 0.0). */
    public double renderMinY;

    /** The maximum Y value for rendering (default 1.0). */
    public double renderMaxY;

    /** The minimum Z value for rendering (default 0.0). */
    public double renderMinZ;

    /** The maximum Z value for rendering (default 1.0). */
    public double renderMaxZ;

    /**
     * Set by overrideBlockBounds, to keep this class from changing the visual bounding box.
     */
    public boolean lockBlockBounds;
    public boolean partialRenderBounds;
    public final Minecraft minecraftRB;
    public int uvRotateEast;
    public int uvRotateWest;
    public int uvRotateSouth;
    public int uvRotateNorth;
    public int uvRotateTop;
    public int uvRotateBottom;

    /** Whether ambient occlusion is enabled or not */
    public boolean enableAO;

    /**
     * Used as a scratch variable for ambient occlusion on the north/bottom/east corner.
     */
    public float aoLightValueScratchXYZNNN;

    /**
     * Used as a scratch variable for ambient occlusion between the bottom face and the north face.
     */
    public float aoLightValueScratchXYNN;

    /**
     * Used as a scratch variable for ambient occlusion on the north/bottom/west corner.
     */
    public float aoLightValueScratchXYZNNP;

    /**
     * Used as a scratch variable for ambient occlusion between the bottom face and the east face.
     */
    public float aoLightValueScratchYZNN;

    /**
     * Used as a scratch variable for ambient occlusion between the bottom face and the west face.
     */
    public float aoLightValueScratchYZNP;

    /**
     * Used as a scratch variable for ambient occlusion on the south/bottom/east corner.
     */
    public float aoLightValueScratchXYZPNN;

    /**
     * Used as a scratch variable for ambient occlusion between the bottom face and the south face.
     */
    public float aoLightValueScratchXYPN;

    /**
     * Used as a scratch variable for ambient occlusion on the south/bottom/west corner.
     */
    public float aoLightValueScratchXYZPNP;

    /**
     * Used as a scratch variable for ambient occlusion on the north/top/east corner.
     */
    public float aoLightValueScratchXYZNPN;

    /**
     * Used as a scratch variable for ambient occlusion between the top face and the north face.
     */
    public float aoLightValueScratchXYNP;

    /**
     * Used as a scratch variable for ambient occlusion on the north/top/west corner.
     */
    public float aoLightValueScratchXYZNPP;

    /**
     * Used as a scratch variable for ambient occlusion between the top face and the east face.
     */
    public float aoLightValueScratchYZPN;

    /**
     * Used as a scratch variable for ambient occlusion on the south/top/east corner.
     */
    public float aoLightValueScratchXYZPPN;

    /**
     * Used as a scratch variable for ambient occlusion between the top face and the south face.
     */
    public float aoLightValueScratchXYPP;

    /**
     * Used as a scratch variable for ambient occlusion between the top face and the west face.
     */
    public float aoLightValueScratchYZPP;

    /**
     * Used as a scratch variable for ambient occlusion on the south/top/west corner.
     */
    public float aoLightValueScratchXYZPPP;

    /**
     * Used as a scratch variable for ambient occlusion between the north face and the east face.
     */
    public float aoLightValueScratchXZNN;

    /**
     * Used as a scratch variable for ambient occlusion between the south face and the east face.
     */
    public float aoLightValueScratchXZPN;

    /**
     * Used as a scratch variable for ambient occlusion between the north face and the west face.
     */
    public float aoLightValueScratchXZNP;

    /**
     * Used as a scratch variable for ambient occlusion between the south face and the west face.
     */
    public float aoLightValueScratchXZPP;

    /** Ambient occlusion brightness XYZNNN */
    public int aoBrightnessXYZNNN;

    /** Ambient occlusion brightness XYNN */
    public int aoBrightnessXYNN;

    /** Ambient occlusion brightness XYZNNP */
    public int aoBrightnessXYZNNP;

    /** Ambient occlusion brightness YZNN */
    public int aoBrightnessYZNN;

    /** Ambient occlusion brightness YZNP */
    public int aoBrightnessYZNP;

    /** Ambient occlusion brightness XYZPNN */
    public int aoBrightnessXYZPNN;

    /** Ambient occlusion brightness XYPN */
    public int aoBrightnessXYPN;

    /** Ambient occlusion brightness XYZPNP */
    public int aoBrightnessXYZPNP;

    /** Ambient occlusion brightness XYZNPN */
    public int aoBrightnessXYZNPN;

    /** Ambient occlusion brightness XYNP */
    public int aoBrightnessXYNP;

    /** Ambient occlusion brightness XYZNPP */
    public int aoBrightnessXYZNPP;

    /** Ambient occlusion brightness YZPN */
    public int aoBrightnessYZPN;

    /** Ambient occlusion brightness XYZPPN */
    public int aoBrightnessXYZPPN;

    /** Ambient occlusion brightness XYPP */
    public int aoBrightnessXYPP;

    /** Ambient occlusion brightness YZPP */
    public int aoBrightnessYZPP;

    /** Ambient occlusion brightness XYZPPP */
    public int aoBrightnessXYZPPP;

    /** Ambient occlusion brightness XZNN */
    public int aoBrightnessXZNN;

    /** Ambient occlusion brightness XZPN */
    public int aoBrightnessXZPN;

    /** Ambient occlusion brightness XZNP */
    public int aoBrightnessXZNP;

    /** Ambient occlusion brightness XZPP */
    public int aoBrightnessXZPP;

    /** Brightness top left */
    public int brightnessTopLeft;

    /** Brightness bottom left */
    public int brightnessBottomLeft;

    /** Brightness bottom right */
    public int brightnessBottomRight;

    /** Brightness top right */
    public int brightnessTopRight;

    /** Red color value for the top left corner */
    public float colorRedTopLeft;

    /** Red color value for the bottom left corner */
    public float colorRedBottomLeft;

    /** Red color value for the bottom right corner */
    public float colorRedBottomRight;

    /** Red color value for the top right corner */
    public float colorRedTopRight;

    /** Green color value for the top left corner */
    public float colorGreenTopLeft;

    /** Green color value for the bottom left corner */
    public float colorGreenBottomLeft;

    /** Green color value for the bottom right corner */
    public float colorGreenBottomRight;

    /** Green color value for the top right corner */
    public float colorGreenTopRight;

    /** Blue color value for the top left corner */
    public float colorBlueTopLeft;

    /** Blue color value for the bottom left corner */
    public float colorBlueBottomLeft;

    /** Blue color value for the bottom right corner */
    public float colorBlueBottomRight;

    /** Blue color value for the top right corner */
    public float colorBlueTopRight;

    public RenderBlocks(final IBlockAccess par1IBlockAccess)
    {
        this.blockAccess = par1IBlockAccess;
        this.minecraftRB = Minecraft.getMinecraft();
    }

    public RenderBlocks()
    {
        this.minecraftRB = Minecraft.getMinecraft();
    }

    /**
     * Sets overrideBlockTexture
     */
    public void setOverrideBlockTexture(final Icon par1Icon)
    {
        this.overrideBlockTexture = par1Icon;
    }

    /**
     * Clear override block texture
     */
    public void clearOverrideBlockTexture()
    {
        this.overrideBlockTexture = null;
    }

    public boolean hasOverrideBlockTexture()
    {
        return this.overrideBlockTexture != null;
    }

    /**
     * Sets the bounding box for the block to draw in, e.g. 0.25-0.75 on all axes for a half-size, centered block.
     */
    public void setRenderBounds(final double par1, final double par3, final double par5, final double par7, final double par9, final double par11)
    {
        if (!this.lockBlockBounds)
        {
            this.renderMinX = par1;
            this.renderMaxX = par7;
            this.renderMinY = par3;
            this.renderMaxY = par9;
            this.renderMinZ = par5;
            this.renderMaxZ = par11;
            this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2 && (this.renderMinX > 0.0D || this.renderMaxX < 1.0D || this.renderMinY > 0.0D || this.renderMaxY < 1.0D || this.renderMinZ > 0.0D || this.renderMaxZ < 1.0D);
        }
    }

    /**
     * Like setRenderBounds, but automatically pulling the bounds from the given Block.
     */
    public void setRenderBoundsFromBlock(final Block par1Block)
    {
        if (!this.lockBlockBounds)
        {
            this.renderMinX = par1Block.getBlockBoundsMinX();
            this.renderMaxX = par1Block.getBlockBoundsMaxX();
            this.renderMinY = par1Block.getBlockBoundsMinY();
            this.renderMaxY = par1Block.getBlockBoundsMaxY();
            this.renderMinZ = par1Block.getBlockBoundsMinZ();
            this.renderMaxZ = par1Block.getBlockBoundsMaxZ();
            this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2 && (this.renderMinX > 0.0D || this.renderMaxX < 1.0D || this.renderMinY > 0.0D || this.renderMaxY < 1.0D || this.renderMinZ > 0.0D || this.renderMaxZ < 1.0D);
        }
    }

    /**
     * Like setRenderBounds, but locks the values so that RenderBlocks won't change them.  If you use this, you must
     * call unlockBlockBounds after you finish rendering!
     */
    public void overrideBlockBounds(final double par1, final double par3, final double par5, final double par7, final double par9, final double par11)
    {
        this.renderMinX = par1;
        this.renderMaxX = par7;
        this.renderMinY = par3;
        this.renderMaxY = par9;
        this.renderMinZ = par5;
        this.renderMaxZ = par11;
        this.lockBlockBounds = true;
        this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2 && (this.renderMinX > 0.0D || this.renderMaxX < 1.0D || this.renderMinY > 0.0D || this.renderMaxY < 1.0D || this.renderMinZ > 0.0D || this.renderMaxZ < 1.0D);
    }

    /**
     * Unlocks the visual bounding box so that RenderBlocks can change it again.
     */
    public void unlockBlockBounds()
    {
        this.lockBlockBounds = false;
    }

    /**
     * Renders a block using the given texture instead of the block's own default texture
     */
    public void renderBlockUsingTexture(final Block par1Block, final int par2, final int par3, final int par4, final Icon par5Icon)
    {
        this.setOverrideBlockTexture(par5Icon);
        this.renderBlockByRenderType(par1Block, par2, par3, par4);
        this.clearOverrideBlockTexture();
    }

    /**
     * Render all faces of a block
     */
    public void renderBlockAllFaces(final Block par1Block, final int par2, final int par3, final int par4)
    {
        this.renderAllFaces = true;
        this.renderBlockByRenderType(par1Block, par2, par3, par4);
        this.renderAllFaces = false;
    }

    /**
     * Renders the block at the given coordinates using the block's rendering type
     */
    public boolean renderBlockByRenderType(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final int l = par1Block.getRenderType();

        if (l == -1)
        {
            return false;
        }
        else
        {
            par1Block.setBlockBoundsBasedOnState(this.blockAccess, par2, par3, par4);
            this.setRenderBoundsFromBlock(par1Block);

            switch (l)
            {
                //regex: ' : \(l == ([\d]+) \?' replace: ';\ncase \1: return' ::: IMPORTANT: REMEMBER THIS ON FIRST line!
                case 0: return this.renderStandardBlock(par1Block, par2, par3, par4);
                case 4: return this.renderBlockFluids(par1Block, par2, par3, par4);
                case 31: return this.renderBlockLog(par1Block, par2, par3, par4);
                case 1: return this.renderCrossedSquares(par1Block, par2, par3, par4);
                case 2: return this.renderBlockTorch(par1Block, par2, par3, par4);
                case 20: return this.renderBlockVine(par1Block, par2, par3, par4);
                case 11: return this.renderBlockFence((BlockFence)par1Block, par2, par3, par4);
                case 39: return this.renderBlockQuartz(par1Block, par2, par3, par4);
                case 5: return this.renderBlockRedstoneWire(par1Block, par2, par3, par4);
                case 13: return this.renderBlockCactus(par1Block, par2, par3, par4);
                case 9: return this.renderBlockMinecartTrack((BlockRailBase)par1Block, par2, par3, par4);
                case 19: return this.renderBlockStem(par1Block, par2, par3, par4);
                case 23: return this.renderBlockLilyPad(par1Block, par2, par3, par4);
                case 6: return this.renderBlockCrops(par1Block, par2, par3, par4);
                case 3: return this.renderBlockFire((BlockFire)par1Block, par2, par3, par4);
                case 8: return this.renderBlockLadder(par1Block, par2, par3, par4);
                case 7: return this.renderBlockDoor(par1Block, par2, par3, par4);
                case 10: return this.renderBlockStairs((BlockStairs)par1Block, par2, par3, par4);
                case 27: return this.renderBlockDragonEgg((BlockDragonEgg)par1Block, par2, par3, par4);
                case 32: return this.renderBlockWall((BlockWall)par1Block, par2, par3, par4);
                case 12: return this.renderBlockLever(par1Block, par2, par3, par4);
                case 29: return this.renderBlockTripWireSource(par1Block, par2, par3, par4);
                case 30: return this.renderBlockTripWire(par1Block, par2, par3, par4);
                case 14: return this.renderBlockBed(par1Block, par2, par3, par4);
                case 15: return this.renderBlockRepeater((BlockRedstoneRepeater)par1Block, par2, par3, par4);
                case 36: return this.renderBlockRedstoneLogic((BlockRedstoneLogic)par1Block, par2, par3, par4);
                case 37: return this.renderBlockComparator((BlockComparator)par1Block, par2, par3, par4);
                case 16: return this.renderPistonBase(par1Block, par2, par3, par4, false);
                case 17: return this.renderPistonExtension(par1Block, par2, par3, par4, true);
                case 18: return this.renderBlockPane((BlockPane)par1Block, par2, par3, par4);
                case 21: return this.renderBlockFenceGate((BlockFenceGate)par1Block, par2, par3, par4);
                case 24: return this.renderBlockCauldron((BlockCauldron)par1Block, par2, par3, par4);
                case 33: return this.renderBlockFlowerpot((BlockFlowerPot)par1Block, par2, par3, par4);
                case 35: return this.renderBlockAnvil((BlockAnvil)par1Block, par2, par3, par4);
                case 25: return this.renderBlockBrewingStand((BlockBrewingStand)par1Block, par2, par3, par4);
                case 26: return this.renderBlockEndPortalFrame((BlockEndPortalFrame)par1Block, par2, par3, par4);
                case 28: return this.renderBlockCocoa((BlockCocoa)par1Block, par2, par3, par4);
                case 34: return this.renderBlockBeacon((BlockBeacon)par1Block, par2, par3, par4);
                case 38: return this.renderBlockHopper((BlockHopper)par1Block, par2, par3, par4);
                default: return FMLRenderAccessLibrary.renderWorldBlock(this, blockAccess, par2, par3, par4, par1Block, l);
            }
        }
    }

    /**
     * Render BlockEndPortalFrame
     */
    public boolean renderBlockEndPortalFrame(final BlockEndPortalFrame par1BlockEndPortalFrame, final int par2, final int par3, final int par4)
    {
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final int i1 = l & 3;

        if (i1 == 0)
        {
            this.uvRotateTop = 3;
        }
        else if (i1 == 3)
        {
            this.uvRotateTop = 1;
        }
        else if (i1 == 1)
        {
            this.uvRotateTop = 2;
        }

        if (!BlockEndPortalFrame.isEnderEyeInserted(l))
        {
            this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.8125D, 1.0D);
            this.renderStandardBlock(par1BlockEndPortalFrame, par2, par3, par4);
            this.uvRotateTop = 0;
            return true;
        }
        else
        {
            this.renderAllFaces = true;
            this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.8125D, 1.0D);
            this.renderStandardBlock(par1BlockEndPortalFrame, par2, par3, par4);
            this.setOverrideBlockTexture(par1BlockEndPortalFrame.func_94398_p());
            this.setRenderBounds(0.25D, 0.8125D, 0.25D, 0.75D, 1.0D, 0.75D);
            this.renderStandardBlock(par1BlockEndPortalFrame, par2, par3, par4);
            this.renderAllFaces = false;
            this.clearOverrideBlockTexture();
            this.uvRotateTop = 0;
            return true;
        }
    }

    /**
     * render a bed at the given coordinates
     */
    public boolean renderBlockBed(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        final int i1 = par1Block.getBedDirection(blockAccess, par2, par3, par4);
        final boolean flag = par1Block.isBedFoot(blockAccess, par2, par3, par4);
        final float f = 0.5F;
        final float f1 = 1.0F;
        final float f2 = 0.8F;
        final float f3 = 0.6F;
        final int j1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
        tessellator.setBrightness(j1);
        tessellator.setColorOpaque_F(f, f, f);
        Icon icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0);
        if (hasOverrideBlockTexture()) icon = overrideBlockTexture; //BugFix Proper breaking texture on underside
        double d0 = (double)icon.getMinU();
        double d1 = (double)icon.getMaxU();
        double d2 = (double)icon.getMinV();
        double d3 = (double)icon.getMaxV();
        double d4 = (double)par2 + this.renderMinX;
        double d5 = (double)par2 + this.renderMaxX;
        double d6 = (double)par3 + this.renderMinY + 0.1875D;
        double d7 = (double)par4 + this.renderMinZ;
        double d8 = (double)par4 + this.renderMaxZ;
        tessellator.addVertexWithUV(d4, d6, d8, d0, d3);
        tessellator.addVertexWithUV(d4, d6, d7, d0, d2);
        tessellator.addVertexWithUV(d5, d6, d7, d1, d2);
        tessellator.addVertexWithUV(d5, d6, d8, d1, d3);
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
        tessellator.setColorOpaque_F(f1, f1, f1);
        icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1);
        if (hasOverrideBlockTexture()) icon = overrideBlockTexture; //BugFix Proper breaking texture on underside
        d0 = (double)icon.getMinU();
        d1 = (double)icon.getMaxU();
        d2 = (double)icon.getMinV();
        d3 = (double)icon.getMaxV();
        d4 = d0;
        d5 = d1;
        d6 = d2;
        d7 = d2;
        d8 = d0;
        double d9 = d1;
        double d10 = d3;
        double d11 = d3;

        if (i1 == 0)
        {
            d5 = d0;
            d6 = d3;
            d8 = d1;
            d11 = d2;
        }
        else if (i1 == 2)
        {
            d4 = d1;
            d7 = d3;
            d9 = d0;
            d10 = d2;
        }
        else if (i1 == 3)
        {
            d4 = d1;
            d7 = d3;
            d9 = d0;
            d10 = d2;
            d5 = d0;
            d6 = d3;
            d8 = d1;
            d11 = d2;
        }

        final double d12 = (double)par2 + this.renderMinX;
        final double d13 = (double)par2 + this.renderMaxX;
        final double d14 = (double)par3 + this.renderMaxY;
        final double d15 = (double)par4 + this.renderMinZ;
        final double d16 = (double)par4 + this.renderMaxZ;
        tessellator.addVertexWithUV(d13, d14, d16, d8, d10);
        tessellator.addVertexWithUV(d13, d14, d15, d4, d6);
        tessellator.addVertexWithUV(d12, d14, d15, d5, d7);
        tessellator.addVertexWithUV(d12, d14, d16, d9, d11);
        int k1 = Direction.directionToFacing[i1];

        if (flag)
        {
            k1 = Direction.directionToFacing[Direction.rotateOpposite[i1]];
        }

        byte b0 = 4;

        switch (i1)
        {
            case 0:
                b0 = 5;
                break;
            case 1:
                b0 = 3;
            case 2:
            default:
                break;
            case 3:
                b0 = 2;
        }

        if (k1 != 2 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 - 1, 2)))
        {
            tessellator.setBrightness(this.renderMinZ > 0.0D ? j1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1));
            tessellator.setColorOpaque_F(f2, f2, f2);
            this.flipTexture = b0 == 2;
            this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2));
        }

        if (k1 != 3 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 + 1, 3)))
        {
            tessellator.setBrightness(this.renderMaxZ < 1.0D ? j1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1));
            tessellator.setColorOpaque_F(f2, f2, f2);
            this.flipTexture = b0 == 3;
            this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3));
        }

        if (k1 != 4 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 - 1, par3, par4, 4)))
        {
            tessellator.setBrightness(this.renderMinZ > 0.0D ? j1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4));
            tessellator.setColorOpaque_F(f3, f3, f3);
            this.flipTexture = b0 == 4;
            this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4));
        }

        if (k1 != 5 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 + 1, par3, par4, 5)))
        {
            tessellator.setBrightness(this.renderMaxZ < 1.0D ? j1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4));
            tessellator.setColorOpaque_F(f3, f3, f3);
            this.flipTexture = b0 == 5;
            this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5));
        }

        this.flipTexture = false;
        return true;
    }

    /**
     * Render BlockBrewingStand
     */
    public boolean renderBlockBrewingStand(final BlockBrewingStand par1BlockBrewingStand, final int par2, final int par3, final int par4)
    {
        this.setRenderBounds(0.4375D, 0.0D, 0.4375D, 0.5625D, 0.875D, 0.5625D);
        this.renderStandardBlock(par1BlockBrewingStand, par2, par3, par4);
        this.setOverrideBlockTexture(par1BlockBrewingStand.getBrewingStandIcon());
        this.renderAllFaces = true;
        this.setRenderBounds(0.5625D, 0.0D, 0.3125D, 0.9375D, 0.125D, 0.6875D);
        this.renderStandardBlock(par1BlockBrewingStand, par2, par3, par4);
        this.setRenderBounds(0.125D, 0.0D, 0.0625D, 0.5D, 0.125D, 0.4375D);
        this.renderStandardBlock(par1BlockBrewingStand, par2, par3, par4);
        this.setRenderBounds(0.125D, 0.0D, 0.5625D, 0.5D, 0.125D, 0.9375D);
        this.renderStandardBlock(par1BlockBrewingStand, par2, par3, par4);
        this.renderAllFaces = false;
        this.clearOverrideBlockTexture();
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1BlockBrewingStand.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = 1.0F;
        final int l = par1BlockBrewingStand.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f1 = (float)(l >> 16 & 255) / 255.0F;
        float f2 = (float)(l >> 8 & 255) / 255.0F;
        float f3 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            final float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            final float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            final float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        Icon icon = this.getBlockIconFromSideAndMetadata(par1BlockBrewingStand, 0, 0);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        final double d0 = (double)icon.getMinV();
        final double d1 = (double)icon.getMaxV();
        final int i1 = this.blockAccess.getBlockMetadata(par2, par3, par4);

        for (int j1 = 0; j1 < 3; ++j1)
        {
            final double d2 = (double)j1 * Math.PI * 2.0D / 3.0D + (Math.PI / 2.0D);
            final double d3 = (double)icon.getInterpolatedU(8.0D);
            double d4 = (double)icon.getMaxU();

            if ((i1 & 1 << j1) != 0)
            {
                d4 = (double)icon.getMinU();
            }

            final double d5 = (double)par2 + 0.5D;
            final double d6 = (double)par2 + 0.5D + Math.sin(d2) * 8.0D / 16.0D;
            final double d7 = (double)par4 + 0.5D;
            final double d8 = (double)par4 + 0.5D + Math.cos(d2) * 8.0D / 16.0D;
            tessellator.addVertexWithUV(d5, (double)(par3 + 1), d7, d3, d0);
            tessellator.addVertexWithUV(d5, (double)(par3 + 0), d7, d3, d1);
            tessellator.addVertexWithUV(d6, (double)(par3 + 0), d8, d4, d1);
            tessellator.addVertexWithUV(d6, (double)(par3 + 1), d8, d4, d0);
            tessellator.addVertexWithUV(d6, (double)(par3 + 1), d8, d4, d0);
            tessellator.addVertexWithUV(d6, (double)(par3 + 0), d8, d4, d1);
            tessellator.addVertexWithUV(d5, (double)(par3 + 0), d7, d3, d1);
            tessellator.addVertexWithUV(d5, (double)(par3 + 1), d7, d3, d0);
        }

        par1BlockBrewingStand.setBlockBoundsForItemRender();
        return true;
    }

    /**
     * Render block cauldron
     */
    public boolean renderBlockCauldron(final BlockCauldron par1BlockCauldron, final int par2, final int par3, final int par4)
    {
        this.renderStandardBlock(par1BlockCauldron, par2, par3, par4);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1BlockCauldron.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = 1.0F;
        final int l = par1BlockCauldron.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f1 = (float)(l >> 16 & 255) / 255.0F;
        float f2 = (float)(l >> 8 & 255) / 255.0F;
        float f3 = (float)(l & 255) / 255.0F;
        float f4;

        if (EntityRenderer.anaglyphEnable)
        {
            final float f5 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            f4 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            final float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f5;
            f2 = f4;
            f3 = f6;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        final Icon icon = par1BlockCauldron.getBlockTextureFromSide(2);
        f4 = 0.125F;
        this.renderFaceXPos(par1BlockCauldron, (double)((float)par2 - 1.0F + f4), (double)par3, (double)par4, icon);
        this.renderFaceXNeg(par1BlockCauldron, (double)((float)par2 + 1.0F - f4), (double)par3, (double)par4, icon);
        this.renderFaceZPos(par1BlockCauldron, (double)par2, (double)par3, (double)((float)par4 - 1.0F + f4), icon);
        this.renderFaceZNeg(par1BlockCauldron, (double)par2, (double)par3, (double)((float)par4 + 1.0F - f4), icon);
        final Icon icon1 = BlockCauldron.getCauldronIcon("inner");
        this.renderFaceYPos(par1BlockCauldron, (double)par2, (double)((float)par3 - 1.0F + 0.25F), (double)par4, icon1);
        this.renderFaceYNeg(par1BlockCauldron, (double)par2, (double)((float)par3 + 1.0F - 0.75F), (double)par4, icon1);
        int i1 = this.blockAccess.getBlockMetadata(par2, par3, par4);

        if (i1 > 0)
        {
            final Icon icon2 = BlockFluid.getFluidIcon("water_still");

            if (i1 > 3)
            {
                i1 = 3;
            }

            this.renderFaceYPos(par1BlockCauldron, (double)par2, (double)((float)par3 - 1.0F + (6.0F + (float)i1 * 3.0F) / 16.0F), (double)par4, icon2);
        }

        return true;
    }

    /**
     * Renders flower pot
     */
    public boolean renderBlockFlowerpot(final BlockFlowerPot par1BlockFlowerPot, final int par2, final int par3, final int par4)
    {
        this.renderStandardBlock(par1BlockFlowerPot, par2, par3, par4);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1BlockFlowerPot.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = 1.0F;
        int l = par1BlockFlowerPot.colorMultiplier(this.blockAccess, par2, par3, par4);
        final Icon icon = this.getBlockIconFromSide(par1BlockFlowerPot, 0);
        float f1 = (float)(l >> 16 & 255) / 255.0F;
        float f2 = (float)(l >> 8 & 255) / 255.0F;
        float f3 = (float)(l & 255) / 255.0F;
        float f4;
        float f5;

        if (EntityRenderer.anaglyphEnable)
        {
            f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            final float f6 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            f5 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f6;
            f3 = f5;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        f4 = 0.1865F;
        this.renderFaceXPos(par1BlockFlowerPot, (double)((float)par2 - 0.5F + f4), (double)par3, (double)par4, icon);
        this.renderFaceXNeg(par1BlockFlowerPot, (double)((float)par2 + 0.5F - f4), (double)par3, (double)par4, icon);
        this.renderFaceZPos(par1BlockFlowerPot, (double)par2, (double)par3, (double)((float)par4 - 0.5F + f4), icon);
        this.renderFaceZNeg(par1BlockFlowerPot, (double)par2, (double)par3, (double)((float)par4 + 0.5F - f4), icon);
        this.renderFaceYPos(par1BlockFlowerPot, (double)par2, (double)((float)par3 - 0.5F + f4 + 0.1875F), (double)par4, this.getBlockIcon(Block.dirt));
        final int i1 = this.blockAccess.getBlockMetadata(par2, par3, par4);

        if (i1 != 0)
        {
            f5 = 0.0F;
            final float f7 = 4.0F;
            final float f8 = 0.0F;
            BlockFlower blockflower = null;

            switch (i1)
            {
                case 1:
                    blockflower = Block.plantRed;
                    break;
                case 2:
                    blockflower = Block.plantYellow;
                case 3:
                case 4:
                case 5:
                case 6:
                default:
                    break;
                case 7:
                    blockflower = Block.mushroomRed;
                    break;
                case 8:
                    blockflower = Block.mushroomBrown;
            }

            tessellator.addTranslation(f5 / 16.0F, f7 / 16.0F, f8 / 16.0F);

            if (blockflower != null)
            {
                this.renderBlockByRenderType(blockflower, par2, par3, par4);
            }
            else if (i1 == 9)
            {
                this.renderAllFaces = true;
                final float f9 = 0.125F;
                this.setRenderBounds((double)(0.5F - f9), 0.0D, (double)(0.5F - f9), (double)(0.5F + f9), 0.25D, (double)(0.5F + f9));
                this.renderStandardBlock(Block.cactus, par2, par3, par4);
                this.setRenderBounds((double)(0.5F - f9), 0.25D, (double)(0.5F - f9), (double)(0.5F + f9), 0.5D, (double)(0.5F + f9));
                this.renderStandardBlock(Block.cactus, par2, par3, par4);
                this.setRenderBounds((double)(0.5F - f9), 0.5D, (double)(0.5F - f9), (double)(0.5F + f9), 0.75D, (double)(0.5F + f9));
                this.renderStandardBlock(Block.cactus, par2, par3, par4);
                this.renderAllFaces = false;
                this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            }
            else if (i1 == 3)
            {
                this.drawCrossedSquares(Block.sapling, 0, (double)par2, (double)par3, (double)par4, 0.75F);
            }
            else if (i1 == 5)
            {
                this.drawCrossedSquares(Block.sapling, 2, (double)par2, (double)par3, (double)par4, 0.75F);
            }
            else if (i1 == 4)
            {
                this.drawCrossedSquares(Block.sapling, 1, (double)par2, (double)par3, (double)par4, 0.75F);
            }
            else if (i1 == 6)
            {
                this.drawCrossedSquares(Block.sapling, 3, (double)par2, (double)par3, (double)par4, 0.75F);
            }
            else if (i1 == 11)
            {
                l = Block.tallGrass.colorMultiplier(this.blockAccess, par2, par3, par4);
                f1 = (float)(l >> 16 & 255) / 255.0F;
                f2 = (float)(l >> 8 & 255) / 255.0F;
                f3 = (float)(l & 255) / 255.0F;
                tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
                this.drawCrossedSquares(Block.tallGrass, 2, (double)par2, (double)par3, (double)par4, 0.75F);
            }
            else if (i1 == 10)
            {
                this.drawCrossedSquares(Block.deadBush, 2, (double)par2, (double)par3, (double)par4, 0.75F);
            }

            tessellator.addTranslation(-f5 / 16.0F, -f7 / 16.0F, -f8 / 16.0F);
        }

        return true;
    }

    /**
     * Renders anvil
     */
    public boolean renderBlockAnvil(final BlockAnvil par1BlockAnvil, final int par2, final int par3, final int par4)
    {
        return this.renderBlockAnvilMetadata(par1BlockAnvil, par2, par3, par4, this.blockAccess.getBlockMetadata(par2, par3, par4));
    }

    /**
     * Renders anvil block with metadata
     */
    public boolean renderBlockAnvilMetadata(final BlockAnvil par1BlockAnvil, final int par2, final int par3, final int par4, final int par5)
    {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1BlockAnvil.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = 1.0F;
        final int i1 = par1BlockAnvil.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f1 = (float)(i1 >> 16 & 255) / 255.0F;
        float f2 = (float)(i1 >> 8 & 255) / 255.0F;
        float f3 = (float)(i1 & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            final float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            final float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            final float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        return this.renderBlockAnvilOrient(par1BlockAnvil, par2, par3, par4, par5, false);
    }

    /**
     * Renders anvil block with orientation
     */
    public boolean renderBlockAnvilOrient(final BlockAnvil par1BlockAnvil, final int par2, final int par3, final int par4, final int par5, final boolean par6)
    {
        final int i1 = par6 ? 0 : par5 & 3;
        boolean flag1 = false;
        float f = 0.0F;

        switch (i1)
        {
            case 0:
                this.uvRotateSouth = 2;
                this.uvRotateNorth = 1;
                this.uvRotateTop = 3;
                this.uvRotateBottom = 3;
                break;
            case 1:
                this.uvRotateEast = 1;
                this.uvRotateWest = 2;
                this.uvRotateTop = 2;
                this.uvRotateBottom = 1;
                flag1 = true;
                break;
            case 2:
                this.uvRotateSouth = 1;
                this.uvRotateNorth = 2;
                break;
            case 3:
                this.uvRotateEast = 2;
                this.uvRotateWest = 1;
                this.uvRotateTop = 1;
                this.uvRotateBottom = 2;
                flag1 = true;
        }

        f = this.renderBlockAnvilRotate(par1BlockAnvil, par2, par3, par4, 0, f, 0.75F, 0.25F, 0.75F, flag1, par6, par5);
        f = this.renderBlockAnvilRotate(par1BlockAnvil, par2, par3, par4, 1, f, 0.5F, 0.0625F, 0.625F, flag1, par6, par5);
        f = this.renderBlockAnvilRotate(par1BlockAnvil, par2, par3, par4, 2, f, 0.25F, 0.3125F, 0.5F, flag1, par6, par5);
        this.renderBlockAnvilRotate(par1BlockAnvil, par2, par3, par4, 3, f, 0.625F, 0.375F, 1.0F, flag1, par6, par5);
        this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        this.uvRotateEast = 0;
        this.uvRotateWest = 0;
        this.uvRotateSouth = 0;
        this.uvRotateNorth = 0;
        this.uvRotateTop = 0;
        this.uvRotateBottom = 0;
        return true;
    }

    /**
     * Renders anvil block with rotation
     */
    public float renderBlockAnvilRotate(final BlockAnvil par1BlockAnvil, final int par2, final int par3, final int par4, final int par5, final float par6, float par7, final float par8, float par9, final boolean par10, final boolean par11, final int par12)
    {
        float par71 = par7;
        float par91 = par9;
        if (par10)
        {
            final float f4 = par71;
            par71 = par91;
            par91 = f4;
        }

        par71 /= 2.0F;
        par91 /= 2.0F;
        par1BlockAnvil.field_82521_b = par5;
        this.setRenderBounds((double)(0.5F - par71), (double)par6, (double)(0.5F - par91), (double)(0.5F + par71), (double)(par6 + par8), (double)(0.5F + par91));

        if (par11)
        {
            final Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            this.renderFaceYNeg(par1BlockAnvil, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 0, par12));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(par1BlockAnvil, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 1, par12));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            this.renderFaceZNeg(par1BlockAnvil, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 2, par12));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(par1BlockAnvil, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 3, par12));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(par1BlockAnvil, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 4, par12));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(par1BlockAnvil, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockAnvil, 5, par12));
            tessellator.draw();
        }
        else
        {
            this.renderStandardBlock(par1BlockAnvil, par2, par3, par4);
        }

        return par6 + par8;
    }

    /**
     * Renders a torch block at the given coordinates
     */
    public boolean renderBlockTorch(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        final double d0 = 0.4000000059604645D;
        final double d1 = 0.5D - d0;
        final double d2 = 0.20000000298023224D;

        if (l == 1)
        {
            this.renderTorchAtAngle(par1Block, (double)par2 - d1, (double)par3 + d2, (double)par4, -d0, 0.0D, 0);
        }
        else if (l == 2)
        {
            this.renderTorchAtAngle(par1Block, (double)par2 + d1, (double)par3 + d2, (double)par4, d0, 0.0D, 0);
        }
        else if (l == 3)
        {
            this.renderTorchAtAngle(par1Block, (double)par2, (double)par3 + d2, (double)par4 - d1, 0.0D, -d0, 0);
        }
        else if (l == 4)
        {
            this.renderTorchAtAngle(par1Block, (double)par2, (double)par3 + d2, (double)par4 + d1, 0.0D, d0, 0);
        }
        else
        {
            this.renderTorchAtAngle(par1Block, (double)par2, (double)par3, (double)par4, 0.0D, 0.0D, 0);
        }

        return true;
    }

    /**
     * render a redstone repeater at the given coordinates
     */
    public boolean renderBlockRepeater(final BlockRedstoneRepeater par1BlockRedstoneRepeater, final int par2, final int par3, final int par4)
    {
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final int i1 = l & 3;
        final int j1 = (l & 12) >> 2;
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1BlockRedstoneRepeater.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        final double d0 = -0.1875D;
        final boolean flag = par1BlockRedstoneRepeater.func_94476_e(this.blockAccess, par2, par3, par4, l);
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;

        switch (i1)
        {
            case 0:
                d4 = -0.3125D;
                d2 = BlockRedstoneRepeater.repeaterTorchOffset[j1];
                break;
            case 1:
                d3 = 0.3125D;
                d1 = -BlockRedstoneRepeater.repeaterTorchOffset[j1];
                break;
            case 2:
                d4 = 0.3125D;
                d2 = -BlockRedstoneRepeater.repeaterTorchOffset[j1];
                break;
            case 3:
                d3 = -0.3125D;
                d1 = BlockRedstoneRepeater.repeaterTorchOffset[j1];
        }

        if (!flag)
        {
            this.renderTorchAtAngle(par1BlockRedstoneRepeater, (double)par2 + d1, (double)par3 + d0, (double)par4 + d2, 0.0D, 0.0D, 0);
        }
        else
        {
            final Icon icon = this.getBlockIcon(Block.bedrock);
            this.setOverrideBlockTexture(icon);
            float f = 2.0F;
            float f1 = 14.0F;
            float f2 = 7.0F;
            float f3 = 9.0F;

            switch (i1)
            {
                case 1:
                case 3:
                    f = 7.0F;
                    f1 = 9.0F;
                    f2 = 2.0F;
                    f3 = 14.0F;
                case 0:
                case 2:
                default:
                    this.setRenderBounds((double)(f / 16.0F + (float)d1), 0.125D, (double)(f2 / 16.0F + (float)d2), (double)(f1 / 16.0F + (float)d1), 0.25D, (double)(f3 / 16.0F + (float)d2));
                    final double d5 = (double)icon.getInterpolatedU((double)f);
                    final double d6 = (double)icon.getInterpolatedV((double)f2);
                    final double d7 = (double)icon.getInterpolatedU((double)f1);
                    final double d8 = (double)icon.getInterpolatedV((double)f3);
                    tessellator.addVertexWithUV((double)((float)par2 + f / 16.0F) + d1, (double)((float)par3 + 0.25F), (double)((float)par4 + f2 / 16.0F) + d2, d5, d6);
                    tessellator.addVertexWithUV((double)((float)par2 + f / 16.0F) + d1, (double)((float)par3 + 0.25F), (double)((float)par4 + f3 / 16.0F) + d2, d5, d8);
                    tessellator.addVertexWithUV((double)((float)par2 + f1 / 16.0F) + d1, (double)((float)par3 + 0.25F), (double)((float)par4 + f3 / 16.0F) + d2, d7, d8);
                    tessellator.addVertexWithUV((double)((float)par2 + f1 / 16.0F) + d1, (double)((float)par3 + 0.25F), (double)((float)par4 + f2 / 16.0F) + d2, d7, d6);
                    this.renderStandardBlock(par1BlockRedstoneRepeater, par2, par3, par4);
                    this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
                    this.clearOverrideBlockTexture();
            }
        }

        tessellator.setBrightness(par1BlockRedstoneRepeater.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        this.renderTorchAtAngle(par1BlockRedstoneRepeater, (double)par2 + d3, (double)par3 + d0, (double)par4 + d4, 0.0D, 0.0D, 0);
        this.renderBlockRedstoneLogic(par1BlockRedstoneRepeater, par2, par3, par4);
        return true;
    }

    public boolean renderBlockComparator(final BlockComparator par1BlockComparator, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1BlockComparator.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final int i1 = l & 3;
        double d0 = 0.0D;
        double d1 = -0.1875D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;
        final Icon icon;

        if (par1BlockComparator.func_94490_c(l))
        {
            icon = Block.torchRedstoneActive.getBlockTextureFromSide(0);
        }
        else
        {
            d1 -= 0.1875D;
            icon = Block.torchRedstoneIdle.getBlockTextureFromSide(0);
        }

        switch (i1)
        {
            case 0:
                d2 = -0.3125D;
                d4 = 1.0D;
                break;
            case 1:
                d0 = 0.3125D;
                d3 = -1.0D;
                break;
            case 2:
                d2 = 0.3125D;
                d4 = -1.0D;
                break;
            case 3:
                d0 = -0.3125D;
                d3 = 1.0D;
        }

        this.renderTorchAtAngle(par1BlockComparator, (double)par2 + 0.25D * d3 + 0.1875D * d4, (double)((float)par3 - 0.1875F), (double)par4 + 0.25D * d4 + 0.1875D * d3, 0.0D, 0.0D, l);
        this.renderTorchAtAngle(par1BlockComparator, (double)par2 + 0.25D * d3 + -0.1875D * d4, (double)((float)par3 - 0.1875F), (double)par4 + 0.25D * d4 + -0.1875D * d3, 0.0D, 0.0D, l);
        this.setOverrideBlockTexture(icon);
        this.renderTorchAtAngle(par1BlockComparator, (double)par2 + d0, (double)par3 + d1, (double)par4 + d2, 0.0D, 0.0D, l);
        this.clearOverrideBlockTexture();
        this.renderBlockRedstoneLogicMetadata(par1BlockComparator, par2, par3, par4, i1);
        return true;
    }

    public boolean renderBlockRedstoneLogic(final BlockRedstoneLogic par1BlockRedstoneLogic, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        this.renderBlockRedstoneLogicMetadata(par1BlockRedstoneLogic, par2, par3, par4, this.blockAccess.getBlockMetadata(par2, par3, par4) & 3);
        return true;
    }

    public void renderBlockRedstoneLogicMetadata(final BlockRedstoneLogic par1BlockRedstoneLogic, final int par2, final int par3, final int par4, final int par5)
    {
        this.renderStandardBlock(par1BlockRedstoneLogic, par2, par3, par4);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1BlockRedstoneLogic.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        final int i1 = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final Icon icon = this.getBlockIconFromSideAndMetadata(par1BlockRedstoneLogic, 1, i1);
        final double d0 = (double)icon.getMinU();
        final double d1 = (double)icon.getMaxU();
        final double d2 = (double)icon.getMinV();
        final double d3 = (double)icon.getMaxV();
        final double d4 = 0.125D;
        double d5 = (double)(par2 + 1);
        double d6 = (double)(par2 + 1);
        double d7 = (double)(par2 + 0);
        double d8 = (double)(par2 + 0);
        double d9 = (double)(par4 + 0);
        double d10 = (double)(par4 + 1);
        double d11 = (double)(par4 + 1);
        double d12 = (double)(par4 + 0);
        final double d13 = (double)par3 + d4;

        if (par5 == 2)
        {
            d5 = d6 = (double)(par2 + 0);
            d7 = d8 = (double)(par2 + 1);
            d9 = d12 = (double)(par4 + 1);
            d10 = d11 = (double)(par4 + 0);
        }
        else if (par5 == 3)
        {
            d5 = d8 = (double)(par2 + 0);
            d6 = d7 = (double)(par2 + 1);
            d9 = d10 = (double)(par4 + 0);
            d11 = d12 = (double)(par4 + 1);
        }
        else if (par5 == 1)
        {
            d5 = d8 = (double)(par2 + 1);
            d6 = d7 = (double)(par2 + 0);
            d9 = d10 = (double)(par4 + 1);
            d11 = d12 = (double)(par4 + 0);
        }

        tessellator.addVertexWithUV(d8, d13, d12, d0, d2);
        tessellator.addVertexWithUV(d7, d13, d11, d0, d3);
        tessellator.addVertexWithUV(d6, d13, d10, d1, d3);
        tessellator.addVertexWithUV(d5, d13, d9, d1, d2);
    }

    /**
     * Render all faces of the piston base
     */
    public void renderPistonBaseAllFaces(final Block par1Block, final int par2, final int par3, final int par4)
    {
        this.renderAllFaces = true;
        this.renderPistonBase(par1Block, par2, par3, par4, true);
        this.renderAllFaces = false;
    }

    /**
     * renders a block as a piston base
     */
    public boolean renderPistonBase(final Block par1Block, final int par2, final int par3, final int par4, final boolean par5)
    {
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final boolean flag1 = par5 || (l & 8) != 0;
        final int i1 = BlockPistonBase.getOrientation(l);
        final float f = 0.25F;

        if (flag1)
        {
            switch (i1)
            {
                case 0:
                    this.uvRotateEast = 3;
                    this.uvRotateWest = 3;
                    this.uvRotateSouth = 3;
                    this.uvRotateNorth = 3;
                    this.setRenderBounds(0.0D, 0.25D, 0.0D, 1.0D, 1.0D, 1.0D);
                    break;
                case 1:
                    this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
                    break;
                case 2:
                    this.uvRotateSouth = 1;
                    this.uvRotateNorth = 2;
                    this.setRenderBounds(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D);
                    break;
                case 3:
                    this.uvRotateSouth = 2;
                    this.uvRotateNorth = 1;
                    this.uvRotateTop = 3;
                    this.uvRotateBottom = 3;
                    this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D);
                    break;
                case 4:
                    this.uvRotateEast = 1;
                    this.uvRotateWest = 2;
                    this.uvRotateTop = 2;
                    this.uvRotateBottom = 1;
                    this.setRenderBounds(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                    break;
                case 5:
                    this.uvRotateEast = 2;
                    this.uvRotateWest = 1;
                    this.uvRotateTop = 1;
                    this.uvRotateBottom = 2;
                    this.setRenderBounds(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D);
            }

            ((BlockPistonBase)par1Block).func_96479_b((float)this.renderMinX, (float)this.renderMinY, (float)this.renderMinZ, (float)this.renderMaxX, (float)this.renderMaxY, (float)this.renderMaxZ);
            this.renderStandardBlock(par1Block, par2, par3, par4);
            this.uvRotateEast = 0;
            this.uvRotateWest = 0;
            this.uvRotateSouth = 0;
            this.uvRotateNorth = 0;
            this.uvRotateTop = 0;
            this.uvRotateBottom = 0;
            this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            ((BlockPistonBase)par1Block).func_96479_b((float)this.renderMinX, (float)this.renderMinY, (float)this.renderMinZ, (float)this.renderMaxX, (float)this.renderMaxY, (float)this.renderMaxZ);
        }
        else
        {
            switch (i1)
            {
                case 0:
                    this.uvRotateEast = 3;
                    this.uvRotateWest = 3;
                    this.uvRotateSouth = 3;
                    this.uvRotateNorth = 3;
                case 1:
                default:
                    break;
                case 2:
                    this.uvRotateSouth = 1;
                    this.uvRotateNorth = 2;
                    break;
                case 3:
                    this.uvRotateSouth = 2;
                    this.uvRotateNorth = 1;
                    this.uvRotateTop = 3;
                    this.uvRotateBottom = 3;
                    break;
                case 4:
                    this.uvRotateEast = 1;
                    this.uvRotateWest = 2;
                    this.uvRotateTop = 2;
                    this.uvRotateBottom = 1;
                    break;
                case 5:
                    this.uvRotateEast = 2;
                    this.uvRotateWest = 1;
                    this.uvRotateTop = 1;
                    this.uvRotateBottom = 2;
            }

            this.renderStandardBlock(par1Block, par2, par3, par4);
            this.uvRotateEast = 0;
            this.uvRotateWest = 0;
            this.uvRotateSouth = 0;
            this.uvRotateNorth = 0;
            this.uvRotateTop = 0;
            this.uvRotateBottom = 0;
        }

        return true;
    }

    /**
     * Render piston rod up/down
     */
    public void renderPistonRodUD(final double par1, final double par3, final double par5, final double par7, final double par9, final double par11, final float par13, final double par14)
    {
        Icon icon = BlockPistonBase.getPistonBaseIcon("piston_side");

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        final Tessellator tessellator = Tessellator.instance;
        final double d7 = (double)icon.getMinU();
        final double d8 = (double)icon.getMinV();
        final double d9 = (double)icon.getInterpolatedU(par14);
        final double d10 = (double)icon.getInterpolatedV(4.0D);
        tessellator.setColorOpaque_F(par13, par13, par13);
        tessellator.addVertexWithUV(par1, par7, par9, d9, d8);
        tessellator.addVertexWithUV(par1, par5, par9, d7, d8);
        tessellator.addVertexWithUV(par3, par5, par11, d7, d10);
        tessellator.addVertexWithUV(par3, par7, par11, d9, d10);
    }

    /**
     * Render piston rod south/north
     */
    public void renderPistonRodSN(final double par1, final double par3, final double par5, final double par7, final double par9, final double par11, final float par13, final double par14)
    {
        Icon icon = BlockPistonBase.getPistonBaseIcon("piston_side");

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        final Tessellator tessellator = Tessellator.instance;
        final double d7 = (double)icon.getMinU();
        final double d8 = (double)icon.getMinV();
        final double d9 = (double)icon.getInterpolatedU(par14);
        final double d10 = (double)icon.getInterpolatedV(4.0D);
        tessellator.setColorOpaque_F(par13, par13, par13);
        tessellator.addVertexWithUV(par1, par5, par11, d9, d8);
        tessellator.addVertexWithUV(par1, par5, par9, d7, d8);
        tessellator.addVertexWithUV(par3, par7, par9, d7, d10);
        tessellator.addVertexWithUV(par3, par7, par11, d9, d10);
    }

    /**
     * Render piston rod east/west
     */
    public void renderPistonRodEW(final double par1, final double par3, final double par5, final double par7, final double par9, final double par11, final float par13, final double par14)
    {
        Icon icon = BlockPistonBase.getPistonBaseIcon("piston_side");

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        final Tessellator tessellator = Tessellator.instance;
        final double d7 = (double)icon.getMinU();
        final double d8 = (double)icon.getMinV();
        final double d9 = (double)icon.getInterpolatedU(par14);
        final double d10 = (double)icon.getInterpolatedV(4.0D);
        tessellator.setColorOpaque_F(par13, par13, par13);
        tessellator.addVertexWithUV(par3, par5, par9, d9, d8);
        tessellator.addVertexWithUV(par1, par5, par9, d7, d8);
        tessellator.addVertexWithUV(par1, par7, par11, d7, d10);
        tessellator.addVertexWithUV(par3, par7, par11, d9, d10);
    }

    /**
     * Render all faces of the piston extension
     */
    public void renderPistonExtensionAllFaces(final Block par1Block, final int par2, final int par3, final int par4, final boolean par5)
    {
        this.renderAllFaces = true;
        this.renderPistonExtension(par1Block, par2, par3, par4, par5);
        this.renderAllFaces = false;
    }

    /**
     * renders the pushing part of a piston
     */
    public boolean renderPistonExtension(final Block par1Block, final int par2, final int par3, final int par4, final boolean par5)
    {
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final int i1 = BlockPistonExtension.getDirectionMeta(l);
        final float f = 0.25F;
        final float f1 = 0.375F;
        final float f2 = 0.625F;
        final float f3 = par1Block.getBlockBrightness(this.blockAccess, par2, par3, par4);
        final float f4 = par5 ? 1.0F : 0.5F;
        final double d0 = par5 ? 16.0D : 8.0D;

        switch (i1)
        {
            case 0:
                this.uvRotateEast = 3;
                this.uvRotateWest = 3;
                this.uvRotateSouth = 3;
                this.uvRotateNorth = 3;
                this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
                this.renderStandardBlock(par1Block, par2, par3, par4);
                this.renderPistonRodUD((double)((float)par2 + 0.375F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.25F), (double)((float)par3 + 0.25F + f4), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.625F), f3 * 0.8F, d0);
                this.renderPistonRodUD((double)((float)par2 + 0.625F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.25F), (double)((float)par3 + 0.25F + f4), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.375F), f3 * 0.8F, d0);
                this.renderPistonRodUD((double)((float)par2 + 0.375F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.25F), (double)((float)par3 + 0.25F + f4), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.625F), f3 * 0.6F, d0);
                this.renderPistonRodUD((double)((float)par2 + 0.625F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.25F), (double)((float)par3 + 0.25F + f4), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.375F), f3 * 0.6F, d0);
                break;
            case 1:
                this.setRenderBounds(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);
                this.renderStandardBlock(par1Block, par2, par3, par4);
                this.renderPistonRodUD((double)((float)par2 + 0.375F), (double)((float)par2 + 0.625F), (double)((float)par3 - 0.25F + 1.0F - f4), (double)((float)par3 - 0.25F + 1.0F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.625F), f3 * 0.8F, d0);
                this.renderPistonRodUD((double)((float)par2 + 0.625F), (double)((float)par2 + 0.375F), (double)((float)par3 - 0.25F + 1.0F - f4), (double)((float)par3 - 0.25F + 1.0F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.375F), f3 * 0.8F, d0);
                this.renderPistonRodUD((double)((float)par2 + 0.375F), (double)((float)par2 + 0.375F), (double)((float)par3 - 0.25F + 1.0F - f4), (double)((float)par3 - 0.25F + 1.0F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.625F), f3 * 0.6F, d0);
                this.renderPistonRodUD((double)((float)par2 + 0.625F), (double)((float)par2 + 0.625F), (double)((float)par3 - 0.25F + 1.0F - f4), (double)((float)par3 - 0.25F + 1.0F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.375F), f3 * 0.6F, d0);
                break;
            case 2:
                this.uvRotateSouth = 1;
                this.uvRotateNorth = 2;
                this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.25D);
                this.renderStandardBlock(par1Block, par2, par3, par4);
                this.renderPistonRodSN((double)((float)par2 + 0.375F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.25F), (double)((float)par4 + 0.25F + f4), f3 * 0.6F, d0);
                this.renderPistonRodSN((double)((float)par2 + 0.625F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.25F), (double)((float)par4 + 0.25F + f4), f3 * 0.6F, d0);
                this.renderPistonRodSN((double)((float)par2 + 0.375F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.25F), (double)((float)par4 + 0.25F + f4), f3 * 0.5F, d0);
                this.renderPistonRodSN((double)((float)par2 + 0.625F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.25F), (double)((float)par4 + 0.25F + f4), f3, d0);
                break;
            case 3:
                this.uvRotateSouth = 2;
                this.uvRotateNorth = 1;
                this.uvRotateTop = 3;
                this.uvRotateBottom = 3;
                this.setRenderBounds(0.0D, 0.0D, 0.75D, 1.0D, 1.0D, 1.0D);
                this.renderStandardBlock(par1Block, par2, par3, par4);
                this.renderPistonRodSN((double)((float)par2 + 0.375F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par4 - 0.25F + 1.0F - f4), (double)((float)par4 - 0.25F + 1.0F), f3 * 0.6F, d0);
                this.renderPistonRodSN((double)((float)par2 + 0.625F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par4 - 0.25F + 1.0F - f4), (double)((float)par4 - 0.25F + 1.0F), f3 * 0.6F, d0);
                this.renderPistonRodSN((double)((float)par2 + 0.375F), (double)((float)par2 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.375F), (double)((float)par4 - 0.25F + 1.0F - f4), (double)((float)par4 - 0.25F + 1.0F), f3 * 0.5F, d0);
                this.renderPistonRodSN((double)((float)par2 + 0.625F), (double)((float)par2 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.625F), (double)((float)par4 - 0.25F + 1.0F - f4), (double)((float)par4 - 0.25F + 1.0F), f3, d0);
                break;
            case 4:
                this.uvRotateEast = 1;
                this.uvRotateWest = 2;
                this.uvRotateTop = 2;
                this.uvRotateBottom = 1;
                this.setRenderBounds(0.0D, 0.0D, 0.0D, 0.25D, 1.0D, 1.0D);
                this.renderStandardBlock(par1Block, par2, par3, par4);
                this.renderPistonRodEW((double)((float)par2 + 0.25F), (double)((float)par2 + 0.25F + f4), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.375F), f3 * 0.5F, d0);
                this.renderPistonRodEW((double)((float)par2 + 0.25F), (double)((float)par2 + 0.25F + f4), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.625F), f3, d0);
                this.renderPistonRodEW((double)((float)par2 + 0.25F), (double)((float)par2 + 0.25F + f4), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.375F), f3 * 0.6F, d0);
                this.renderPistonRodEW((double)((float)par2 + 0.25F), (double)((float)par2 + 0.25F + f4), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.625F), f3 * 0.6F, d0);
                break;
            case 5:
                this.uvRotateEast = 2;
                this.uvRotateWest = 1;
                this.uvRotateTop = 1;
                this.uvRotateBottom = 2;
                this.setRenderBounds(0.75D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                this.renderStandardBlock(par1Block, par2, par3, par4);
                this.renderPistonRodEW((double)((float)par2 - 0.25F + 1.0F - f4), (double)((float)par2 - 0.25F + 1.0F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.375F), f3 * 0.5F, d0);
                this.renderPistonRodEW((double)((float)par2 - 0.25F + 1.0F - f4), (double)((float)par2 - 0.25F + 1.0F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.625F), f3, d0);
                this.renderPistonRodEW((double)((float)par2 - 0.25F + 1.0F - f4), (double)((float)par2 - 0.25F + 1.0F), (double)((float)par3 + 0.375F), (double)((float)par3 + 0.625F), (double)((float)par4 + 0.375F), (double)((float)par4 + 0.375F), f3 * 0.6F, d0);
                this.renderPistonRodEW((double)((float)par2 - 0.25F + 1.0F - f4), (double)((float)par2 - 0.25F + 1.0F), (double)((float)par3 + 0.625F), (double)((float)par3 + 0.375F), (double)((float)par4 + 0.625F), (double)((float)par4 + 0.625F), f3 * 0.6F, d0);
        }

        this.uvRotateEast = 0;
        this.uvRotateWest = 0;
        this.uvRotateSouth = 0;
        this.uvRotateNorth = 0;
        this.uvRotateTop = 0;
        this.uvRotateBottom = 0;
        this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        return true;
    }

    /**
     * Renders a lever block at the given coordinates
     */
    public boolean renderBlockLever(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final int i1 = l & 7;
        final boolean flag = (l & 8) > 0;
        final Tessellator tessellator = Tessellator.instance;
        final boolean flag1 = this.hasOverrideBlockTexture();

        if (!flag1)
        {
            this.setOverrideBlockTexture(this.getBlockIcon(Block.cobblestone));
        }

        final float f = 0.25F;
        final float f1 = 0.1875F;
        final float f2 = 0.1875F;

        if (i1 == 5)
        {
            this.setRenderBounds((double)(0.5F - f1), 0.0D, (double)(0.5F - f), (double)(0.5F + f1), (double)f2, (double)(0.5F + f));
        }
        else if (i1 == 6)
        {
            this.setRenderBounds((double)(0.5F - f), 0.0D, (double)(0.5F - f1), (double)(0.5F + f), (double)f2, (double)(0.5F + f1));
        }
        else if (i1 == 4)
        {
            this.setRenderBounds((double)(0.5F - f1), (double)(0.5F - f), (double)(1.0F - f2), (double)(0.5F + f1), (double)(0.5F + f), 1.0D);
        }
        else if (i1 == 3)
        {
            this.setRenderBounds((double)(0.5F - f1), (double)(0.5F - f), 0.0D, (double)(0.5F + f1), (double)(0.5F + f), (double)f2);
        }
        else if (i1 == 2)
        {
            this.setRenderBounds((double)(1.0F - f2), (double)(0.5F - f), (double)(0.5F - f1), 1.0D, (double)(0.5F + f), (double)(0.5F + f1));
        }
        else if (i1 == 1)
        {
            this.setRenderBounds(0.0D, (double)(0.5F - f), (double)(0.5F - f1), (double)f2, (double)(0.5F + f), (double)(0.5F + f1));
        }
        else if (i1 == 0)
        {
            this.setRenderBounds((double)(0.5F - f), (double)(1.0F - f2), (double)(0.5F - f1), (double)(0.5F + f), 1.0D, (double)(0.5F + f1));
        }
        else if (i1 == 7)
        {
            this.setRenderBounds((double)(0.5F - f1), (double)(1.0F - f2), (double)(0.5F - f), (double)(0.5F + f1), 1.0D, (double)(0.5F + f));
        }

        this.renderStandardBlock(par1Block, par2, par3, par4);

        if (!flag1)
        {
            this.clearOverrideBlockTexture();
        }

        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        float f3 = 1.0F;

        if (Block.lightValue[par1Block.blockID] > 0)
        {
            f3 = 1.0F;
        }

        tessellator.setColorOpaque_F(f3, f3, f3);
        Icon icon = this.getBlockIconFromSide(par1Block, 0);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        double d0 = (double)icon.getMinU();
        double d1 = (double)icon.getMinV();
        double d2 = (double)icon.getMaxU();
        double d3 = (double)icon.getMaxV();
        final Vec3[] avec3 = new Vec3[8];
        final float f4 = 0.0625F;
        final float f5 = 0.0625F;
        final float f6 = 0.625F;
        avec3[0] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f4), 0.0D, (double)(-f5));
        avec3[1] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f4, 0.0D, (double)(-f5));
        avec3[2] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f4, 0.0D, (double)f5);
        avec3[3] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f4), 0.0D, (double)f5);
        avec3[4] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f4), (double)f6, (double)(-f5));
        avec3[5] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f4, (double)f6, (double)(-f5));
        avec3[6] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f4, (double)f6, (double)f5);
        avec3[7] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f4), (double)f6, (double)f5);

        for (int j1 = 0; j1 < 8; ++j1)
        {
            if (flag)
            {
                avec3[j1].zCoord -= 0.0625D;
                avec3[j1].rotateAroundX(((float)Math.PI * 2.0F / 9.0F));
            }
            else
            {
                avec3[j1].zCoord += 0.0625D;
                avec3[j1].rotateAroundX(-((float)Math.PI * 2.0F / 9.0F));
            }

            if (i1 == 0 || i1 == 7)
            {
                avec3[j1].rotateAroundZ((float)Math.PI);
            }

            if (i1 == 6 || i1 == 0)
            {
                avec3[j1].rotateAroundY(((float)Math.PI / 2.0F));
            }

            if (i1 > 0 && i1 < 5)
            {
                avec3[j1].yCoord -= 0.375D;
                avec3[j1].rotateAroundX(((float)Math.PI / 2.0F));

                if (i1 == 4)
                {
                    avec3[j1].rotateAroundY(0.0F);
                }

                if (i1 == 3)
                {
                    avec3[j1].rotateAroundY((float)Math.PI);
                }

                if (i1 == 2)
                {
                    avec3[j1].rotateAroundY(((float)Math.PI / 2.0F));
                }

                if (i1 == 1)
                {
                    avec3[j1].rotateAroundY(-((float)Math.PI / 2.0F));
                }

                avec3[j1].xCoord += (double)par2 + 0.5D;
                avec3[j1].yCoord += (double)((float)par3 + 0.5F);
                avec3[j1].zCoord += (double)par4 + 0.5D;
            }
            else if (i1 != 0 && i1 != 7)
            {
                avec3[j1].xCoord += (double)par2 + 0.5D;
                avec3[j1].yCoord += (double)((float)par3 + 0.125F);
                avec3[j1].zCoord += (double)par4 + 0.5D;
            }
            else
            {
                avec3[j1].xCoord += (double)par2 + 0.5D;
                avec3[j1].yCoord += (double)((float)par3 + 0.875F);
                avec3[j1].zCoord += (double)par4 + 0.5D;
            }
        }

        Vec3 vec3 = null;
        Vec3 vec31 = null;
        Vec3 vec32 = null;
        Vec3 vec33 = null;

        for (int k1 = 0; k1 < 6; ++k1)
        {
            if (k1 == 0)
            {
                d0 = (double)icon.getInterpolatedU(7.0D);
                d1 = (double)icon.getInterpolatedV(6.0D);
                d2 = (double)icon.getInterpolatedU(9.0D);
                d3 = (double)icon.getInterpolatedV(8.0D);
            }
            else if (k1 == 2)
            {
                d0 = (double)icon.getInterpolatedU(7.0D);
                d1 = (double)icon.getInterpolatedV(6.0D);
                d2 = (double)icon.getInterpolatedU(9.0D);
                d3 = (double)icon.getMaxV();
            }

            if (k1 == 0)
            {
                vec3 = avec3[0];
                vec31 = avec3[1];
                vec32 = avec3[2];
                vec33 = avec3[3];
            }
            else if (k1 == 1)
            {
                vec3 = avec3[7];
                vec31 = avec3[6];
                vec32 = avec3[5];
                vec33 = avec3[4];
            }
            else if (k1 == 2)
            {
                vec3 = avec3[1];
                vec31 = avec3[0];
                vec32 = avec3[4];
                vec33 = avec3[5];
            }
            else if (k1 == 3)
            {
                vec3 = avec3[2];
                vec31 = avec3[1];
                vec32 = avec3[5];
                vec33 = avec3[6];
            }
            else if (k1 == 4)
            {
                vec3 = avec3[3];
                vec31 = avec3[2];
                vec32 = avec3[6];
                vec33 = avec3[7];
            }
            else if (k1 == 5)
            {
                vec3 = avec3[0];
                vec31 = avec3[3];
                vec32 = avec3[7];
                vec33 = avec3[4];
            }

            tessellator.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, d0, d3);
            tessellator.addVertexWithUV(vec31.xCoord, vec31.yCoord, vec31.zCoord, d2, d3);
            tessellator.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, d2, d1);
            tessellator.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, d0, d1);
        }

        return true;
    }

    /**
     * Renders a trip wire source block at the given coordinates
     */
    public boolean renderBlockTripWireSource(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final int i1 = l & 3;
        final boolean flag = (l & 4) == 4;
        final boolean flag1 = (l & 8) == 8;
        final boolean flag2 = !this.blockAccess.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4);
        final boolean flag3 = this.hasOverrideBlockTexture();

        if (!flag3)
        {
            this.setOverrideBlockTexture(this.getBlockIcon(Block.planks));
        }

        final float f = 0.25F;
        final float f1 = 0.125F;
        final float f2 = 0.125F;
        final float f3 = 0.3F - f;
        final float f4 = 0.3F + f;

        if (i1 == 2)
        {
            this.setRenderBounds((double)(0.5F - f1), (double)f3, (double)(1.0F - f2), (double)(0.5F + f1), (double)f4, 1.0D);
        }
        else if (i1 == 0)
        {
            this.setRenderBounds((double)(0.5F - f1), (double)f3, 0.0D, (double)(0.5F + f1), (double)f4, (double)f2);
        }
        else if (i1 == 1)
        {
            this.setRenderBounds((double)(1.0F - f2), (double)f3, (double)(0.5F - f1), 1.0D, (double)f4, (double)(0.5F + f1));
        }
        else if (i1 == 3)
        {
            this.setRenderBounds(0.0D, (double)f3, (double)(0.5F - f1), (double)f2, (double)f4, (double)(0.5F + f1));
        }

        this.renderStandardBlock(par1Block, par2, par3, par4);

        if (!flag3)
        {
            this.clearOverrideBlockTexture();
        }

        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        float f5 = 1.0F;

        if (Block.lightValue[par1Block.blockID] > 0)
        {
            f5 = 1.0F;
        }

        tessellator.setColorOpaque_F(f5, f5, f5);
        Icon icon = this.getBlockIconFromSide(par1Block, 0);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        double d0 = (double)icon.getMinU();
        double d1 = (double)icon.getMinV();
        double d2 = (double)icon.getMaxU();
        double d3 = (double)icon.getMaxV();
        final Vec3[] avec3 = new Vec3[8];
        final float f6 = 0.046875F;
        final float f7 = 0.046875F;
        final float f8 = 0.3125F;
        avec3[0] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f6), 0.0D, (double)(-f7));
        avec3[1] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f6, 0.0D, (double)(-f7));
        avec3[2] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f6, 0.0D, (double)f7);
        avec3[3] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f6), 0.0D, (double)f7);
        avec3[4] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f6), (double)f8, (double)(-f7));
        avec3[5] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f6, (double)f8, (double)(-f7));
        avec3[6] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f6, (double)f8, (double)f7);
        avec3[7] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f6), (double)f8, (double)f7);

        for (int j1 = 0; j1 < 8; ++j1)
        {
            avec3[j1].zCoord += 0.0625D;

            if (flag1)
            {
                avec3[j1].rotateAroundX(0.5235988F);
                avec3[j1].yCoord -= 0.4375D;
            }
            else if (flag)
            {
                avec3[j1].rotateAroundX(0.08726647F);
                avec3[j1].yCoord -= 0.4375D;
            }
            else
            {
                avec3[j1].rotateAroundX(-((float)Math.PI * 2.0F / 9.0F));
                avec3[j1].yCoord -= 0.375D;
            }

            avec3[j1].rotateAroundX(((float)Math.PI / 2.0F));

            if (i1 == 2)
            {
                avec3[j1].rotateAroundY(0.0F);
            }

            if (i1 == 0)
            {
                avec3[j1].rotateAroundY((float)Math.PI);
            }

            if (i1 == 1)
            {
                avec3[j1].rotateAroundY(((float)Math.PI / 2.0F));
            }

            if (i1 == 3)
            {
                avec3[j1].rotateAroundY(-((float)Math.PI / 2.0F));
            }

            avec3[j1].xCoord += (double)par2 + 0.5D;
            avec3[j1].yCoord += (double)((float)par3 + 0.3125F);
            avec3[j1].zCoord += (double)par4 + 0.5D;
        }

        Vec3 vec3 = null;
        Vec3 vec31 = null;
        Vec3 vec32 = null;
        Vec3 vec33 = null;
        final byte b0 = 7;
        final byte b1 = 9;
        final byte b2 = 9;
        final byte b3 = 16;

        for (int k1 = 0; k1 < 6; ++k1)
        {
            if (k1 == 0)
            {
                vec3 = avec3[0];
                vec31 = avec3[1];
                vec32 = avec3[2];
                vec33 = avec3[3];
                d0 = (double)icon.getInterpolatedU((double)b0);
                d1 = (double)icon.getInterpolatedV((double)b2);
                d2 = (double)icon.getInterpolatedU((double)b1);
                d3 = (double)icon.getInterpolatedV((double)(b2 + 2));
            }
            else if (k1 == 1)
            {
                vec3 = avec3[7];
                vec31 = avec3[6];
                vec32 = avec3[5];
                vec33 = avec3[4];
            }
            else if (k1 == 2)
            {
                vec3 = avec3[1];
                vec31 = avec3[0];
                vec32 = avec3[4];
                vec33 = avec3[5];
                d0 = (double)icon.getInterpolatedU((double)b0);
                d1 = (double)icon.getInterpolatedV((double)b2);
                d2 = (double)icon.getInterpolatedU((double)b1);
                d3 = (double)icon.getInterpolatedV((double)b3);
            }
            else if (k1 == 3)
            {
                vec3 = avec3[2];
                vec31 = avec3[1];
                vec32 = avec3[5];
                vec33 = avec3[6];
            }
            else if (k1 == 4)
            {
                vec3 = avec3[3];
                vec31 = avec3[2];
                vec32 = avec3[6];
                vec33 = avec3[7];
            }
            else if (k1 == 5)
            {
                vec3 = avec3[0];
                vec31 = avec3[3];
                vec32 = avec3[7];
                vec33 = avec3[4];
            }

            tessellator.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, d0, d3);
            tessellator.addVertexWithUV(vec31.xCoord, vec31.yCoord, vec31.zCoord, d2, d3);
            tessellator.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, d2, d1);
            tessellator.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, d0, d1);
        }

        final float f9 = 0.09375F;
        final float f10 = 0.09375F;
        final float f11 = 0.03125F;
        avec3[0] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f9), 0.0D, (double)(-f10));
        avec3[1] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f9, 0.0D, (double)(-f10));
        avec3[2] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f9, 0.0D, (double)f10);
        avec3[3] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f9), 0.0D, (double)f10);
        avec3[4] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f9), (double)f11, (double)(-f10));
        avec3[5] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f9, (double)f11, (double)(-f10));
        avec3[6] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)f9, (double)f11, (double)f10);
        avec3[7] = this.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-f9), (double)f11, (double)f10);

        for (int l1 = 0; l1 < 8; ++l1)
        {
            avec3[l1].zCoord += 0.21875D;

            if (flag1)
            {
                avec3[l1].yCoord -= 0.09375D;
                avec3[l1].zCoord -= 0.1625D;
                avec3[l1].rotateAroundX(0.0F);
            }
            else if (flag)
            {
                avec3[l1].yCoord += 0.015625D;
                avec3[l1].zCoord -= 0.171875D;
                avec3[l1].rotateAroundX(0.17453294F);
            }
            else
            {
                avec3[l1].rotateAroundX(0.87266463F);
            }

            if (i1 == 2)
            {
                avec3[l1].rotateAroundY(0.0F);
            }

            if (i1 == 0)
            {
                avec3[l1].rotateAroundY((float)Math.PI);
            }

            if (i1 == 1)
            {
                avec3[l1].rotateAroundY(((float)Math.PI / 2.0F));
            }

            if (i1 == 3)
            {
                avec3[l1].rotateAroundY(-((float)Math.PI / 2.0F));
            }

            avec3[l1].xCoord += (double)par2 + 0.5D;
            avec3[l1].yCoord += (double)((float)par3 + 0.3125F);
            avec3[l1].zCoord += (double)par4 + 0.5D;
        }

        final byte b4 = 5;
        final byte b5 = 11;
        final byte b6 = 3;
        final byte b7 = 9;

        for (int i2 = 0; i2 < 6; ++i2)
        {
            if (i2 == 0)
            {
                vec3 = avec3[0];
                vec31 = avec3[1];
                vec32 = avec3[2];
                vec33 = avec3[3];
                d0 = (double)icon.getInterpolatedU((double)b4);
                d1 = (double)icon.getInterpolatedV((double)b6);
                d2 = (double)icon.getInterpolatedU((double)b5);
                d3 = (double)icon.getInterpolatedV((double)b7);
            }
            else if (i2 == 1)
            {
                vec3 = avec3[7];
                vec31 = avec3[6];
                vec32 = avec3[5];
                vec33 = avec3[4];
            }
            else if (i2 == 2)
            {
                vec3 = avec3[1];
                vec31 = avec3[0];
                vec32 = avec3[4];
                vec33 = avec3[5];
                d0 = (double)icon.getInterpolatedU((double)b4);
                d1 = (double)icon.getInterpolatedV((double)b6);
                d2 = (double)icon.getInterpolatedU((double)b5);
                d3 = (double)icon.getInterpolatedV((double)(b6 + 2));
            }
            else if (i2 == 3)
            {
                vec3 = avec3[2];
                vec31 = avec3[1];
                vec32 = avec3[5];
                vec33 = avec3[6];
            }
            else if (i2 == 4)
            {
                vec3 = avec3[3];
                vec31 = avec3[2];
                vec32 = avec3[6];
                vec33 = avec3[7];
            }
            else if (i2 == 5)
            {
                vec3 = avec3[0];
                vec31 = avec3[3];
                vec32 = avec3[7];
                vec33 = avec3[4];
            }

            tessellator.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, d0, d3);
            tessellator.addVertexWithUV(vec31.xCoord, vec31.yCoord, vec31.zCoord, d2, d3);
            tessellator.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, d2, d1);
            tessellator.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, d0, d1);
        }

        if (flag)
        {
            final double d4 = avec3[0].yCoord;
            final float f12 = 0.03125F;
            final float f13 = 0.5F - f12 / 2.0F;
            final float f14 = f13 + f12;
            final Icon icon1 = this.getBlockIcon(Block.tripWire);
            final double d5 = (double)icon.getMinU();
            final double d6 = (double)icon.getInterpolatedV(flag ? 2.0D : 0.0D);
            final double d7 = (double)icon.getMaxU();
            final double d8 = (double)icon.getInterpolatedV(flag ? 4.0D : 2.0D);
            final double d9 = (double)(flag2 ? 3.5F : 1.5F) / 16.0D;
            f5 = par1Block.getBlockBrightness(this.blockAccess, par2, par3, par4) * 0.75F;
            tessellator.setColorOpaque_F(f5, f5, f5);

            if (i1 == 2)
            {
                tessellator.addVertexWithUV((double)((float)par2 + f13), (double)par3 + d9, (double)par4 + 0.25D, d5, d6);
                tessellator.addVertexWithUV((double)((float)par2 + f14), (double)par3 + d9, (double)par4 + 0.25D, d5, d8);
                tessellator.addVertexWithUV((double)((float)par2 + f14), (double)par3 + d9, (double)par4, d7, d8);
                tessellator.addVertexWithUV((double)((float)par2 + f13), (double)par3 + d9, (double)par4, d7, d6);
                tessellator.addVertexWithUV((double)((float)par2 + f13), d4, (double)par4 + 0.5D, d5, d6);
                tessellator.addVertexWithUV((double)((float)par2 + f14), d4, (double)par4 + 0.5D, d5, d8);
                tessellator.addVertexWithUV((double)((float)par2 + f14), (double)par3 + d9, (double)par4 + 0.25D, d7, d8);
                tessellator.addVertexWithUV((double)((float)par2 + f13), (double)par3 + d9, (double)par4 + 0.25D, d7, d6);
            }
            else if (i1 == 0)
            {
                tessellator.addVertexWithUV((double)((float)par2 + f13), (double)par3 + d9, (double)par4 + 0.75D, d5, d6);
                tessellator.addVertexWithUV((double)((float)par2 + f14), (double)par3 + d9, (double)par4 + 0.75D, d5, d8);
                tessellator.addVertexWithUV((double)((float)par2 + f14), d4, (double)par4 + 0.5D, d7, d8);
                tessellator.addVertexWithUV((double)((float)par2 + f13), d4, (double)par4 + 0.5D, d7, d6);
                tessellator.addVertexWithUV((double)((float)par2 + f13), (double)par3 + d9, (double)(par4 + 1), d5, d6);
                tessellator.addVertexWithUV((double)((float)par2 + f14), (double)par3 + d9, (double)(par4 + 1), d5, d8);
                tessellator.addVertexWithUV((double)((float)par2 + f14), (double)par3 + d9, (double)par4 + 0.75D, d7, d8);
                tessellator.addVertexWithUV((double)((float)par2 + f13), (double)par3 + d9, (double)par4 + 0.75D, d7, d6);
            }
            else if (i1 == 1)
            {
                tessellator.addVertexWithUV((double)par2, (double)par3 + d9, (double)((float)par4 + f14), d5, d8);
                tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d9, (double)((float)par4 + f14), d7, d8);
                tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d9, (double)((float)par4 + f13), d7, d6);
                tessellator.addVertexWithUV((double)par2, (double)par3 + d9, (double)((float)par4 + f13), d5, d6);
                tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d9, (double)((float)par4 + f14), d5, d8);
                tessellator.addVertexWithUV((double)par2 + 0.5D, d4, (double)((float)par4 + f14), d7, d8);
                tessellator.addVertexWithUV((double)par2 + 0.5D, d4, (double)((float)par4 + f13), d7, d6);
                tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d9, (double)((float)par4 + f13), d5, d6);
            }
            else
            {
                tessellator.addVertexWithUV((double)par2 + 0.5D, d4, (double)((float)par4 + f14), d5, d8);
                tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d9, (double)((float)par4 + f14), d7, d8);
                tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d9, (double)((float)par4 + f13), d7, d6);
                tessellator.addVertexWithUV((double)par2 + 0.5D, d4, (double)((float)par4 + f13), d5, d6);
                tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d9, (double)((float)par4 + f14), d5, d8);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)par3 + d9, (double)((float)par4 + f14), d7, d8);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)par3 + d9, (double)((float)par4 + f13), d7, d6);
                tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d9, (double)((float)par4 + f13), d5, d6);
            }
        }

        return true;
    }

    /**
     * Renders a trip wire block at the given coordinates
     */
    public boolean renderBlockTripWire(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        Icon icon = this.getBlockIconFromSide(par1Block, 0);
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final boolean flag = (l & 4) == 4;
        final boolean flag1 = (l & 2) == 2;

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = par1Block.getBlockBrightness(this.blockAccess, par2, par3, par4) * 0.75F;
        tessellator.setColorOpaque_F(f, f, f);
        final double d0 = (double)icon.getMinU();
        final double d1 = (double)icon.getInterpolatedV(flag ? 2.0D : 0.0D);
        final double d2 = (double)icon.getMaxU();
        final double d3 = (double)icon.getInterpolatedV(flag ? 4.0D : 2.0D);
        final double d4 = (double)(flag1 ? 3.5F : 1.5F) / 16.0D;
        final boolean flag2 = BlockTripWire.func_72148_a(this.blockAccess, par2, par3, par4, l, 1);
        final boolean flag3 = BlockTripWire.func_72148_a(this.blockAccess, par2, par3, par4, l, 3);
        boolean flag4 = BlockTripWire.func_72148_a(this.blockAccess, par2, par3, par4, l, 2);
        boolean flag5 = BlockTripWire.func_72148_a(this.blockAccess, par2, par3, par4, l, 0);
        final float f1 = 0.03125F;
        final float f2 = 0.5F - f1 / 2.0F;
        final float f3 = f2 + f1;

        if (!flag4 && !flag3 && !flag5 && !flag2)
        {
            flag4 = true;
            flag5 = true;
        }

        if (flag4)
        {
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.25D, d0, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.25D, d0, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4, d2, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4, d2, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4, d2, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4, d2, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.25D, d0, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.25D, d0, d1);
        }

        if (flag4 || flag5 && !flag3 && !flag2)
        {
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.5D, d0, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.5D, d0, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.25D, d2, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.25D, d2, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.25D, d2, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.25D, d2, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.5D, d0, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.5D, d0, d1);
        }

        if (flag5 || flag4 && !flag3 && !flag2)
        {
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.75D, d0, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.75D, d0, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.5D, d2, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.5D, d2, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.5D, d2, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.5D, d2, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.75D, d0, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.75D, d0, d1);
        }

        if (flag5)
        {
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)(par4 + 1), d0, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)(par4 + 1), d0, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.75D, d2, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.75D, d2, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)par4 + 0.75D, d2, d1);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)par4 + 0.75D, d2, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f3), (double)par3 + d4, (double)(par4 + 1), d0, d3);
            tessellator.addVertexWithUV((double)((float)par2 + f2), (double)par3 + d4, (double)(par4 + 1), d0, d1);
        }

        if (flag2)
        {
            tessellator.addVertexWithUV((double)par2, (double)par3 + d4, (double)((float)par4 + f3), d0, d3);
            tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d4, (double)((float)par4 + f3), d2, d3);
            tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d4, (double)((float)par4 + f2), d2, d1);
            tessellator.addVertexWithUV((double)par2, (double)par3 + d4, (double)((float)par4 + f2), d0, d1);
            tessellator.addVertexWithUV((double)par2, (double)par3 + d4, (double)((float)par4 + f2), d0, d1);
            tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d4, (double)((float)par4 + f2), d2, d1);
            tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d4, (double)((float)par4 + f3), d2, d3);
            tessellator.addVertexWithUV((double)par2, (double)par3 + d4, (double)((float)par4 + f3), d0, d3);
        }

        if (flag2 || flag3 && !flag4 && !flag5)
        {
            tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d4, (double)((float)par4 + f3), d0, d3);
            tessellator.addVertexWithUV((double)par2 + 0.5D, (double)par3 + d4, (double)((float)par4 + f3), d2, d3);
            tessellator.addVertexWithUV((double)par2 + 0.5D, (double)par3 + d4, (double)((float)par4 + f2), d2, d1);
            tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d4, (double)((float)par4 + f2), d0, d1);
            tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d4, (double)((float)par4 + f2), d0, d1);
            tessellator.addVertexWithUV((double)par2 + 0.5D, (double)par3 + d4, (double)((float)par4 + f2), d2, d1);
            tessellator.addVertexWithUV((double)par2 + 0.5D, (double)par3 + d4, (double)((float)par4 + f3), d2, d3);
            tessellator.addVertexWithUV((double)par2 + 0.25D, (double)par3 + d4, (double)((float)par4 + f3), d0, d3);
        }

        if (flag3 || flag2 && !flag4 && !flag5)
        {
            tessellator.addVertexWithUV((double)par2 + 0.5D, (double)par3 + d4, (double)((float)par4 + f3), d0, d3);
            tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d4, (double)((float)par4 + f3), d2, d3);
            tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d4, (double)((float)par4 + f2), d2, d1);
            tessellator.addVertexWithUV((double)par2 + 0.5D, (double)par3 + d4, (double)((float)par4 + f2), d0, d1);
            tessellator.addVertexWithUV((double)par2 + 0.5D, (double)par3 + d4, (double)((float)par4 + f2), d0, d1);
            tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d4, (double)((float)par4 + f2), d2, d1);
            tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d4, (double)((float)par4 + f3), d2, d3);
            tessellator.addVertexWithUV((double)par2 + 0.5D, (double)par3 + d4, (double)((float)par4 + f3), d0, d3);
        }

        if (flag3)
        {
            tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d4, (double)((float)par4 + f3), d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)par3 + d4, (double)((float)par4 + f3), d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)par3 + d4, (double)((float)par4 + f2), d2, d1);
            tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d4, (double)((float)par4 + f2), d0, d1);
            tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d4, (double)((float)par4 + f2), d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)par3 + d4, (double)((float)par4 + f2), d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)par3 + d4, (double)((float)par4 + f3), d2, d3);
            tessellator.addVertexWithUV((double)par2 + 0.75D, (double)par3 + d4, (double)((float)par4 + f3), d0, d3);
        }

        return true;
    }

    /**
     * Renders a fire block at the given coordinates
     */
    public boolean renderBlockFire(final BlockFire par1BlockFire, final int par2, int par3, final int par4)
    {
        int par31 = par3;
        final Tessellator tessellator = Tessellator.instance;
        final Icon icon = par1BlockFire.getFireIcon(0);
        final Icon icon1 = par1BlockFire.getFireIcon(1);
        Icon icon2 = icon;

        if (this.hasOverrideBlockTexture())
        {
            icon2 = this.overrideBlockTexture;
        }

        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setBrightness(par1BlockFire.getMixedBrightnessForBlock(this.blockAccess, par2, par31, par4));
        double d0 = (double)icon2.getMinU();
        double d1 = (double)icon2.getMinV();
        double d2 = (double)icon2.getMaxU();
        double d3 = (double)icon2.getMaxV();
        float f = 1.4F;
        double d4;
        double d5;
        double d6;
        double d7;
        double d8;
        double d9;
        double d10;

        if (!this.blockAccess.doesBlockHaveSolidTopSurface(par2, par31 - 1, par4) && !Block.fire.canBlockCatchFire(this.blockAccess, par2, par31 - 1, par4, UP))
        {
            final float f1 = 0.2F;
            final float f2 = 0.0625F;

            if ((par2 + par31 + par4 & 1) == 1)
            {
                d0 = (double)icon1.getMinU();
                d1 = (double)icon1.getMinV();
                d2 = (double)icon1.getMaxU();
                d3 = (double)icon1.getMaxV();
            }

            if ((par2 / 2 + par31 / 2 + par4 / 2 & 1) == 1)
            {
                d5 = d2;
                d2 = d0;
                d0 = d5;
            }

            if (Block.fire.canBlockCatchFire(this.blockAccess, par2 - 1, par31, par4, EAST))
            {
                tessellator.addVertexWithUV((double)((float)par2 + f1), (double)((float) par31 + f + f2), (double)(par4 + 1), d2, d1);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 1), d2, d3);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 0), d0, d3);
                tessellator.addVertexWithUV((double)((float)par2 + f1), (double)((float) par31 + f + f2), (double)(par4 + 0), d0, d1);
                tessellator.addVertexWithUV((double)((float)par2 + f1), (double)((float) par31 + f + f2), (double)(par4 + 0), d0, d1);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 0), d0, d3);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 1), d2, d3);
                tessellator.addVertexWithUV((double)((float)par2 + f1), (double)((float) par31 + f + f2), (double)(par4 + 1), d2, d1);
            }

            if (Block.fire.canBlockCatchFire(this.blockAccess, par2 + 1, par31, par4, WEST))
            {
                tessellator.addVertexWithUV((double)((float)(par2 + 1) - f1), (double)((float) par31 + f + f2), (double)(par4 + 0), d0, d1);
                tessellator.addVertexWithUV((double)(par2 + 1 - 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 0), d0, d3);
                tessellator.addVertexWithUV((double)(par2 + 1 - 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 1), d2, d3);
                tessellator.addVertexWithUV((double)((float)(par2 + 1) - f1), (double)((float) par31 + f + f2), (double)(par4 + 1), d2, d1);
                tessellator.addVertexWithUV((double)((float)(par2 + 1) - f1), (double)((float) par31 + f + f2), (double)(par4 + 1), d2, d1);
                tessellator.addVertexWithUV((double)(par2 + 1 - 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 1), d2, d3);
                tessellator.addVertexWithUV((double)(par2 + 1 - 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 0), d0, d3);
                tessellator.addVertexWithUV((double)((float)(par2 + 1) - f1), (double)((float) par31 + f + f2), (double)(par4 + 0), d0, d1);
            }

            if (Block.fire.canBlockCatchFire(this.blockAccess, par2, par31, par4 - 1, SOUTH))
            {
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float) par31 + f + f2), (double)((float)par4 + f1), d2, d1);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 0), d2, d3);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float)(par31 + 0) + f2), (double)(par4 + 0), d0, d3);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float) par31 + f + f2), (double)((float)par4 + f1), d0, d1);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float) par31 + f + f2), (double)((float)par4 + f1), d0, d1);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float)(par31 + 0) + f2), (double)(par4 + 0), d0, d3);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 0), d2, d3);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float) par31 + f + f2), (double)((float)par4 + f1), d2, d1);
            }

            if (Block.fire.canBlockCatchFire(this.blockAccess, par2, par31, par4 + 1, NORTH))
            {
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float) par31 + f + f2), (double)((float)(par4 + 1) - f1), d0, d1);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float)(par31 + 0) + f2), (double)(par4 + 1 - 0), d0, d3);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 1 - 0), d2, d3);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float) par31 + f + f2), (double)((float)(par4 + 1) - f1), d2, d1);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float) par31 + f + f2), (double)((float)(par4 + 1) - f1), d2, d1);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par31 + 0) + f2), (double)(par4 + 1 - 0), d2, d3);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float)(par31 + 0) + f2), (double)(par4 + 1 - 0), d0, d3);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float) par31 + f + f2), (double)((float)(par4 + 1) - f1), d0, d1);
            }

            if (Block.fire.canBlockCatchFire(this.blockAccess, par2, par31 + 1, par4, DOWN))
            {
                d5 = (double)par2 + 0.5D + 0.5D;
                d6 = (double)par2 + 0.5D - 0.5D;
                d7 = (double)par4 + 0.5D + 0.5D;
                d8 = (double)par4 + 0.5D - 0.5D;
                d9 = (double)par2 + 0.5D - 0.5D;
                d10 = (double)par2 + 0.5D + 0.5D;
                d4 = (double)par4 + 0.5D - 0.5D;
                final double d11 = (double)par4 + 0.5D + 0.5D;
                d0 = (double)icon.getMinU();
                d1 = (double)icon.getMinV();
                d2 = (double)icon.getMaxU();
                d3 = (double)icon.getMaxV();
                ++par31;
                f = -0.2F;

                if ((par2 + par31 + par4 & 1) == 0)
                {
                    tessellator.addVertexWithUV(d9, (double)((float) par31 + f), (double)(par4 + 0), d2, d1);
                    tessellator.addVertexWithUV(d5, (double)(par31 + 0), (double)(par4 + 0), d2, d3);
                    tessellator.addVertexWithUV(d5, (double)(par31 + 0), (double)(par4 + 1), d0, d3);
                    tessellator.addVertexWithUV(d9, (double)((float) par31 + f), (double)(par4 + 1), d0, d1);
                    d0 = (double)icon1.getMinU();
                    d1 = (double)icon1.getMinV();
                    d2 = (double)icon1.getMaxU();
                    d3 = (double)icon1.getMaxV();
                    tessellator.addVertexWithUV(d10, (double)((float) par31 + f), (double)(par4 + 1), d2, d1);
                    tessellator.addVertexWithUV(d6, (double)(par31 + 0), (double)(par4 + 1), d2, d3);
                    tessellator.addVertexWithUV(d6, (double)(par31 + 0), (double)(par4 + 0), d0, d3);
                    tessellator.addVertexWithUV(d10, (double)((float) par31 + f), (double)(par4 + 0), d0, d1);
                }
                else
                {
                    tessellator.addVertexWithUV((double)(par2 + 0), (double)((float) par31 + f), d11, d2, d1);
                    tessellator.addVertexWithUV((double)(par2 + 0), (double)(par31 + 0), d8, d2, d3);
                    tessellator.addVertexWithUV((double)(par2 + 1), (double)(par31 + 0), d8, d0, d3);
                    tessellator.addVertexWithUV((double)(par2 + 1), (double)((float) par31 + f), d11, d0, d1);
                    d0 = (double)icon1.getMinU();
                    d1 = (double)icon1.getMinV();
                    d2 = (double)icon1.getMaxU();
                    d3 = (double)icon1.getMaxV();
                    tessellator.addVertexWithUV((double)(par2 + 1), (double)((float) par31 + f), d4, d2, d1);
                    tessellator.addVertexWithUV((double)(par2 + 1), (double)(par31 + 0), d7, d2, d3);
                    tessellator.addVertexWithUV((double)(par2 + 0), (double)(par31 + 0), d7, d0, d3);
                    tessellator.addVertexWithUV((double)(par2 + 0), (double)((float) par31 + f), d4, d0, d1);
                }
            }
        }
        else
        {
            double d12 = (double)par2 + 0.5D + 0.2D;
            d5 = (double)par2 + 0.5D - 0.2D;
            d6 = (double)par4 + 0.5D + 0.2D;
            d7 = (double)par4 + 0.5D - 0.2D;
            d8 = (double)par2 + 0.5D - 0.3D;
            d9 = (double)par2 + 0.5D + 0.3D;
            d10 = (double)par4 + 0.5D - 0.3D;
            d4 = (double)par4 + 0.5D + 0.3D;
            tessellator.addVertexWithUV(d8, (double)((float) par31 + f), (double)(par4 + 1), d2, d1);
            tessellator.addVertexWithUV(d12, (double)(par31 + 0), (double)(par4 + 1), d2, d3);
            tessellator.addVertexWithUV(d12, (double)(par31 + 0), (double)(par4 + 0), d0, d3);
            tessellator.addVertexWithUV(d8, (double)((float) par31 + f), (double)(par4 + 0), d0, d1);
            tessellator.addVertexWithUV(d9, (double)((float) par31 + f), (double)(par4 + 0), d2, d1);
            tessellator.addVertexWithUV(d5, (double)(par31 + 0), (double)(par4 + 0), d2, d3);
            tessellator.addVertexWithUV(d5, (double)(par31 + 0), (double)(par4 + 1), d0, d3);
            tessellator.addVertexWithUV(d9, (double)((float) par31 + f), (double)(par4 + 1), d0, d1);
            d0 = (double)icon1.getMinU();
            d1 = (double)icon1.getMinV();
            d2 = (double)icon1.getMaxU();
            d3 = (double)icon1.getMaxV();
            tessellator.addVertexWithUV((double)(par2 + 1), (double)((float) par31 + f), d4, d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par31 + 0), d7, d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par31 + 0), d7, d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)((float) par31 + f), d4, d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)((float) par31 + f), d10, d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par31 + 0), d6, d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par31 + 0), d6, d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)((float) par31 + f), d10, d0, d1);
            d12 = (double)par2 + 0.5D - 0.5D;
            d5 = (double)par2 + 0.5D + 0.5D;
            d6 = (double)par4 + 0.5D - 0.5D;
            d7 = (double)par4 + 0.5D + 0.5D;
            d8 = (double)par2 + 0.5D - 0.4D;
            d9 = (double)par2 + 0.5D + 0.4D;
            d10 = (double)par4 + 0.5D - 0.4D;
            d4 = (double)par4 + 0.5D + 0.4D;
            tessellator.addVertexWithUV(d8, (double)((float) par31 + f), (double)(par4 + 0), d0, d1);
            tessellator.addVertexWithUV(d12, (double)(par31 + 0), (double)(par4 + 0), d0, d3);
            tessellator.addVertexWithUV(d12, (double)(par31 + 0), (double)(par4 + 1), d2, d3);
            tessellator.addVertexWithUV(d8, (double)((float) par31 + f), (double)(par4 + 1), d2, d1);
            tessellator.addVertexWithUV(d9, (double)((float) par31 + f), (double)(par4 + 1), d0, d1);
            tessellator.addVertexWithUV(d5, (double)(par31 + 0), (double)(par4 + 1), d0, d3);
            tessellator.addVertexWithUV(d5, (double)(par31 + 0), (double)(par4 + 0), d2, d3);
            tessellator.addVertexWithUV(d9, (double)((float) par31 + f), (double)(par4 + 0), d2, d1);
            d0 = (double)icon.getMinU();
            d1 = (double)icon.getMinV();
            d2 = (double)icon.getMaxU();
            d3 = (double)icon.getMaxV();
            tessellator.addVertexWithUV((double)(par2 + 0), (double)((float) par31 + f), d4, d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par31 + 0), d7, d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par31 + 0), d7, d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)((float) par31 + f), d4, d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)((float) par31 + f), d10, d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par31 + 0), d6, d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par31 + 0), d6, d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)((float) par31 + f), d10, d2, d1);
        }

        return true;
    }

    /**
     * Renders a redstone wire block at the given coordinates
     */
    public boolean renderBlockRedstoneWire(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final Icon icon = BlockRedstoneWire.getRedstoneWireIcon("cross");
        final Icon icon1 = BlockRedstoneWire.getRedstoneWireIcon("line");
        final Icon icon2 = BlockRedstoneWire.getRedstoneWireIcon("cross_overlay");
        final Icon icon3 = BlockRedstoneWire.getRedstoneWireIcon("line_overlay");
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = 1.0F;
        final float f1 = (float)l / 15.0F;
        float f2 = f1 * 0.6F + 0.4F;

        if (l == 0)
        {
            f2 = 0.3F;
        }

        float f3 = f1 * f1 * 0.7F - 0.5F;
        float f4 = f1 * f1 * 0.6F - 0.7F;

        if (f3 < 0.0F)
        {
            f3 = 0.0F;
        }

        if (f4 < 0.0F)
        {
            f4 = 0.0F;
        }

        tessellator.setColorOpaque_F(f2, f3, f4);
        final double d0 = 0.015625D;
        final double d1 = 0.015625D;
        boolean flag = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 - 1, par3, par4, 1) || !this.blockAccess.isBlockNormalCube(par2 - 1, par3, par4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 - 1, par3 - 1, par4, -1);
        boolean flag1 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 + 1, par3, par4, 3) || !this.blockAccess.isBlockNormalCube(par2 + 1, par3, par4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 + 1, par3 - 1, par4, -1);
        boolean flag2 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3, par4 - 1, 2) || !this.blockAccess.isBlockNormalCube(par2, par3, par4 - 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3 - 1, par4 - 1, -1);
        boolean flag3 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3, par4 + 1, 0) || !this.blockAccess.isBlockNormalCube(par2, par3, par4 + 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3 - 1, par4 + 1, -1);

        if (!this.blockAccess.isBlockNormalCube(par2, par3 + 1, par4))
        {
            if (this.blockAccess.isBlockNormalCube(par2 - 1, par3, par4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 - 1, par3 + 1, par4, -1))
            {
                flag = true;
            }

            if (this.blockAccess.isBlockNormalCube(par2 + 1, par3, par4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2 + 1, par3 + 1, par4, -1))
            {
                flag1 = true;
            }

            if (this.blockAccess.isBlockNormalCube(par2, par3, par4 - 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3 + 1, par4 - 1, -1))
            {
                flag2 = true;
            }

            if (this.blockAccess.isBlockNormalCube(par2, par3, par4 + 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, par2, par3 + 1, par4 + 1, -1))
            {
                flag3 = true;
            }
        }

        float f5 = (float)(par2 + 0);
        float f6 = (float)(par2 + 1);
        float f7 = (float)(par4 + 0);
        float f8 = (float)(par4 + 1);
        int i1 = 0;

        if ((flag || flag1) && !flag2 && !flag3)
        {
            i1 = 1;
        }

        if ((flag2 || flag3) && !flag1 && !flag)
        {
            i1 = 2;
        }

        if (i1 == 0)
        {
            int j1 = 0;
            int k1 = 0;
            int l1 = 16;
            int i2 = 16;
            final boolean flag4 = true;

            if (!flag)
            {
                f5 += 0.3125F;
            }

            if (!flag)
            {
                j1 += 5;
            }

            if (!flag1)
            {
                f6 -= 0.3125F;
            }

            if (!flag1)
            {
                l1 -= 5;
            }

            if (!flag2)
            {
                f7 += 0.3125F;
            }

            if (!flag2)
            {
                k1 += 5;
            }

            if (!flag3)
            {
                f8 -= 0.3125F;
            }

            if (!flag3)
            {
                i2 -= 5;
            }

            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f8, (double)icon.getInterpolatedU((double)l1), (double)icon.getInterpolatedV((double)i2));
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f7, (double)icon.getInterpolatedU((double)l1), (double)icon.getInterpolatedV((double)k1));
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f7, (double)icon.getInterpolatedU((double)j1), (double)icon.getInterpolatedV((double)k1));
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f8, (double)icon.getInterpolatedU((double)j1), (double)icon.getInterpolatedV((double)i2));
            tessellator.setColorOpaque_F(f, f, f);
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f8, (double)icon2.getInterpolatedU((double)l1), (double)icon2.getInterpolatedV((double)i2));
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f7, (double)icon2.getInterpolatedU((double)l1), (double)icon2.getInterpolatedV((double)k1));
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f7, (double)icon2.getInterpolatedU((double)j1), (double)icon2.getInterpolatedV((double)k1));
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f8, (double)icon2.getInterpolatedU((double)j1), (double)icon2.getInterpolatedV((double)i2));
        }
        else if (i1 == 1)
        {
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f8, (double)icon1.getMaxU(), (double)icon1.getMaxV());
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f7, (double)icon1.getMaxU(), (double)icon1.getMinV());
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f7, (double)icon1.getMinU(), (double)icon1.getMinV());
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f8, (double)icon1.getMinU(), (double)icon1.getMaxV());
            tessellator.setColorOpaque_F(f, f, f);
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f8, (double)icon3.getMaxU(), (double)icon3.getMaxV());
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f7, (double)icon3.getMaxU(), (double)icon3.getMinV());
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f7, (double)icon3.getMinU(), (double)icon3.getMinV());
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f8, (double)icon3.getMinU(), (double)icon3.getMaxV());
        }
        else
        {
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f8, (double)icon1.getMaxU(), (double)icon1.getMaxV());
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f7, (double)icon1.getMinU(), (double)icon1.getMaxV());
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f7, (double)icon1.getMinU(), (double)icon1.getMinV());
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f8, (double)icon1.getMaxU(), (double)icon1.getMinV());
            tessellator.setColorOpaque_F(f, f, f);
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f8, (double)icon3.getMaxU(), (double)icon3.getMaxV());
            tessellator.addVertexWithUV((double)f6, (double)par3 + 0.015625D, (double)f7, (double)icon3.getMinU(), (double)icon3.getMaxV());
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f7, (double)icon3.getMinU(), (double)icon3.getMinV());
            tessellator.addVertexWithUV((double)f5, (double)par3 + 0.015625D, (double)f8, (double)icon3.getMaxU(), (double)icon3.getMinV());
        }

        if (!this.blockAccess.isBlockNormalCube(par2, par3 + 1, par4))
        {
            final float f9 = 0.021875F;

            if (this.blockAccess.isBlockNormalCube(par2 - 1, par3, par4) && this.blockAccess.getBlockId(par2 - 1, par3 + 1, par4) == Block.redstoneWire.blockID)
            {
                tessellator.setColorOpaque_F(f * f2, f * f3, f * f4);
                tessellator.addVertexWithUV((double)par2 + 0.015625D, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1), (double)icon1.getMaxU(), (double)icon1.getMinV());
                tessellator.addVertexWithUV((double)par2 + 0.015625D, (double)(par3 + 0), (double)(par4 + 1), (double)icon1.getMinU(), (double)icon1.getMinV());
                tessellator.addVertexWithUV((double)par2 + 0.015625D, (double)(par3 + 0), (double)(par4 + 0), (double)icon1.getMinU(), (double)icon1.getMaxV());
                tessellator.addVertexWithUV((double)par2 + 0.015625D, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 0), (double)icon1.getMaxU(), (double)icon1.getMaxV());
                tessellator.setColorOpaque_F(f, f, f);
                tessellator.addVertexWithUV((double)par2 + 0.015625D, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1), (double)icon3.getMaxU(), (double)icon3.getMinV());
                tessellator.addVertexWithUV((double)par2 + 0.015625D, (double)(par3 + 0), (double)(par4 + 1), (double)icon3.getMinU(), (double)icon3.getMinV());
                tessellator.addVertexWithUV((double)par2 + 0.015625D, (double)(par3 + 0), (double)(par4 + 0), (double)icon3.getMinU(), (double)icon3.getMaxV());
                tessellator.addVertexWithUV((double)par2 + 0.015625D, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 0), (double)icon3.getMaxU(), (double)icon3.getMaxV());
            }

            if (this.blockAccess.isBlockNormalCube(par2 + 1, par3, par4) && this.blockAccess.getBlockId(par2 + 1, par3 + 1, par4) == Block.redstoneWire.blockID)
            {
                tessellator.setColorOpaque_F(f * f2, f * f3, f * f4);
                tessellator.addVertexWithUV((double)(par2 + 1) - 0.015625D, (double)(par3 + 0), (double)(par4 + 1), (double)icon1.getMinU(), (double)icon1.getMaxV());
                tessellator.addVertexWithUV((double)(par2 + 1) - 0.015625D, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1), (double)icon1.getMaxU(), (double)icon1.getMaxV());
                tessellator.addVertexWithUV((double)(par2 + 1) - 0.015625D, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 0), (double)icon1.getMaxU(), (double)icon1.getMinV());
                tessellator.addVertexWithUV((double)(par2 + 1) - 0.015625D, (double)(par3 + 0), (double)(par4 + 0), (double)icon1.getMinU(), (double)icon1.getMinV());
                tessellator.setColorOpaque_F(f, f, f);
                tessellator.addVertexWithUV((double)(par2 + 1) - 0.015625D, (double)(par3 + 0), (double)(par4 + 1), (double)icon3.getMinU(), (double)icon3.getMaxV());
                tessellator.addVertexWithUV((double)(par2 + 1) - 0.015625D, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1), (double)icon3.getMaxU(), (double)icon3.getMaxV());
                tessellator.addVertexWithUV((double)(par2 + 1) - 0.015625D, (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 0), (double)icon3.getMaxU(), (double)icon3.getMinV());
                tessellator.addVertexWithUV((double)(par2 + 1) - 0.015625D, (double)(par3 + 0), (double)(par4 + 0), (double)icon3.getMinU(), (double)icon3.getMinV());
            }

            if (this.blockAccess.isBlockNormalCube(par2, par3, par4 - 1) && this.blockAccess.getBlockId(par2, par3 + 1, par4 - 1) == Block.redstoneWire.blockID)
            {
                tessellator.setColorOpaque_F(f * f2, f * f3, f * f4);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)par4 + 0.015625D, (double)icon1.getMinU(), (double)icon1.getMaxV());
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 1) + 0.021875F), (double)par4 + 0.015625D, (double)icon1.getMaxU(), (double)icon1.getMaxV());
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 1) + 0.021875F), (double)par4 + 0.015625D, (double)icon1.getMaxU(), (double)icon1.getMinV());
                tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)par4 + 0.015625D, (double)icon1.getMinU(), (double)icon1.getMinV());
                tessellator.setColorOpaque_F(f, f, f);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)par4 + 0.015625D, (double)icon3.getMinU(), (double)icon3.getMaxV());
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 1) + 0.021875F), (double)par4 + 0.015625D, (double)icon3.getMaxU(), (double)icon3.getMaxV());
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 1) + 0.021875F), (double)par4 + 0.015625D, (double)icon3.getMaxU(), (double)icon3.getMinV());
                tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)par4 + 0.015625D, (double)icon3.getMinU(), (double)icon3.getMinV());
            }

            if (this.blockAccess.isBlockNormalCube(par2, par3, par4 + 1) && this.blockAccess.getBlockId(par2, par3 + 1, par4 + 1) == Block.redstoneWire.blockID)
            {
                tessellator.setColorOpaque_F(f * f2, f * f3, f * f4);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1) - 0.015625D, (double)icon1.getMaxU(), (double)icon1.getMinV());
                tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)(par4 + 1) - 0.015625D, (double)icon1.getMinU(), (double)icon1.getMinV());
                tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)(par4 + 1) - 0.015625D, (double)icon1.getMinU(), (double)icon1.getMaxV());
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1) - 0.015625D, (double)icon1.getMaxU(), (double)icon1.getMaxV());
                tessellator.setColorOpaque_F(f, f, f);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1) - 0.015625D, (double)icon3.getMaxU(), (double)icon3.getMinV());
                tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)(par4 + 1) - 0.015625D, (double)icon3.getMinU(), (double)icon3.getMinV());
                tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)(par4 + 1) - 0.015625D, (double)icon3.getMinU(), (double)icon3.getMaxV());
                tessellator.addVertexWithUV((double)(par2 + 0), (double)((float)(par3 + 1) + 0.021875F), (double)(par4 + 1) - 0.015625D, (double)icon3.getMaxU(), (double)icon3.getMaxV());
            }
        }

        return true;
    }

    /**
     * Renders a minecart track block at the given coordinates
     */
    public boolean renderBlockMinecartTrack(final BlockRailBase par1BlockRailBase, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        Icon icon = this.getBlockIconFromSideAndMetadata(par1BlockRailBase, 0, l);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        if (par1BlockRailBase.isPowered())
        {
            l &= 7;
        }

        tessellator.setBrightness(par1BlockRailBase.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        final double d0 = (double)icon.getMinU();
        final double d1 = (double)icon.getMinV();
        final double d2 = (double)icon.getMaxU();
        final double d3 = (double)icon.getMaxV();
        final double d4 = 0.0625D;
        double d5 = (double)(par2 + 1);
        double d6 = (double)(par2 + 1);
        double d7 = (double)(par2 + 0);
        double d8 = (double)(par2 + 0);
        double d9 = (double)(par4 + 0);
        double d10 = (double)(par4 + 1);
        double d11 = (double)(par4 + 1);
        double d12 = (double)(par4 + 0);
        double d13 = (double)par3 + d4;
        double d14 = (double)par3 + d4;
        double d15 = (double)par3 + d4;
        double d16 = (double)par3 + d4;

        if (l != 1 && l != 2 && l != 3 && l != 7)
        {
            if (l == 8)
            {
                d5 = d6 = (double)(par2 + 0);
                d7 = d8 = (double)(par2 + 1);
                d9 = d12 = (double)(par4 + 1);
                d10 = d11 = (double)(par4 + 0);
            }
            else if (l == 9)
            {
                d5 = d8 = (double)(par2 + 0);
                d6 = d7 = (double)(par2 + 1);
                d9 = d10 = (double)(par4 + 0);
                d11 = d12 = (double)(par4 + 1);
            }
        }
        else
        {
            d5 = d8 = (double)(par2 + 1);
            d6 = d7 = (double)(par2 + 0);
            d9 = d10 = (double)(par4 + 1);
            d11 = d12 = (double)(par4 + 0);
        }

        if (l != 2 && l != 4)
        {
            if (l == 3 || l == 5)
            {
                ++d14;
                ++d15;
            }
        }
        else
        {
            ++d13;
            ++d16;
        }

        tessellator.addVertexWithUV(d5, d13, d9, d2, d1);
        tessellator.addVertexWithUV(d6, d14, d10, d2, d3);
        tessellator.addVertexWithUV(d7, d15, d11, d0, d3);
        tessellator.addVertexWithUV(d8, d16, d12, d0, d1);
        tessellator.addVertexWithUV(d8, d16, d12, d0, d1);
        tessellator.addVertexWithUV(d7, d15, d11, d0, d3);
        tessellator.addVertexWithUV(d6, d14, d10, d2, d3);
        tessellator.addVertexWithUV(d5, d13, d9, d2, d1);
        return true;
    }

    /**
     * Renders a ladder block at the given coordinates
     */
    public boolean renderBlockLadder(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        Icon icon = this.getBlockIconFromSide(par1Block, 0);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = 1.0F;
        tessellator.setColorOpaque_F(f, f, f);
        final double d0 = (double)icon.getMinU();
        final double d1 = (double)icon.getMinV();
        final double d2 = (double)icon.getMaxU();
        final double d3 = (double)icon.getMaxV();
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final double d4 = 0.0D;
        final double d5 = 0.05000000074505806D;

        if (l == 5)
        {
            tessellator.addVertexWithUV((double)par2 + d5, (double)(par3 + 1) + d4, (double)(par4 + 1) + d4, d0, d1);
            tessellator.addVertexWithUV((double)par2 + d5, (double)(par3 + 0) - d4, (double)(par4 + 1) + d4, d0, d3);
            tessellator.addVertexWithUV((double)par2 + d5, (double)(par3 + 0) - d4, (double)(par4 + 0) - d4, d2, d3);
            tessellator.addVertexWithUV((double)par2 + d5, (double)(par3 + 1) + d4, (double)(par4 + 0) - d4, d2, d1);
        }

        if (l == 4)
        {
            tessellator.addVertexWithUV((double)(par2 + 1) - d5, (double)(par3 + 0) - d4, (double)(par4 + 1) + d4, d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 1) - d5, (double)(par3 + 1) + d4, (double)(par4 + 1) + d4, d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 1) - d5, (double)(par3 + 1) + d4, (double)(par4 + 0) - d4, d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 1) - d5, (double)(par3 + 0) - d4, (double)(par4 + 0) - d4, d0, d3);
        }

        if (l == 3)
        {
            tessellator.addVertexWithUV((double)(par2 + 1) + d4, (double)(par3 + 0) - d4, (double)par4 + d5, d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 1) + d4, (double)(par3 + 1) + d4, (double)par4 + d5, d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 0) - d4, (double)(par3 + 1) + d4, (double)par4 + d5, d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 0) - d4, (double)(par3 + 0) - d4, (double)par4 + d5, d0, d3);
        }

        if (l == 2)
        {
            tessellator.addVertexWithUV((double)(par2 + 1) + d4, (double)(par3 + 1) + d4, (double)(par4 + 1) - d5, d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 1) + d4, (double)(par3 + 0) - d4, (double)(par4 + 1) - d5, d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 0) - d4, (double)(par3 + 0) - d4, (double)(par4 + 1) - d5, d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 0) - d4, (double)(par3 + 1) + d4, (double)(par4 + 1) - d5, d2, d1);
        }

        return true;
    }

    /**
     * Render block vine
     */
    public boolean renderBlockVine(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        Icon icon = this.getBlockIconFromSide(par1Block, 0);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        final float f = 1.0F;
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final int l = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
        final float f1 = (float)(l >> 16 & 255) / 255.0F;
        final float f2 = (float)(l >> 8 & 255) / 255.0F;
        final float f3 = (float)(l & 255) / 255.0F;
        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        final double d0 = (double)icon.getMinU();
        final double d1 = (double)icon.getMinV();
        final double d2 = (double)icon.getMaxU();
        final double d3 = (double)icon.getMaxV();
        final double d4 = 0.05000000074505806D;
        final int i1 = this.blockAccess.getBlockMetadata(par2, par3, par4);

        if ((i1 & 2) != 0)
        {
            tessellator.addVertexWithUV((double)par2 + d4, (double)(par3 + 1), (double)(par4 + 1), d0, d1);
            tessellator.addVertexWithUV((double)par2 + d4, (double)(par3 + 0), (double)(par4 + 1), d0, d3);
            tessellator.addVertexWithUV((double)par2 + d4, (double)(par3 + 0), (double)(par4 + 0), d2, d3);
            tessellator.addVertexWithUV((double)par2 + d4, (double)(par3 + 1), (double)(par4 + 0), d2, d1);
            tessellator.addVertexWithUV((double)par2 + d4, (double)(par3 + 1), (double)(par4 + 0), d2, d1);
            tessellator.addVertexWithUV((double)par2 + d4, (double)(par3 + 0), (double)(par4 + 0), d2, d3);
            tessellator.addVertexWithUV((double)par2 + d4, (double)(par3 + 0), (double)(par4 + 1), d0, d3);
            tessellator.addVertexWithUV((double)par2 + d4, (double)(par3 + 1), (double)(par4 + 1), d0, d1);
        }

        if ((i1 & 8) != 0)
        {
            tessellator.addVertexWithUV((double)(par2 + 1) - d4, (double)(par3 + 0), (double)(par4 + 1), d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 1) - d4, (double)(par3 + 1), (double)(par4 + 1), d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 1) - d4, (double)(par3 + 1), (double)(par4 + 0), d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 1) - d4, (double)(par3 + 0), (double)(par4 + 0), d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 1) - d4, (double)(par3 + 0), (double)(par4 + 0), d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 1) - d4, (double)(par3 + 1), (double)(par4 + 0), d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 1) - d4, (double)(par3 + 1), (double)(par4 + 1), d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 1) - d4, (double)(par3 + 0), (double)(par4 + 1), d2, d3);
        }

        if ((i1 & 4) != 0)
        {
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)par4 + d4, d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1), (double)par4 + d4, d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1), (double)par4 + d4, d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)par4 + d4, d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)par4 + d4, d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1), (double)par4 + d4, d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1), (double)par4 + d4, d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)par4 + d4, d2, d3);
        }

        if ((i1 & 1) != 0)
        {
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1), (double)(par4 + 1) - d4, d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)(par4 + 1) - d4, d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)(par4 + 1) - d4, d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1), (double)(par4 + 1) - d4, d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1), (double)(par4 + 1) - d4, d2, d1);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)(par4 + 1) - d4, d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 0), (double)(par4 + 1) - d4, d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1), (double)(par4 + 1) - d4, d0, d1);
        }

        if (this.blockAccess.isBlockNormalCube(par2, par3 + 1, par4))
        {
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1) - d4, (double)(par4 + 0), d0, d1);
            tessellator.addVertexWithUV((double)(par2 + 1), (double)(par3 + 1) - d4, (double)(par4 + 1), d0, d3);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1) - d4, (double)(par4 + 1), d2, d3);
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 1) - d4, (double)(par4 + 0), d2, d1);
        }

        return true;
    }

    public boolean renderBlockPane(final BlockPane par1BlockPane, final int par2, final int par3, final int par4)
    {
        final int l = this.blockAccess.getHeight();
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1BlockPane.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = 1.0F;
        final int i1 = par1BlockPane.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f1 = (float)(i1 >> 16 & 255) / 255.0F;
        float f2 = (float)(i1 >> 8 & 255) / 255.0F;
        float f3 = (float)(i1 & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            final float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            final float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            final float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        final Icon icon;
        final Icon icon1;

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
            icon1 = this.overrideBlockTexture;
        }
        else
        {
            final int j1 = this.blockAccess.getBlockMetadata(par2, par3, par4);
            icon = this.getBlockIconFromSideAndMetadata(par1BlockPane, 0, j1);
            icon1 = par1BlockPane.getSideTextureIndex();
        }

        final double d0 = (double)icon.getMinU();
        final double d1 = (double)icon.getInterpolatedU(8.0D);
        final double d2 = (double)icon.getMaxU();
        final double d3 = (double)icon.getMinV();
        final double d4 = (double)icon.getMaxV();
        final double d5 = (double)icon1.getInterpolatedU(7.0D);
        final double d6 = (double)icon1.getInterpolatedU(9.0D);
        final double d7 = (double)icon1.getMinV();
        final double d8 = (double)icon1.getInterpolatedV(8.0D);
        final double d9 = (double)icon1.getMaxV();
        final double d10 = (double)par2;
        final double d11 = (double)par2 + 0.5D;
        final double d12 = (double)(par2 + 1);
        final double d13 = (double)par4;
        final double d14 = (double)par4 + 0.5D;
        final double d15 = (double)(par4 + 1);
        final double d16 = (double)par2 + 0.5D - 0.0625D;
        final double d17 = (double)par2 + 0.5D + 0.0625D;
        final double d18 = (double)par4 + 0.5D - 0.0625D;
        final double d19 = (double)par4 + 0.5D + 0.0625D;
        final boolean flag = par1BlockPane.canPaneConnectTo(this.blockAccess,par2, par3, par4, NORTH);
        final boolean flag1 = par1BlockPane.canPaneConnectTo(this.blockAccess,par2, par3, par4, SOUTH);
        final boolean flag2 = par1BlockPane.canPaneConnectTo(this.blockAccess,par2, par3, par4, WEST);
        final boolean flag3 = par1BlockPane.canPaneConnectTo(this.blockAccess,par2, par3, par4, EAST);
        final boolean flag4 = par1BlockPane.shouldSideBeRendered(this.blockAccess, par2, par3 + 1, par4, 1);
        final boolean flag5 = par1BlockPane.shouldSideBeRendered(this.blockAccess, par2, par3 - 1, par4, 0);
        final double d20 = 0.01D;
        final double d21 = 0.005D;

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1))
        {
            if (flag2 && !flag3)
            {
                tessellator.addVertexWithUV(d10, (double)(par3 + 1), d14, d0, d3);
                tessellator.addVertexWithUV(d10, (double)(par3 + 0), d14, d0, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d14, d1, d3);
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d14, d0, d3);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d14, d0, d4);
                tessellator.addVertexWithUV(d10, (double)(par3 + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d10, (double)(par3 + 1), d14, d1, d3);

                if (!flag1 && !flag)
                {
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1), d19, d5, d7);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 0), d19, d5, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 0), d18, d6, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1), d18, d6, d7);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1), d18, d5, d7);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 0), d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 0), d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1), d19, d6, d7);
                }

                if (flag4 || par3 < l - 1 && this.blockAccess.isAirBlock(par2 - 1, par3 + 1, par4))
                {
                    tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d18, d5, d8);
                }

                if (flag5 || par3 > 1 && this.blockAccess.isAirBlock(par2 - 1, par3 - 1, par4))
                {
                    tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d18, d5, d8);
                }
            }
            else if (!flag2 && flag3)
            {
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d14, d1, d3);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d12, (double)(par3 + 0), d14, d2, d4);
                tessellator.addVertexWithUV(d12, (double)(par3 + 1), d14, d2, d3);
                tessellator.addVertexWithUV(d12, (double)(par3 + 1), d14, d1, d3);
                tessellator.addVertexWithUV(d12, (double)(par3 + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d14, d2, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d14, d2, d3);

                if (!flag1 && !flag)
                {
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1), d18, d5, d7);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 0), d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 0), d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1), d19, d6, d7);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1), d19, d5, d7);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 0), d19, d5, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 0), d18, d6, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1), d18, d6, d7);
                }

                if (flag4 || par3 < l - 1 && this.blockAccess.isAirBlock(par2 + 1, par3 + 1, par4))
                {
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d18, d5, d7);
                }

                if (flag5 || par3 > 1 && this.blockAccess.isAirBlock(par2 + 1, par3 - 1, par4))
                {
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d18, d5, d7);
                }
            }
        }
        else
        {
            tessellator.addVertexWithUV(d10, (double)(par3 + 1), d14, d0, d3);
            tessellator.addVertexWithUV(d10, (double)(par3 + 0), d14, d0, d4);
            tessellator.addVertexWithUV(d12, (double)(par3 + 0), d14, d2, d4);
            tessellator.addVertexWithUV(d12, (double)(par3 + 1), d14, d2, d3);
            tessellator.addVertexWithUV(d12, (double)(par3 + 1), d14, d0, d3);
            tessellator.addVertexWithUV(d12, (double)(par3 + 0), d14, d0, d4);
            tessellator.addVertexWithUV(d10, (double)(par3 + 0), d14, d2, d4);
            tessellator.addVertexWithUV(d10, (double)(par3 + 1), d14, d2, d3);

            if (flag4)
            {
                tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d19, d6, d9);
                tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d19, d6, d7);
                tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d18, d5, d7);
                tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d18, d5, d9);
                tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d19, d6, d9);
                tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d19, d6, d7);
                tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d18, d5, d7);
                tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d18, d5, d9);
            }
            else
            {
                if (par3 < l - 1 && this.blockAccess.isAirBlock(par2 - 1, par3 + 1, par4))
                {
                    tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d10, (double)(par3 + 1) + 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d18, d5, d8);
                }

                if (par3 < l - 1 && this.blockAccess.isAirBlock(par2 + 1, par3 + 1, par4))
                {
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double)(par3 + 1) + 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d12, (double)(par3 + 1) + 0.01D, d18, d5, d7);
                }
            }

            if (flag5)
            {
                tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d19, d6, d9);
                tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d19, d6, d7);
                tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d18, d5, d7);
                tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d18, d5, d9);
                tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d19, d6, d9);
                tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d19, d6, d7);
                tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d18, d5, d7);
                tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d18, d5, d9);
            }
            else
            {
                if (par3 > 1 && this.blockAccess.isAirBlock(par2 - 1, par3 - 1, par4))
                {
                    tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d19, d6, d9);
                    tessellator.addVertexWithUV(d10, (double)par3 - 0.01D, d18, d5, d9);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d18, d5, d8);
                }

                if (par3 > 1 && this.blockAccess.isAirBlock(par2 + 1, par3 - 1, par4))
                {
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d18, d5, d7);
                    tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d19, d6, d7);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d19, d6, d8);
                    tessellator.addVertexWithUV(d11, (double)par3 - 0.01D, d18, d5, d8);
                    tessellator.addVertexWithUV(d12, (double)par3 - 0.01D, d18, d5, d7);
                }
            }
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1))
        {
            if (flag && !flag1)
            {
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d13, d0, d3);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d13, d0, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d14, d1, d3);
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d14, d0, d3);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d14, d0, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d13, d1, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d13, d1, d3);

                if (!flag3 && !flag2)
                {
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1), d14, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 0), d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 0), d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1), d14, d6, d7);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1), d14, d5, d7);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 0), d14, d5, d9);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 0), d14, d6, d9);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1), d14, d6, d7);
                }

                if (flag4 || par3 < l - 1 && this.blockAccess.isAirBlock(par2, par3 + 1, par4 - 1))
                {
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d13, d6, d7);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d14, d6, d7);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d13, d6, d8);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d14, d5, d7);
                }

                if (flag5 || par3 > 1 && this.blockAccess.isAirBlock(par2, par3 - 1, par4 - 1))
                {
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d13, d6, d7);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d14, d6, d7);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d13, d6, d8);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d14, d5, d7);
                }
            }
            else if (!flag && flag1)
            {
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d14, d1, d3);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d14, d1, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d15, d2, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d15, d2, d3);
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d15, d1, d3);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d15, d1, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 0), d14, d2, d4);
                tessellator.addVertexWithUV(d11, (double)(par3 + 1), d14, d2, d3);

                if (!flag3 && !flag2)
                {
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1), d14, d5, d7);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 0), d14, d5, d9);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 0), d14, d6, d9);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1), d14, d6, d7);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1), d14, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 0), d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 0), d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1), d14, d6, d7);
                }

                if (flag4 || par3 < l - 1 && this.blockAccess.isAirBlock(par2, par3 + 1, par4 + 1))
                {
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d15, d5, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d15, d6, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d15, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d15, d6, d8);
                }

                if (flag5 || par3 > 1 && this.blockAccess.isAirBlock(par2, par3 - 1, par4 + 1))
                {
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d15, d5, d9);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d15, d6, d9);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d15, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d15, d6, d8);
                }
            }
        }
        else
        {
            tessellator.addVertexWithUV(d11, (double)(par3 + 1), d15, d0, d3);
            tessellator.addVertexWithUV(d11, (double)(par3 + 0), d15, d0, d4);
            tessellator.addVertexWithUV(d11, (double)(par3 + 0), d13, d2, d4);
            tessellator.addVertexWithUV(d11, (double)(par3 + 1), d13, d2, d3);
            tessellator.addVertexWithUV(d11, (double)(par3 + 1), d13, d0, d3);
            tessellator.addVertexWithUV(d11, (double)(par3 + 0), d13, d0, d4);
            tessellator.addVertexWithUV(d11, (double)(par3 + 0), d15, d2, d4);
            tessellator.addVertexWithUV(d11, (double)(par3 + 1), d15, d2, d3);

            if (flag4)
            {
                tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d15, d6, d9);
                tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d13, d6, d7);
                tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d13, d5, d7);
                tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d15, d5, d9);
                tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d13, d6, d9);
                tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d15, d6, d7);
                tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d15, d5, d7);
                tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d13, d5, d9);
            }
            else
            {
                if (par3 < l - 1 && this.blockAccess.isAirBlock(par2, par3 + 1, par4 - 1))
                {
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d13, d6, d7);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d14, d6, d7);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d13, d6, d8);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d14, d5, d7);
                }

                if (par3 < l - 1 && this.blockAccess.isAirBlock(par2, par3 + 1, par4 + 1))
                {
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d15, d5, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d15, d6, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d15, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 1) + 0.005D, d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double)(par3 + 1) + 0.005D, d15, d6, d8);
                }
            }

            if (flag5)
            {
                tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d15, d6, d9);
                tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d13, d6, d7);
                tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d13, d5, d7);
                tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d15, d5, d9);
                tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d13, d6, d9);
                tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d15, d6, d7);
                tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d15, d5, d7);
                tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d13, d5, d9);
            }
            else
            {
                if (par3 > 1 && this.blockAccess.isAirBlock(par2, par3 - 1, par4 - 1))
                {
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d13, d6, d7);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d13, d5, d7);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d14, d6, d7);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d13, d6, d8);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d13, d5, d8);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d14, d5, d7);
                }

                if (par3 > 1 && this.blockAccess.isAirBlock(par2, par3 - 1, par4 + 1))
                {
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d14, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d15, d5, d9);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d15, d6, d9);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d14, d6, d8);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d15, d5, d8);
                    tessellator.addVertexWithUV(d16, (double)par3 - 0.005D, d14, d5, d9);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d14, d6, d9);
                    tessellator.addVertexWithUV(d17, (double)par3 - 0.005D, d15, d6, d8);
                }
            }
        }

        return true;
    }

    /**
     * Renders any block requiring croseed squares such as reeds, flowers, and mushrooms
     */
    public boolean renderCrossedSquares(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = 1.0F;
        final int l = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f1 = (float)(l >> 16 & 255) / 255.0F;
        float f2 = (float)(l >> 8 & 255) / 255.0F;
        float f3 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            final float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            final float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            final float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        double d0 = (double)par2;
        double d1 = (double)par3;
        double d2 = (double)par4;

        if (par1Block == Block.tallGrass)
        {
            long i1 = (long)(par2 * 3129871) ^ (long)par4 * 116129781L ^ (long)par3;
            i1 = i1 * i1 * 42317861L + i1 * 11L;
            d0 += ((double)((float)(i1 >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
            d1 += ((double)((float)(i1 >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
            d2 += ((double)((float)(i1 >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
        }

        this.drawCrossedSquares(par1Block, this.blockAccess.getBlockMetadata(par2, par3, par4), d0, d1, d2, 1.0F);
        return true;
    }

    /**
     * Render block stem
     */
    public boolean renderBlockStem(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final BlockStem blockstem = (BlockStem)par1Block;
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(blockstem.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = 1.0F;
        final int l = blockstem.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f1 = (float)(l >> 16 & 255) / 255.0F;
        float f2 = (float)(l >> 8 & 255) / 255.0F;
        float f3 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            final float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            final float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            final float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        blockstem.setBlockBoundsBasedOnState(this.blockAccess, par2, par3, par4);
        final int i1 = blockstem.getState(this.blockAccess, par2, par3, par4);

        if (i1 < 0)
        {
            this.renderBlockStemSmall(blockstem, this.blockAccess.getBlockMetadata(par2, par3, par4), this.renderMaxY, (double)par2, (double)((float)par3 - 0.0625F), (double)par4);
        }
        else
        {
            this.renderBlockStemSmall(blockstem, this.blockAccess.getBlockMetadata(par2, par3, par4), 0.5D, (double)par2, (double)((float)par3 - 0.0625F), (double)par4);
            this.renderBlockStemBig(blockstem, this.blockAccess.getBlockMetadata(par2, par3, par4), i1, this.renderMaxY, (double)par2, (double)((float)par3 - 0.0625F), (double)par4);
        }

        return true;
    }

    /**
     * Render block crops
     */
    public boolean renderBlockCrops(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        this.renderBlockCropsImpl(par1Block, this.blockAccess.getBlockMetadata(par2, par3, par4), (double)par2, (double)((float)par3 - 0.0625F), (double)par4);
        return true;
    }

    /**
     * Renders a torch at the given coordinates, with the base slanting at the given delta
     */
    public void renderTorchAtAngle(final Block par1Block, double par2, final double par4, double par6, final double par8, final double par10, final int par12)
    {
        double par21 = par2;
        double par61 = par6;
        final Tessellator tessellator = Tessellator.instance;
        Icon icon = this.getBlockIconFromSideAndMetadata(par1Block, 0, par12);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        final double d5 = (double)icon.getMinU();
        final double d6 = (double)icon.getMinV();
        final double d7 = (double)icon.getMaxU();
        final double d8 = (double)icon.getMaxV();
        final double d9 = (double)icon.getInterpolatedU(7.0D);
        final double d10 = (double)icon.getInterpolatedV(6.0D);
        final double d11 = (double)icon.getInterpolatedU(9.0D);
        final double d12 = (double)icon.getInterpolatedV(8.0D);
        final double d13 = (double)icon.getInterpolatedU(7.0D);
        final double d14 = (double)icon.getInterpolatedV(13.0D);
        final double d15 = (double)icon.getInterpolatedU(9.0D);
        final double d16 = (double)icon.getInterpolatedV(15.0D);
        par21 += 0.5D;
        par61 += 0.5D;
        final double d17 = par21 - 0.5D;
        final double d18 = par21 + 0.5D;
        final double d19 = par61 - 0.5D;
        final double d20 = par61 + 0.5D;
        final double d21 = 0.0625D;
        final double d22 = 0.625D;
        tessellator.addVertexWithUV(par21 + par8 * (1.0D - d22) - d21, par4 + d22, par61 + par10 * (1.0D - d22) - d21, d9, d10);
        tessellator.addVertexWithUV(par21 + par8 * (1.0D - d22) - d21, par4 + d22, par61 + par10 * (1.0D - d22) + d21, d9, d12);
        tessellator.addVertexWithUV(par21 + par8 * (1.0D - d22) + d21, par4 + d22, par61 + par10 * (1.0D - d22) + d21, d11, d12);
        tessellator.addVertexWithUV(par21 + par8 * (1.0D - d22) + d21, par4 + d22, par61 + par10 * (1.0D - d22) - d21, d11, d10);
        tessellator.addVertexWithUV(par21 + d21 + par8, par4, par61 - d21 + par10, d15, d14);
        tessellator.addVertexWithUV(par21 + d21 + par8, par4, par61 + d21 + par10, d15, d16);
        tessellator.addVertexWithUV(par21 - d21 + par8, par4, par61 + d21 + par10, d13, d16);
        tessellator.addVertexWithUV(par21 - d21 + par8, par4, par61 - d21 + par10, d13, d14);
        tessellator.addVertexWithUV(par21 - d21, par4 + 1.0D, d19, d5, d6);
        tessellator.addVertexWithUV(par21 - d21 + par8, par4 + 0.0D, d19 + par10, d5, d8);
        tessellator.addVertexWithUV(par21 - d21 + par8, par4 + 0.0D, d20 + par10, d7, d8);
        tessellator.addVertexWithUV(par21 - d21, par4 + 1.0D, d20, d7, d6);
        tessellator.addVertexWithUV(par21 + d21, par4 + 1.0D, d20, d5, d6);
        tessellator.addVertexWithUV(par21 + par8 + d21, par4 + 0.0D, d20 + par10, d5, d8);
        tessellator.addVertexWithUV(par21 + par8 + d21, par4 + 0.0D, d19 + par10, d7, d8);
        tessellator.addVertexWithUV(par21 + d21, par4 + 1.0D, d19, d7, d6);
        tessellator.addVertexWithUV(d17, par4 + 1.0D, par61 + d21, d5, d6);
        tessellator.addVertexWithUV(d17 + par8, par4 + 0.0D, par61 + d21 + par10, d5, d8);
        tessellator.addVertexWithUV(d18 + par8, par4 + 0.0D, par61 + d21 + par10, d7, d8);
        tessellator.addVertexWithUV(d18, par4 + 1.0D, par61 + d21, d7, d6);
        tessellator.addVertexWithUV(d18, par4 + 1.0D, par61 - d21, d5, d6);
        tessellator.addVertexWithUV(d18 + par8, par4 + 0.0D, par61 - d21 + par10, d5, d8);
        tessellator.addVertexWithUV(d17 + par8, par4 + 0.0D, par61 - d21 + par10, d7, d8);
        tessellator.addVertexWithUV(d17, par4 + 1.0D, par61 - d21, d7, d6);
    }

    /**
     * Utility function to draw crossed swuares
     */
    public void drawCrossedSquares(final Block par1Block, final int par2, final double par3, final double par5, final double par7, final float par9)
    {
        final Tessellator tessellator = Tessellator.instance;
        Icon icon = this.getBlockIconFromSideAndMetadata(par1Block, 0, par2);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        final double d3 = (double)icon.getMinU();
        final double d4 = (double)icon.getMinV();
        final double d5 = (double)icon.getMaxU();
        final double d6 = (double)icon.getMaxV();
        final double d7 = 0.45D * (double)par9;
        final double d8 = par3 + 0.5D - d7;
        final double d9 = par3 + 0.5D + d7;
        final double d10 = par7 + 0.5D - d7;
        final double d11 = par7 + 0.5D + d7;
        tessellator.addVertexWithUV(d8, par5 + (double)par9, d10, d3, d4);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d9, par5 + 0.0D, d11, d5, d6);
        tessellator.addVertexWithUV(d9, par5 + (double)par9, d11, d5, d4);
        tessellator.addVertexWithUV(d9, par5 + (double)par9, d11, d3, d4);
        tessellator.addVertexWithUV(d9, par5 + 0.0D, d11, d3, d6);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d8, par5 + (double)par9, d10, d5, d4);
        tessellator.addVertexWithUV(d8, par5 + (double)par9, d11, d3, d4);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d11, d3, d6);
        tessellator.addVertexWithUV(d9, par5 + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d9, par5 + (double)par9, d10, d5, d4);
        tessellator.addVertexWithUV(d9, par5 + (double)par9, d10, d3, d4);
        tessellator.addVertexWithUV(d9, par5 + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d11, d5, d6);
        tessellator.addVertexWithUV(d8, par5 + (double)par9, d11, d5, d4);
    }

    /**
     * Render block stem small
     */
    public void renderBlockStemSmall(final Block par1Block, final int par2, final double par3, final double par5, final double par7, final double par9)
    {
        final Tessellator tessellator = Tessellator.instance;
        Icon icon = this.getBlockIconFromSideAndMetadata(par1Block, 0, par2);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        final double d4 = (double)icon.getMinU();
        final double d5 = (double)icon.getMinV();
        final double d6 = (double)icon.getMaxU();
        final double d7 = (double)icon.getInterpolatedV(par3 * 16.0D);
        final double d8 = par5 + 0.5D - 0.44999998807907104D;
        final double d9 = par5 + 0.5D + 0.44999998807907104D;
        final double d10 = par9 + 0.5D - 0.44999998807907104D;
        final double d11 = par9 + 0.5D + 0.44999998807907104D;
        tessellator.addVertexWithUV(d8, par7 + par3, d10, d4, d5);
        tessellator.addVertexWithUV(d8, par7 + 0.0D, d10, d4, d7);
        tessellator.addVertexWithUV(d9, par7 + 0.0D, d11, d6, d7);
        tessellator.addVertexWithUV(d9, par7 + par3, d11, d6, d5);
        tessellator.addVertexWithUV(d9, par7 + par3, d11, d4, d5);
        tessellator.addVertexWithUV(d9, par7 + 0.0D, d11, d4, d7);
        tessellator.addVertexWithUV(d8, par7 + 0.0D, d10, d6, d7);
        tessellator.addVertexWithUV(d8, par7 + par3, d10, d6, d5);
        tessellator.addVertexWithUV(d8, par7 + par3, d11, d4, d5);
        tessellator.addVertexWithUV(d8, par7 + 0.0D, d11, d4, d7);
        tessellator.addVertexWithUV(d9, par7 + 0.0D, d10, d6, d7);
        tessellator.addVertexWithUV(d9, par7 + par3, d10, d6, d5);
        tessellator.addVertexWithUV(d9, par7 + par3, d10, d4, d5);
        tessellator.addVertexWithUV(d9, par7 + 0.0D, d10, d4, d7);
        tessellator.addVertexWithUV(d8, par7 + 0.0D, d11, d6, d7);
        tessellator.addVertexWithUV(d8, par7 + par3, d11, d6, d5);
    }

    /**
     * Render BlockLilyPad
     */
    public boolean renderBlockLilyPad(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        Icon icon = this.getBlockIconFromSide(par1Block, 1);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        final float f = 0.015625F;
        final double d0 = (double)icon.getMinU();
        final double d1 = (double)icon.getMinV();
        final double d2 = (double)icon.getMaxU();
        final double d3 = (double)icon.getMaxV();
        long l = (long)(par2 * 3129871) ^ (long)par4 * 116129781L ^ (long)par3;
        l = l * l * 42317861L + l * 11L;
        final int i1 = (int)(l >> 16 & 3L);
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f1 = (float)par2 + 0.5F;
        final float f2 = (float)par4 + 0.5F;
        final float f3 = (float)(i1 & 1) * 0.5F * (float)(1 - i1 / 2 % 2 * 2);
        final float f4 = (float)(i1 + 1 & 1) * 0.5F * (float)(1 - (i1 + 1) / 2 % 2 * 2);
        tessellator.setColorOpaque_I(par1Block.getBlockColor());
        tessellator.addVertexWithUV((double)(f1 + f3 - f4), (double)((float)par3 + f), (double)(f2 + f3 + f4), d0, d1);
        tessellator.addVertexWithUV((double)(f1 + f3 + f4), (double)((float)par3 + f), (double)(f2 - f3 + f4), d2, d1);
        tessellator.addVertexWithUV((double)(f1 - f3 + f4), (double)((float)par3 + f), (double)(f2 - f3 - f4), d2, d3);
        tessellator.addVertexWithUV((double)(f1 - f3 - f4), (double)((float)par3 + f), (double)(f2 + f3 - f4), d0, d3);
        tessellator.setColorOpaque_I((par1Block.getBlockColor() & 16711422) >> 1);
        tessellator.addVertexWithUV((double)(f1 - f3 - f4), (double)((float)par3 + f), (double)(f2 + f3 - f4), d0, d3);
        tessellator.addVertexWithUV((double)(f1 - f3 + f4), (double)((float)par3 + f), (double)(f2 - f3 - f4), d2, d3);
        tessellator.addVertexWithUV((double)(f1 + f3 + f4), (double)((float)par3 + f), (double)(f2 - f3 + f4), d2, d1);
        tessellator.addVertexWithUV((double)(f1 + f3 - f4), (double)((float)par3 + f), (double)(f2 + f3 + f4), d0, d1);
        return true;
    }

    /**
     * Render block stem big
     */
    public void renderBlockStemBig(final BlockStem par1BlockStem, final int par2, final int par3, final double par4, final double par6, final double par8, final double par10)
    {
        final Tessellator tessellator = Tessellator.instance;
        Icon icon = par1BlockStem.getStemIcon();

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        double d4 = (double)icon.getMinU();
        final double d5 = (double)icon.getMinV();
        double d6 = (double)icon.getMaxU();
        final double d7 = (double)icon.getMaxV();
        final double d8 = par6 + 0.5D - 0.5D;
        final double d9 = par6 + 0.5D + 0.5D;
        final double d10 = par10 + 0.5D - 0.5D;
        final double d11 = par10 + 0.5D + 0.5D;
        final double d12 = par6 + 0.5D;
        final double d13 = par10 + 0.5D;

        if ((par3 + 1) / 2 % 2 == 1)
        {
            final double d14 = d6;
            d6 = d4;
            d4 = d14;
        }

        if (par3 < 2)
        {
            tessellator.addVertexWithUV(d8, par8 + par4, d13, d4, d5);
            tessellator.addVertexWithUV(d8, par8 + 0.0D, d13, d4, d7);
            tessellator.addVertexWithUV(d9, par8 + 0.0D, d13, d6, d7);
            tessellator.addVertexWithUV(d9, par8 + par4, d13, d6, d5);
            tessellator.addVertexWithUV(d9, par8 + par4, d13, d6, d5);
            tessellator.addVertexWithUV(d9, par8 + 0.0D, d13, d6, d7);
            tessellator.addVertexWithUV(d8, par8 + 0.0D, d13, d4, d7);
            tessellator.addVertexWithUV(d8, par8 + par4, d13, d4, d5);
        }
        else
        {
            tessellator.addVertexWithUV(d12, par8 + par4, d11, d4, d5);
            tessellator.addVertexWithUV(d12, par8 + 0.0D, d11, d4, d7);
            tessellator.addVertexWithUV(d12, par8 + 0.0D, d10, d6, d7);
            tessellator.addVertexWithUV(d12, par8 + par4, d10, d6, d5);
            tessellator.addVertexWithUV(d12, par8 + par4, d10, d6, d5);
            tessellator.addVertexWithUV(d12, par8 + 0.0D, d10, d6, d7);
            tessellator.addVertexWithUV(d12, par8 + 0.0D, d11, d4, d7);
            tessellator.addVertexWithUV(d12, par8 + par4, d11, d4, d5);
        }
    }

    /**
     * Render block crops implementation
     */
    public void renderBlockCropsImpl(final Block par1Block, final int par2, final double par3, final double par5, final double par7)
    {
        final Tessellator tessellator = Tessellator.instance;
        Icon icon = this.getBlockIconFromSideAndMetadata(par1Block, 0, par2);

        if (this.hasOverrideBlockTexture())
        {
            icon = this.overrideBlockTexture;
        }

        final double d3 = (double)icon.getMinU();
        final double d4 = (double)icon.getMinV();
        final double d5 = (double)icon.getMaxU();
        final double d6 = (double)icon.getMaxV();
        double d7 = par3 + 0.5D - 0.25D;
        double d8 = par3 + 0.5D + 0.25D;
        double d9 = par7 + 0.5D - 0.5D;
        double d10 = par7 + 0.5D + 0.5D;
        tessellator.addVertexWithUV(d7, par5 + 1.0D, d9, d3, d4);
        tessellator.addVertexWithUV(d7, par5 + 0.0D, d9, d3, d6);
        tessellator.addVertexWithUV(d7, par5 + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d7, par5 + 1.0D, d10, d5, d4);
        tessellator.addVertexWithUV(d7, par5 + 1.0D, d10, d3, d4);
        tessellator.addVertexWithUV(d7, par5 + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d7, par5 + 0.0D, d9, d5, d6);
        tessellator.addVertexWithUV(d7, par5 + 1.0D, d9, d5, d4);
        tessellator.addVertexWithUV(d8, par5 + 1.0D, d10, d3, d4);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d9, d5, d6);
        tessellator.addVertexWithUV(d8, par5 + 1.0D, d9, d5, d4);
        tessellator.addVertexWithUV(d8, par5 + 1.0D, d9, d3, d4);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d9, d3, d6);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d8, par5 + 1.0D, d10, d5, d4);
        d7 = par3 + 0.5D - 0.5D;
        d8 = par3 + 0.5D + 0.5D;
        d9 = par7 + 0.5D - 0.25D;
        d10 = par7 + 0.5D + 0.25D;
        tessellator.addVertexWithUV(d7, par5 + 1.0D, d9, d3, d4);
        tessellator.addVertexWithUV(d7, par5 + 0.0D, d9, d3, d6);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d9, d5, d6);
        tessellator.addVertexWithUV(d8, par5 + 1.0D, d9, d5, d4);
        tessellator.addVertexWithUV(d8, par5 + 1.0D, d9, d3, d4);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d9, d3, d6);
        tessellator.addVertexWithUV(d7, par5 + 0.0D, d9, d5, d6);
        tessellator.addVertexWithUV(d7, par5 + 1.0D, d9, d5, d4);
        tessellator.addVertexWithUV(d8, par5 + 1.0D, d10, d3, d4);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d7, par5 + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d7, par5 + 1.0D, d10, d5, d4);
        tessellator.addVertexWithUV(d7, par5 + 1.0D, d10, d3, d4);
        tessellator.addVertexWithUV(d7, par5 + 0.0D, d10, d3, d6);
        tessellator.addVertexWithUV(d8, par5 + 0.0D, d10, d5, d6);
        tessellator.addVertexWithUV(d8, par5 + 1.0D, d10, d5, d4);
    }

    /**
     * Renders a block based on the BlockFluids class at the given coordinates
     */
    public boolean renderBlockFluids(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        final int l = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
        final float f = (float)(l >> 16 & 255) / 255.0F;
        final float f1 = (float)(l >> 8 & 255) / 255.0F;
        final float f2 = (float)(l & 255) / 255.0F;
        final boolean flag = par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 + 1, par4, 1);
        final boolean flag1 = par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 - 1, par4, 0);
        final boolean[] aboolean = {par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 - 1, 2), par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 + 1, 3), par1Block.shouldSideBeRendered(this.blockAccess, par2 - 1, par3, par4, 4), par1Block.shouldSideBeRendered(this.blockAccess, par2 + 1, par3, par4, 5)};

        if (!flag && !flag1 && !aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3])
        {
            return false;
        }
        else
        {
            boolean flag2 = false;
            final float f3 = 0.5F;
            final float f4 = 1.0F;
            final float f5 = 0.8F;
            final float f6 = 0.6F;
            final double d0 = 0.0D;
            final double d1 = 1.0D;
            final Material material = par1Block.blockMaterial;
            final int i1 = this.blockAccess.getBlockMetadata(par2, par3, par4);
            double d2 = (double)this.getFluidHeight(par2, par3, par4, material);
            double d3 = (double)this.getFluidHeight(par2, par3, par4 + 1, material);
            double d4 = (double)this.getFluidHeight(par2 + 1, par3, par4 + 1, material);
            double d5 = (double)this.getFluidHeight(par2 + 1, par3, par4, material);
            final double d6 = 0.0010000000474974513D;
            float f7;
            float f8;
            float f9;

            if (this.renderAllFaces || flag)
            {
                flag2 = true;
                Icon icon = this.getBlockIconFromSideAndMetadata(par1Block, 1, i1);
                final float f10 = (float)BlockFluid.getFlowDirection(this.blockAccess, par2, par3, par4, material);

                if (f10 > -999.0F)
                {
                    icon = this.getBlockIconFromSideAndMetadata(par1Block, 2, i1);
                }

                d2 -= d6;
                d3 -= d6;
                d4 -= d6;
                d5 -= d6;
                final double d7;
                final double d8;
                final double d9;
                final double d10;
                final double d11;
                final double d12;
                final double d13;
                final double d14;

                if (f10 < -999.0F)
                {
                    d8 = (double)icon.getInterpolatedU(0.0D);
                    d12 = (double)icon.getInterpolatedV(0.0D);
                    d7 = d8;
                    d11 = (double)icon.getInterpolatedV(16.0D);
                    d10 = (double)icon.getInterpolatedU(16.0D);
                    d14 = d11;
                    d9 = d10;
                    d13 = d12;
                }
                else
                {
                    f9 = MathHelper.sin(f10) * 0.25F;
                    f8 = MathHelper.cos(f10) * 0.25F;
                    f7 = 8.0F;
                    d8 = (double)icon.getInterpolatedU((double)(8.0F + (-f8 - f9) * 16.0F));
                    d12 = (double)icon.getInterpolatedV((double)(8.0F + (-f8 + f9) * 16.0F));
                    d7 = (double)icon.getInterpolatedU((double)(8.0F + (-f8 + f9) * 16.0F));
                    d11 = (double)icon.getInterpolatedV((double)(8.0F + (f8 + f9) * 16.0F));
                    d10 = (double)icon.getInterpolatedU((double)(8.0F + (f8 + f9) * 16.0F));
                    d14 = (double)icon.getInterpolatedV((double)(8.0F + (f8 - f9) * 16.0F));
                    d9 = (double)icon.getInterpolatedU((double)(8.0F + (f8 - f9) * 16.0F));
                    d13 = (double)icon.getInterpolatedV((double)(8.0F + (-f8 - f9) * 16.0F));
                }

                tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
                f9 = 1.0F;
                tessellator.setColorOpaque_F(f4 * f9 * f, f4 * f9 * f1, f4 * f9 * f2);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)par3 + d2, (double)(par4 + 0), d8, d12);
                tessellator.addVertexWithUV((double)(par2 + 0), (double)par3 + d3, (double)(par4 + 1), d7, d11);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)par3 + d4, (double)(par4 + 1), d10, d14);
                tessellator.addVertexWithUV((double)(par2 + 1), (double)par3 + d5, (double)(par4 + 0), d9, d13);
            }

            if (this.renderAllFaces || flag1)
            {
                tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4));
                final float f11 = 1.0F;
                tessellator.setColorOpaque_F(f3 * f11, f3 * f11, f3 * f11);
                this.renderFaceYNeg(par1Block, (double)par2, (double)par3 + d6, (double)par4, this.getBlockIconFromSide(par1Block, 0));
                flag2 = true;
            }

            for (int j1 = 0; j1 < 4; ++j1)
            {
                int k1 = par2;
                int l1 = par4;

                if (j1 == 0)
                {
                    l1 = par4 - 1;
                }

                if (j1 == 1)
                {
                    ++l1;
                }

                if (j1 == 2)
                {
                    k1 = par2 - 1;
                }

                if (j1 == 3)
                {
                    ++k1;
                }

                final Icon icon1 = this.getBlockIconFromSideAndMetadata(par1Block, j1 + 2, i1);

                if (this.renderAllFaces || aboolean[j1])
                {
                    final double d15;
                    final double d16;
                    final double d17;
                    final double d18;
                    final double d19;
                    final double d20;

                    if (j1 == 0)
                    {
                        d15 = d2;
                        d17 = d5;
                        d16 = (double)par2;
                        d18 = (double)(par2 + 1);
                        d19 = (double)par4 + d6;
                        d20 = (double)par4 + d6;
                    }
                    else if (j1 == 1)
                    {
                        d15 = d4;
                        d17 = d3;
                        d16 = (double)(par2 + 1);
                        d18 = (double)par2;
                        d19 = (double)(par4 + 1) - d6;
                        d20 = (double)(par4 + 1) - d6;
                    }
                    else if (j1 == 2)
                    {
                        d15 = d3;
                        d17 = d2;
                        d16 = (double)par2 + d6;
                        d18 = (double)par2 + d6;
                        d19 = (double)(par4 + 1);
                        d20 = (double)par4;
                    }
                    else
                    {
                        d15 = d5;
                        d17 = d4;
                        d16 = (double)(par2 + 1) - d6;
                        d18 = (double)(par2 + 1) - d6;
                        d19 = (double)par4;
                        d20 = (double)(par4 + 1);
                    }

                    flag2 = true;
                    final float f12 = icon1.getInterpolatedU(0.0D);
                    f9 = icon1.getInterpolatedU(8.0D);
                    f8 = icon1.getInterpolatedV((1.0D - d15) * 16.0D * 0.5D);
                    f7 = icon1.getInterpolatedV((1.0D - d17) * 16.0D * 0.5D);
                    final float f13 = icon1.getInterpolatedV(8.0D);
                    tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, k1, par3, l1));
                    float f14 = 1.0F;

                    if (j1 < 2)
                    {
                        f14 *= f5;
                    }
                    else
                    {
                        f14 *= f6;
                    }

                    tessellator.setColorOpaque_F(f4 * f14 * f, f4 * f14 * f1, f4 * f14 * f2);
                    tessellator.addVertexWithUV(d16, (double)par3 + d15, d19, (double)f12, (double)f8);
                    tessellator.addVertexWithUV(d18, (double)par3 + d17, d20, (double)f9, (double)f7);
                    tessellator.addVertexWithUV(d18, (double)(par3 + 0), d20, (double)f9, (double)f13);
                    tessellator.addVertexWithUV(d16, (double)(par3 + 0), d19, (double)f12, (double)f13);
                }
            }

            this.renderMinY = d0;
            this.renderMaxY = d1;
            return flag2;
        }
    }

    /**
     * Get fluid height
     */
    public float getFluidHeight(final int par1, final int par2, final int par3, final Material par4Material)
    {
        int l = 0;
        float f = 0.0F;

        for (int i1 = 0; i1 < 4; ++i1)
        {
            final int j1 = par1 - (i1 & 1);
            final int k1 = par3 - (i1 >> 1 & 1);

            if (this.blockAccess.getBlockMaterial(j1, par2 + 1, k1) == par4Material)
            {
                return 1.0F;
            }

            final Material material1 = this.blockAccess.getBlockMaterial(j1, par2, k1);

            if (material1 == par4Material)
            {
                final int l1 = this.blockAccess.getBlockMetadata(j1, par2, k1);

                if (l1 >= 8 || l1 == 0)
                {
                    f += BlockFluid.getFluidHeightPercent(l1) * 10.0F;
                    l += 10;
                }

                f += BlockFluid.getFluidHeightPercent(l1);
                ++l;
            }
            else if (!material1.isSolid())
            {
                ++f;
                ++l;
            }
        }

        return 1.0F - f / (float)l;
    }

    /**
     * Renders a falling sand block
     */
    public void renderBlockSandFalling(final Block par1Block, final World par2World, final int par3, final int par4, final int par5, final int par6)
    {
        final float f = 0.5F;
        final float f1 = 1.0F;
        final float f2 = 0.8F;
        final float f3 = 0.6F;
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(par2World, par3, par4, par5));
        final float f4 = 1.0F;
        float f5 = 1.0F;

        if (f5 < f4)
        {
            f5 = f4;
        }

        tessellator.setColorOpaque_F(f * f5, f * f5, f * f5);
        this.renderFaceYNeg(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 0, par6));
        f5 = 1.0F;

        if (f5 < f4)
        {
            f5 = f4;
        }

        tessellator.setColorOpaque_F(f1 * f5, f1 * f5, f1 * f5);
        this.renderFaceYPos(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 1, par6));
        f5 = 1.0F;

        if (f5 < f4)
        {
            f5 = f4;
        }

        tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
        this.renderFaceZNeg(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 2, par6));
        f5 = 1.0F;

        if (f5 < f4)
        {
            f5 = f4;
        }

        tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
        this.renderFaceZPos(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 3, par6));
        f5 = 1.0F;

        if (f5 < f4)
        {
            f5 = f4;
        }

        tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
        this.renderFaceXNeg(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 4, par6));
        f5 = 1.0F;

        if (f5 < f4)
        {
            f5 = f4;
        }

        tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
        this.renderFaceXPos(par1Block, -0.5D, -0.5D, -0.5D, this.getBlockIconFromSideAndMetadata(par1Block, 5, par6));
        tessellator.draw();
    }

    /**
     * Renders a standard cube block at the given coordinates
     */
    public boolean renderStandardBlock(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final int l = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            final float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            final float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            final float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        return Minecraft.isAmbientOcclusionEnabled() && Block.lightValue[par1Block.blockID] == 0 ? (this.partialRenderBounds ? this.renderStandardBlockWithAmbientOcclusionPartial(par1Block, par2, par3, par4, f, f1, f2) : this.renderStandardBlockWithAmbientOcclusion(par1Block, par2, par3, par4, f, f1, f2)) : this.renderStandardBlockWithColorMultiplier(par1Block, par2, par3, par4, f, f1, f2);
    }

    /**
     * Renders a log block at the given coordinates
     */
    public boolean renderBlockLog(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final int i1 = l & 12;

        if (i1 == 4)
        {
            this.uvRotateEast = 1;
            this.uvRotateWest = 1;
            this.uvRotateTop = 1;
            this.uvRotateBottom = 1;
        }
        else if (i1 == 8)
        {
            this.uvRotateSouth = 1;
            this.uvRotateNorth = 1;
        }

        final boolean flag = this.renderStandardBlock(par1Block, par2, par3, par4);
        this.uvRotateSouth = 0;
        this.uvRotateEast = 0;
        this.uvRotateWest = 0;
        this.uvRotateNorth = 0;
        this.uvRotateTop = 0;
        this.uvRotateBottom = 0;
        return flag;
    }

    public boolean renderBlockQuartz(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);

        if (l == 3)
        {
            this.uvRotateEast = 1;
            this.uvRotateWest = 1;
            this.uvRotateTop = 1;
            this.uvRotateBottom = 1;
        }
        else if (l == 4)
        {
            this.uvRotateSouth = 1;
            this.uvRotateNorth = 1;
        }

        final boolean flag = this.renderStandardBlock(par1Block, par2, par3, par4);
        this.uvRotateSouth = 0;
        this.uvRotateEast = 0;
        this.uvRotateWest = 0;
        this.uvRotateNorth = 0;
        this.uvRotateTop = 0;
        this.uvRotateBottom = 0;
        return flag;
    }

    public boolean renderStandardBlockWithAmbientOcclusion(final Block par1Block, int par2, int par3, int par4, final float par5, final float par6, final float par7)
    {
        int par31 = par3;
        int par41 = par4;
        int par21 = par2;
        this.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        final int l = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (this.getBlockIcon(par1Block).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (this.hasOverrideBlockTexture())
        {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        float f7;
        int i1;

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21, par31 - 1, par41, 0))
        {
            if (this.renderMinY <= 0.0D)
            {
                --par31;
            }

            this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41);
            this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 - 1);
            this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 + 1);
            this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41);
            this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41);
            this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 - 1);
            this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 + 1);
            this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31 - 1, par41)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31 - 1, par41)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 - 1, par41 + 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 - 1, par41 - 1)];

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41 - 1);
                this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41 - 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41 + 1);
                this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41 + 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41 - 1);
                this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41 - 1);
            }

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41 + 1);
                this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41 + 1);
            }

            if (this.renderMinY <= 0.0D)
            {
                ++par31;
            }

            i1 = l;

            if (this.renderMinY <= 0.0D || !this.blockAccess.isBlockOpaqueCube(par21, par31 - 1, par41))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41);
            f3 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + f7 + this.aoLightValueScratchYZNN) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.5F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.5F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.5F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.5F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.5F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.5F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            this.renderFaceYNeg(par1Block, (double) par21, (double) par31, (double) par41, this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 0));
            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21, par31 + 1, par41, 1))
        {
            if (this.renderMaxY >= 1.0D)
            {
                ++par31;
            }

            this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41);
            this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41);
            this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 - 1);
            this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 + 1);
            this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41);
            this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41);
            this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 - 1);
            this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 + 1);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31 + 1, par41)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31 + 1, par41)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 + 1, par41 + 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 + 1, par41 - 1)];

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41 - 1);
                this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41 - 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41 - 1);
                this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41 - 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41 + 1);
                this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41 + 1);
            }

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41 + 1);
                this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41 + 1);
            }

            if (this.renderMaxY >= 1.0D)
            {
                --par31;
            }

            i1 = l;

            if (this.renderMaxY >= 1.0D || !this.blockAccess.isBlockOpaqueCube(par21, par31 + 1, par41))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41);
            f6 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (this.aoLightValueScratchYZPP + f7 + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, i1);
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7;
            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            this.renderFaceYPos(par1Block, (double) par21, (double) par31, (double) par41, this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 1));
            flag = true;
        }

        Icon icon;

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21, par31, par41 - 1, 2))
        {
            if (this.renderMinZ <= 0.0D)
            {
                --par41;
            }

            this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41);
            this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41);
            this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41);
            this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41);
            this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41);
            this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41);
            this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41);
            this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31, par41 - 1)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31, par41 - 1)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 + 1, par41 - 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 - 1, par41 - 1)];

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31 - 1, par41);
                this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31 - 1, par41);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31 + 1, par41);
                this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31 + 1, par41);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31 - 1, par41);
                this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31 - 1, par41);
            }

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31 + 1, par41);
                this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31 + 1, par41);
            }

            if (this.renderMinZ <= 0.0D)
            {
                ++par41;
            }

            i1 = l;

            if (this.renderMinZ <= 0.0D || !this.blockAccess.isBlockOpaqueCube(par21, par31, par41 - 1))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 - 1);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 - 1);
            f3 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
            f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (this.aoLightValueScratchYZNN + f7 + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
            f6 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + f7) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.8F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            icon = this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 2);
            this.renderFaceZNeg(par1Block, (double) par21, (double) par31, (double) par41, icon);

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= par5;
                this.colorRedBottomLeft *= par5;
                this.colorRedBottomRight *= par5;
                this.colorRedTopRight *= par5;
                this.colorGreenTopLeft *= par6;
                this.colorGreenBottomLeft *= par6;
                this.colorGreenBottomRight *= par6;
                this.colorGreenTopRight *= par6;
                this.colorBlueTopLeft *= par7;
                this.colorBlueBottomLeft *= par7;
                this.colorBlueBottomRight *= par7;
                this.colorBlueTopRight *= par7;
                this.renderFaceZNeg(par1Block, (double) par21, (double) par31, (double) par41, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21, par31, par41 + 1, 3))
        {
            if (this.renderMaxZ >= 1.0D)
            {
                ++par41;
            }

            this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41);
            this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41);
            this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41);
            this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41);
            this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41);
            this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41);
            this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41);
            this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31, par41 + 1)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31, par41 + 1)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 + 1, par41 + 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 - 1, par41 + 1)];

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31 - 1, par41);
                this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31 - 1, par41);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31 + 1, par41);
                this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31 + 1, par41);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31 - 1, par41);
                this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31 - 1, par41);
            }

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31 + 1, par41);
                this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31 + 1, par41);
            }

            if (this.renderMaxZ >= 1.0D)
            {
                --par41;
            }

            i1 = l;

            if (this.renderMaxZ >= 1.0D || !this.blockAccess.isBlockOpaqueCube(par21, par31, par41 + 1))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 + 1);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 + 1);
            f3 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + f7 + this.aoLightValueScratchYZPP) / 4.0F;
            f6 = (f7 + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            f5 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
            f4 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + f7) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.8F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            icon = this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 3);
            this.renderFaceZPos(par1Block, (double) par21, (double) par31, (double) par41, this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 3));

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= par5;
                this.colorRedBottomLeft *= par5;
                this.colorRedBottomRight *= par5;
                this.colorRedTopRight *= par5;
                this.colorGreenTopLeft *= par6;
                this.colorGreenBottomLeft *= par6;
                this.colorGreenBottomRight *= par6;
                this.colorGreenTopRight *= par6;
                this.colorBlueTopLeft *= par7;
                this.colorBlueBottomLeft *= par7;
                this.colorBlueBottomRight *= par7;
                this.colorBlueTopRight *= par7;
                this.renderFaceZPos(par1Block, (double) par21, (double) par31, (double) par41, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21 - 1, par31, par41, 4))
        {
            if (this.renderMinX <= 0.0D)
            {
                --par21;
            }

            this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41);
            this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 - 1);
            this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 + 1);
            this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41);
            this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41);
            this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 - 1);
            this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 + 1);
            this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31 + 1, par41)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31 - 1, par41)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31, par41 - 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31, par41 + 1)];

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41 - 1);
                this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41 - 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41 + 1);
                this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41 + 1);
            }

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41 - 1);
                this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41 - 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41 + 1);
                this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41 + 1);
            }

            if (this.renderMinX <= 0.0D)
            {
                ++par21;
            }

            i1 = l;

            if (this.renderMinX <= 0.0D || !this.blockAccess.isBlockOpaqueCube(par21 - 1, par31, par41))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41);
            f6 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + f7 + this.aoLightValueScratchXZNP) / 4.0F;
            f3 = (f7 + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (this.aoLightValueScratchXZNN + f7 + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
            f5 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + f7) / 4.0F;
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, i1);
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.6F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            icon = this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 4);
            this.renderFaceXNeg(par1Block, (double) par21, (double) par31, (double) par41, icon);

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= par5;
                this.colorRedBottomLeft *= par5;
                this.colorRedBottomRight *= par5;
                this.colorRedTopRight *= par5;
                this.colorGreenTopLeft *= par6;
                this.colorGreenBottomLeft *= par6;
                this.colorGreenBottomRight *= par6;
                this.colorGreenTopRight *= par6;
                this.colorBlueTopLeft *= par7;
                this.colorBlueBottomLeft *= par7;
                this.colorBlueBottomRight *= par7;
                this.colorBlueTopRight *= par7;
                this.renderFaceXNeg(par1Block, (double) par21, (double) par31, (double) par41, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21 + 1, par31, par41, 5))
        {
            if (this.renderMaxX >= 1.0D)
            {
                ++par21;
            }

            this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41);
            this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 - 1);
            this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 + 1);
            this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41);
            this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41);
            this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 - 1);
            this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 + 1);
            this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31 + 1, par41)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31 - 1, par41)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31, par41 + 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31, par41 - 1)];

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41 - 1);
                this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41 - 1);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41 + 1);
                this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41 + 1);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41 - 1);
                this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41 - 1);
            }

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41 + 1);
                this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41 + 1);
            }

            if (this.renderMaxX >= 1.0D)
            {
                --par21;
            }

            i1 = l;

            if (this.renderMaxX >= 1.0D || !this.blockAccess.isBlockOpaqueCube(par21 + 1, par31, par41))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41);
            f3 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + f7 + this.aoLightValueScratchXZPP) / 4.0F;
            f4 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (this.aoLightValueScratchXZPN + f7 + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
            f6 = (f7 + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.6F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            icon = this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 5);
            this.renderFaceXPos(par1Block, (double) par21, (double) par31, (double) par41, icon);

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= par5;
                this.colorRedBottomLeft *= par5;
                this.colorRedBottomRight *= par5;
                this.colorRedTopRight *= par5;
                this.colorGreenTopLeft *= par6;
                this.colorGreenBottomLeft *= par6;
                this.colorGreenBottomRight *= par6;
                this.colorGreenTopRight *= par6;
                this.colorBlueTopLeft *= par7;
                this.colorBlueBottomLeft *= par7;
                this.colorBlueBottomRight *= par7;
                this.colorBlueTopRight *= par7;
                this.renderFaceXPos(par1Block, (double) par21, (double) par31, (double) par41, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        this.enableAO = false;
        return flag;
    }

    /**
     * Renders non-full-cube block with ambient occusion.  Args: block, x, y, z, red, green, blue (lighting)
     */
    public boolean renderStandardBlockWithAmbientOcclusionPartial(final Block par1Block, int par2, int par3, int par4, final float par5, final float par6, final float par7)
    {
        int par31 = par3;
        int par41 = par4;
        int par21 = par2;
        this.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        final int l = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (this.getBlockIcon(par1Block).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (this.hasOverrideBlockTexture())
        {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        float f7;
        int i1;

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21, par31 - 1, par41, 0))
        {
            if (this.renderMinY <= 0.0D)
            {
                --par31;
            }

            this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41);
            this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 - 1);
            this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 + 1);
            this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41);
            this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41);
            this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 - 1);
            this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 + 1);
            this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31 - 1, par41)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31 - 1, par41)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 - 1, par41 + 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 - 1, par41 - 1)];

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41 - 1);
                this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41 - 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41 + 1);
                this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41 + 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41 - 1);
                this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41 - 1);
            }

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41 + 1);
                this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41 + 1);
            }

            if (this.renderMinY <= 0.0D)
            {
                ++par31;
            }

            i1 = l;

            if (this.renderMinY <= 0.0D || !this.blockAccess.isBlockOpaqueCube(par21, par31 - 1, par41))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41);
            f3 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + f7 + this.aoLightValueScratchYZNN) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.5F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.5F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.5F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.5F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.5F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.5F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            this.renderFaceYNeg(par1Block, (double) par21, (double) par31, (double) par41, this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 0));
            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21, par31 + 1, par41, 1))
        {
            if (this.renderMaxY >= 1.0D)
            {
                ++par31;
            }

            this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41);
            this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41);
            this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 - 1);
            this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 + 1);
            this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41);
            this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41);
            this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 - 1);
            this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 + 1);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31 + 1, par41)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31 + 1, par41)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 + 1, par41 + 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 + 1, par41 - 1)];

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41 - 1);
                this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41 - 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41 - 1);
                this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41 - 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41 + 1);
                this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41 + 1);
            }

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41 + 1);
                this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41 + 1);
            }

            if (this.renderMaxY >= 1.0D)
            {
                --par31;
            }

            i1 = l;

            if (this.renderMaxY >= 1.0D || !this.blockAccess.isBlockOpaqueCube(par21, par31 + 1, par41))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41);
            f6 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (this.aoLightValueScratchYZPP + f7 + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, i1);
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7;
            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            this.renderFaceYPos(par1Block, (double) par21, (double) par31, (double) par41, this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 1));
            flag = true;
        }

        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;
        Icon icon;

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21, par31, par41 - 1, 2))
        {
            if (this.renderMinZ <= 0.0D)
            {
                --par41;
            }

            this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41);
            this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41);
            this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41);
            this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41);
            this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41);
            this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41);
            this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41);
            this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31, par41 - 1)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31, par41 - 1)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 + 1, par41 - 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 - 1, par41 - 1)];

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31 - 1, par41);
                this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31 - 1, par41);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31 + 1, par41);
                this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31 + 1, par41);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31 - 1, par41);
                this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31 - 1, par41);
            }

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31 + 1, par41);
                this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31 + 1, par41);
            }

            if (this.renderMinZ <= 0.0D)
            {
                ++par41;
            }

            i1 = l;

            if (this.renderMinZ <= 0.0D || !this.blockAccess.isBlockOpaqueCube(par21, par31, par41 - 1))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 - 1);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 - 1);
            f9 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
            f8 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
            f11 = (this.aoLightValueScratchYZNN + f7 + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
            f10 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + f7) / 4.0F;
            f3 = (float)((double)f9 * this.renderMaxY * (1.0D - this.renderMinX) + (double)f8 * this.renderMinY * this.renderMinX + (double)f11 * (1.0D - this.renderMaxY) * this.renderMinX + (double)f10 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinX));
            f4 = (float)((double)f9 * this.renderMaxY * (1.0D - this.renderMaxX) + (double)f8 * this.renderMaxY * this.renderMaxX + (double)f11 * (1.0D - this.renderMaxY) * this.renderMaxX + (double)f10 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX));
            f5 = (float)((double)f9 * this.renderMinY * (1.0D - this.renderMaxX) + (double)f8 * this.renderMinY * this.renderMaxX + (double)f11 * (1.0D - this.renderMinY) * this.renderMaxX + (double)f10 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxX));
            f6 = (float)((double)f9 * this.renderMinY * (1.0D - this.renderMinX) + (double)f8 * this.renderMinY * this.renderMinX + (double)f11 * (1.0D - this.renderMinY) * this.renderMinX + (double)f10 * (1.0D - this.renderMinY) * (1.0D - this.renderMinX));
            k1 = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
            j1 = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, i1);
            i2 = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, i1);
            l1 = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, i1);
            this.brightnessTopLeft = this.mixAoBrightness(k1, j1, i2, l1, this.renderMaxY * (1.0D - this.renderMinX), this.renderMaxY * this.renderMinX, (1.0D - this.renderMaxY) * this.renderMinX, (1.0D - this.renderMaxY) * (1.0D - this.renderMinX));
            this.brightnessBottomLeft = this.mixAoBrightness(k1, j1, i2, l1, this.renderMaxY * (1.0D - this.renderMaxX), this.renderMaxY * this.renderMaxX, (1.0D - this.renderMaxY) * this.renderMaxX, (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX));
            this.brightnessBottomRight = this.mixAoBrightness(k1, j1, i2, l1, this.renderMinY * (1.0D - this.renderMaxX), this.renderMinY * this.renderMaxX, (1.0D - this.renderMinY) * this.renderMaxX, (1.0D - this.renderMinY) * (1.0D - this.renderMaxX));
            this.brightnessTopRight = this.mixAoBrightness(k1, j1, i2, l1, this.renderMinY * (1.0D - this.renderMinX), this.renderMinY * this.renderMinX, (1.0D - this.renderMinY) * this.renderMinX, (1.0D - this.renderMinY) * (1.0D - this.renderMinX));

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.8F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            icon = this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 2);
            this.renderFaceZNeg(par1Block, (double) par21, (double) par31, (double) par41, icon);

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= par5;
                this.colorRedBottomLeft *= par5;
                this.colorRedBottomRight *= par5;
                this.colorRedTopRight *= par5;
                this.colorGreenTopLeft *= par6;
                this.colorGreenBottomLeft *= par6;
                this.colorGreenBottomRight *= par6;
                this.colorGreenTopRight *= par6;
                this.colorBlueTopLeft *= par7;
                this.colorBlueBottomLeft *= par7;
                this.colorBlueBottomRight *= par7;
                this.colorBlueTopRight *= par7;
                this.renderFaceZNeg(par1Block, (double) par21, (double) par31, (double) par41, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21, par31, par41 + 1, 3))
        {
            if (this.renderMaxZ >= 1.0D)
            {
                ++par41;
            }

            this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41);
            this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41);
            this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41);
            this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41);
            this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41);
            this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41);
            this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41);
            this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31, par41 + 1)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31, par41 + 1)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 + 1, par41 + 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21, par31 - 1, par41 + 1)];

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31 - 1, par41);
                this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31 - 1, par41);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31 + 1, par41);
                this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31 + 1, par41);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31 - 1, par41);
                this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31 - 1, par41);
            }

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31 + 1, par41);
                this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31 + 1, par41);
            }

            if (this.renderMaxZ >= 1.0D)
            {
                --par41;
            }

            i1 = l;

            if (this.renderMaxZ >= 1.0D || !this.blockAccess.isBlockOpaqueCube(par21, par31, par41 + 1))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 + 1);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 + 1);
            f9 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + f7 + this.aoLightValueScratchYZPP) / 4.0F;
            f8 = (f7 + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            f11 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
            f10 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + f7) / 4.0F;
            f3 = (float)((double)f9 * this.renderMaxY * (1.0D - this.renderMinX) + (double)f8 * this.renderMaxY * this.renderMinX + (double)f11 * (1.0D - this.renderMaxY) * this.renderMinX + (double)f10 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinX));
            f4 = (float)((double)f9 * this.renderMinY * (1.0D - this.renderMinX) + (double)f8 * this.renderMinY * this.renderMinX + (double)f11 * (1.0D - this.renderMinY) * this.renderMinX + (double)f10 * (1.0D - this.renderMinY) * (1.0D - this.renderMinX));
            f5 = (float)((double)f9 * this.renderMinY * (1.0D - this.renderMaxX) + (double)f8 * this.renderMinY * this.renderMaxX + (double)f11 * (1.0D - this.renderMinY) * this.renderMaxX + (double)f10 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxX));
            f6 = (float)((double)f9 * this.renderMaxY * (1.0D - this.renderMaxX) + (double)f8 * this.renderMaxY * this.renderMaxX + (double)f11 * (1.0D - this.renderMaxY) * this.renderMaxX + (double)f10 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX));
            k1 = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, i1);
            j1 = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, i1);
            i2 = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
            l1 = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, i1);
            this.brightnessTopLeft = this.mixAoBrightness(k1, l1, i2, j1, this.renderMaxY * (1.0D - this.renderMinX), (1.0D - this.renderMaxY) * (1.0D - this.renderMinX), (1.0D - this.renderMaxY) * this.renderMinX, this.renderMaxY * this.renderMinX);
            this.brightnessBottomLeft = this.mixAoBrightness(k1, l1, i2, j1, this.renderMinY * (1.0D - this.renderMinX), (1.0D - this.renderMinY) * (1.0D - this.renderMinX), (1.0D - this.renderMinY) * this.renderMinX, this.renderMinY * this.renderMinX);
            this.brightnessBottomRight = this.mixAoBrightness(k1, l1, i2, j1, this.renderMinY * (1.0D - this.renderMaxX), (1.0D - this.renderMinY) * (1.0D - this.renderMaxX), (1.0D - this.renderMinY) * this.renderMaxX, this.renderMinY * this.renderMaxX);
            this.brightnessTopRight = this.mixAoBrightness(k1, l1, i2, j1, this.renderMaxY * (1.0D - this.renderMaxX), (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX), (1.0D - this.renderMaxY) * this.renderMaxX, this.renderMaxY * this.renderMaxX);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.8F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            icon = this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 3);
            this.renderFaceZPos(par1Block, (double) par21, (double) par31, (double) par41, this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 3));

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= par5;
                this.colorRedBottomLeft *= par5;
                this.colorRedBottomRight *= par5;
                this.colorRedTopRight *= par5;
                this.colorGreenTopLeft *= par6;
                this.colorGreenBottomLeft *= par6;
                this.colorGreenBottomRight *= par6;
                this.colorGreenTopRight *= par6;
                this.colorBlueTopLeft *= par7;
                this.colorBlueBottomLeft *= par7;
                this.colorBlueBottomRight *= par7;
                this.colorBlueTopRight *= par7;
                this.renderFaceZPos(par1Block, (double) par21, (double) par31, (double) par41, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21 - 1, par31, par41, 4))
        {
            if (this.renderMinX <= 0.0D)
            {
                --par21;
            }

            this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41);
            this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 - 1);
            this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 + 1);
            this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41);
            this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41);
            this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 - 1);
            this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 + 1);
            this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31 + 1, par41)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31 - 1, par41)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31, par41 - 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 - 1, par31, par41 + 1)];

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41 - 1);
                this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41 - 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41 + 1);
                this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41 + 1);
            }

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41 - 1);
                this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41 - 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41 + 1);
                this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41 + 1);
            }

            if (this.renderMinX <= 0.0D)
            {
                ++par21;
            }

            i1 = l;

            if (this.renderMinX <= 0.0D || !this.blockAccess.isBlockOpaqueCube(par21 - 1, par31, par41))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 - 1, par31, par41);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 - 1, par31, par41);
            f9 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + f7 + this.aoLightValueScratchXZNP) / 4.0F;
            f8 = (f7 + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
            f11 = (this.aoLightValueScratchXZNN + f7 + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
            f10 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + f7) / 4.0F;
            f3 = (float)((double)f8 * this.renderMaxY * this.renderMaxZ + (double)f11 * this.renderMaxY * (1.0D - this.renderMaxZ) + (double)f10 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ) + (double)f9 * (1.0D - this.renderMaxY) * this.renderMaxZ);
            f4 = (float)((double)f8 * this.renderMaxY * this.renderMinZ + (double)f11 * this.renderMaxY * (1.0D - this.renderMinZ) + (double)f10 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ) + (double)f9 * (1.0D - this.renderMaxY) * this.renderMinZ);
            f5 = (float)((double)f8 * this.renderMinY * this.renderMinZ + (double)f11 * this.renderMinY * (1.0D - this.renderMinZ) + (double)f10 * (1.0D - this.renderMinY) * (1.0D - this.renderMinZ) + (double)f9 * (1.0D - this.renderMinY) * this.renderMinZ);
            f6 = (float)((double)f8 * this.renderMinY * this.renderMaxZ + (double)f11 * this.renderMinY * (1.0D - this.renderMaxZ) + (double)f10 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ) + (double)f9 * (1.0D - this.renderMinY) * this.renderMaxZ);
            k1 = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, i1);
            j1 = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, i1);
            i2 = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, i1);
            l1 = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, i1);
            this.brightnessTopLeft = this.mixAoBrightness(j1, i2, l1, k1, this.renderMaxY * this.renderMaxZ, this.renderMaxY * (1.0D - this.renderMaxZ), (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ), (1.0D - this.renderMaxY) * this.renderMaxZ);
            this.brightnessBottomLeft = this.mixAoBrightness(j1, i2, l1, k1, this.renderMaxY * this.renderMinZ, this.renderMaxY * (1.0D - this.renderMinZ), (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ), (1.0D - this.renderMaxY) * this.renderMinZ);
            this.brightnessBottomRight = this.mixAoBrightness(j1, i2, l1, k1, this.renderMinY * this.renderMinZ, this.renderMinY * (1.0D - this.renderMinZ), (1.0D - this.renderMinY) * (1.0D - this.renderMinZ), (1.0D - this.renderMinY) * this.renderMinZ);
            this.brightnessTopRight = this.mixAoBrightness(j1, i2, l1, k1, this.renderMinY * this.renderMaxZ, this.renderMinY * (1.0D - this.renderMaxZ), (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ), (1.0D - this.renderMinY) * this.renderMaxZ);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.6F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            icon = this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 4);
            this.renderFaceXNeg(par1Block, (double) par21, (double) par31, (double) par41, icon);

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= par5;
                this.colorRedBottomLeft *= par5;
                this.colorRedBottomRight *= par5;
                this.colorRedTopRight *= par5;
                this.colorGreenTopLeft *= par6;
                this.colorGreenBottomLeft *= par6;
                this.colorGreenBottomRight *= par6;
                this.colorGreenTopRight *= par6;
                this.colorBlueTopLeft *= par7;
                this.colorBlueBottomLeft *= par7;
                this.colorBlueBottomRight *= par7;
                this.colorBlueTopRight *= par7;
                this.renderFaceXNeg(par1Block, (double) par21, (double) par31, (double) par41, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par21 + 1, par31, par41, 5))
        {
            if (this.renderMaxX >= 1.0D)
            {
                ++par21;
            }

            this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41);
            this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 - 1);
            this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31, par41 + 1);
            this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41);
            this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41);
            this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 - 1);
            this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31, par41 + 1);
            this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41);
            flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31 + 1, par41)];
            flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31 - 1, par41)];
            flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31, par41 + 1)];
            flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(par21 + 1, par31, par41 - 1)];

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41 - 1);
                this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41 - 1);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 - 1, par41 + 1);
                this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 - 1, par41 + 1);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41 - 1);
                this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41 - 1);
            }

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21, par31 + 1, par41 + 1);
                this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21, par31 + 1, par41 + 1);
            }

            if (this.renderMaxX >= 1.0D)
            {
                --par21;
            }

            i1 = l;

            if (this.renderMaxX >= 1.0D || !this.blockAccess.isBlockOpaqueCube(par21 + 1, par31, par41))
            {
                i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par21 + 1, par31, par41);
            }

            f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, par21 + 1, par31, par41);
            f9 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + f7 + this.aoLightValueScratchXZPP) / 4.0F;
            f8 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + f7) / 4.0F;
            f11 = (this.aoLightValueScratchXZPN + f7 + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
            f10 = (f7 + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            f3 = (float)((double)f9 * (1.0D - this.renderMinY) * this.renderMaxZ + (double)f8 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ) + (double)f11 * this.renderMinY * (1.0D - this.renderMaxZ) + (double)f10 * this.renderMinY * this.renderMaxZ);
            f4 = (float)((double)f9 * (1.0D - this.renderMinY) * this.renderMinZ + (double)f8 * (1.0D - this.renderMinY) * (1.0D - this.renderMinZ) + (double)f11 * this.renderMinY * (1.0D - this.renderMinZ) + (double)f10 * this.renderMinY * this.renderMinZ);
            f5 = (float)((double)f9 * (1.0D - this.renderMaxY) * this.renderMinZ + (double)f8 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ) + (double)f11 * this.renderMaxY * (1.0D - this.renderMinZ) + (double)f10 * this.renderMaxY * this.renderMinZ);
            f6 = (float)((double)f9 * (1.0D - this.renderMaxY) * this.renderMaxZ + (double)f8 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ) + (double)f11 * this.renderMaxY * (1.0D - this.renderMaxZ) + (double)f10 * this.renderMaxY * this.renderMaxZ);
            k1 = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
            j1 = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, i1);
            i2 = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, i1);
            l1 = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, i1);
            this.brightnessTopLeft = this.mixAoBrightness(k1, l1, i2, j1, (1.0D - this.renderMinY) * this.renderMaxZ, (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ), this.renderMinY * (1.0D - this.renderMaxZ), this.renderMinY * this.renderMaxZ);
            this.brightnessBottomLeft = this.mixAoBrightness(k1, l1, i2, j1, (1.0D - this.renderMinY) * this.renderMinZ, (1.0D - this.renderMinY) * (1.0D - this.renderMinZ), this.renderMinY * (1.0D - this.renderMinZ), this.renderMinY * this.renderMinZ);
            this.brightnessBottomRight = this.mixAoBrightness(k1, l1, i2, j1, (1.0D - this.renderMaxY) * this.renderMinZ, (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ), this.renderMaxY * (1.0D - this.renderMinZ), this.renderMaxY * this.renderMinZ);
            this.brightnessTopRight = this.mixAoBrightness(k1, l1, i2, j1, (1.0D - this.renderMaxY) * this.renderMaxZ, (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ), this.renderMaxY * (1.0D - this.renderMaxZ), this.renderMaxY * this.renderMaxZ);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.6F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            icon = this.getBlockIcon(par1Block, this.blockAccess, par21, par31, par41, 5);
            this.renderFaceXPos(par1Block, (double) par21, (double) par31, (double) par41, icon);

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= par5;
                this.colorRedBottomLeft *= par5;
                this.colorRedBottomRight *= par5;
                this.colorRedTopRight *= par5;
                this.colorGreenTopLeft *= par6;
                this.colorGreenBottomLeft *= par6;
                this.colorGreenBottomRight *= par6;
                this.colorGreenTopRight *= par6;
                this.colorBlueTopLeft *= par7;
                this.colorBlueBottomLeft *= par7;
                this.colorBlueBottomRight *= par7;
                this.colorBlueTopRight *= par7;
                this.renderFaceXPos(par1Block, (double) par21, (double) par31, (double) par41, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        this.enableAO = false;
        return flag;
    }

    /**
     * Get ambient occlusion brightness
     */
    public int getAoBrightness(int par1, int par2, int par3, final int par4)
    {
        int par11 = par1;
        int par21 = par2;
        int par31 = par3;
        if (par11 == 0)
        {
            par11 = par4;
        }

        if (par21 == 0)
        {
            par21 = par4;
        }

        if (par31 == 0)
        {
            par31 = par4;
        }

        return par11 + par21 + par31 + par4 >> 2 & 16711935;
    }

    public int mixAoBrightness(final int par1, final int par2, final int par3, final int par4, final double par5, final double par7, final double par9, final double par11)
    {
        final int i1 = (int)((double)(par1 >> 16 & 255) * par5 + (double)(par2 >> 16 & 255) * par7 + (double)(par3 >> 16 & 255) * par9 + (double)(par4 >> 16 & 255) * par11) & 255;
        final int j1 = (int)((double)(par1 & 255) * par5 + (double)(par2 & 255) * par7 + (double)(par3 & 255) * par9 + (double)(par4 & 255) * par11) & 255;
        return i1 << 16 | j1;
    }

    /**
     * Renders a standard cube block at the given coordinates, with a given color ratio.  Args: block, x, y, z, r, g, b
     */
    public boolean renderStandardBlockWithColorMultiplier(final Block par1Block, final int par2, final int par3, final int par4, final float par5, final float par6, final float par7)
    {
        this.enableAO = false;
        final Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        final float f3 = 0.5F;
        final float f4 = 1.0F;
        final float f5 = 0.8F;
        final float f6 = 0.6F;
        final float f7 = f4 * par5;
        final float f8 = f4 * par6;
        final float f9 = f4 * par7;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (par1Block != Block.grass)
        {
            f10 = f3 * par5;
            f11 = f5 * par5;
            f12 = f6 * par5;
            f13 = f3 * par6;
            f14 = f5 * par6;
            f15 = f6 * par6;
            f16 = f3 * par7;
            f17 = f5 * par7;
            f18 = f6 * par7;
        }

        final int l = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 - 1, par4, 0))
        {
            tessellator.setBrightness(this.renderMinY > 0.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4));
            tessellator.setColorOpaque_F(f10, f13, f16);
            this.renderFaceYNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0));
            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 + 1, par4, 1))
        {
            tessellator.setBrightness(this.renderMaxY < 1.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
            tessellator.setColorOpaque_F(f7, f8, f9);
            this.renderFaceYPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1));
            flag = true;
        }

        Icon icon;

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 - 1, 2))
        {
            tessellator.setBrightness(this.renderMinZ > 0.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2);
            this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, icon);

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f11 * par5, f14 * par6, f17 * par7);
                this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 + 1, 3))
        {
            tessellator.setBrightness(this.renderMaxZ < 1.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3);
            this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, icon);

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f11 * par5, f14 * par6, f17 * par7);
                this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 - 1, par3, par4, 4))
        {
            tessellator.setBrightness(this.renderMinX > 0.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4));
            tessellator.setColorOpaque_F(f12, f15, f18);
            icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4);
            this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, icon);

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f12 * par5, f15 * par6, f18 * par7);
                this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 + 1, par3, par4, 5))
        {
            tessellator.setBrightness(this.renderMaxX < 1.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4));
            tessellator.setColorOpaque_F(f12, f15, f18);
            icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5);
            this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, icon);

            if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f12 * par5, f15 * par6, f18 * par7);
                this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        return flag;
    }

    /**
     * Renders a Cocoa block at the given coordinates
     */
    public boolean renderBlockCocoa(final BlockCocoa par1BlockCocoa, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1BlockCocoa.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final int i1 = BlockDirectional.getDirection(l);
        final int j1 = BlockCocoa.func_72219_c(l);
        final Icon icon = par1BlockCocoa.getCocoaIcon(j1);
        final int k1 = 4 + j1 * 2;
        final int l1 = 5 + j1 * 2;
        final double d0 = 15.0D - (double)k1;
        final double d1 = 15.0D;
        final double d2 = 4.0D;
        final double d3 = 4.0D + (double)l1;
        double d4 = (double)icon.getInterpolatedU(d0);
        double d5 = (double)icon.getInterpolatedU(d1);
        double d6 = (double)icon.getInterpolatedV(d2);
        double d7 = (double)icon.getInterpolatedV(d3);
        double d8 = 0.0D;
        double d9 = 0.0D;

        switch (i1)
        {
            case 0:
                d8 = 8.0D - (double)(k1 / 2);
                d9 = 15.0D - (double)k1;
                break;
            case 1:
                d8 = 1.0D;
                d9 = 8.0D - (double)(k1 / 2);
                break;
            case 2:
                d8 = 8.0D - (double)(k1 / 2);
                d9 = 1.0D;
                break;
            case 3:
                d8 = 15.0D - (double)k1;
                d9 = 8.0D - (double)(k1 / 2);
        }

        double d10 = (double)par2 + d8 / 16.0D;
        double d11 = (double)par2 + (d8 + (double)k1) / 16.0D;
        double d12 = (double)par3 + (12.0D - (double)l1) / 16.0D;
        double d13 = (double)par3 + 0.75D;
        double d14 = (double)par4 + d9 / 16.0D;
        double d15 = (double)par4 + (d9 + (double)k1) / 16.0D;
        tessellator.addVertexWithUV(d10, d12, d14, d4, d7);
        tessellator.addVertexWithUV(d10, d12, d15, d5, d7);
        tessellator.addVertexWithUV(d10, d13, d15, d5, d6);
        tessellator.addVertexWithUV(d10, d13, d14, d4, d6);
        tessellator.addVertexWithUV(d11, d12, d15, d4, d7);
        tessellator.addVertexWithUV(d11, d12, d14, d5, d7);
        tessellator.addVertexWithUV(d11, d13, d14, d5, d6);
        tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
        tessellator.addVertexWithUV(d11, d12, d14, d4, d7);
        tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
        tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
        tessellator.addVertexWithUV(d11, d13, d14, d4, d6);
        tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
        tessellator.addVertexWithUV(d11, d12, d15, d5, d7);
        tessellator.addVertexWithUV(d11, d13, d15, d5, d6);
        tessellator.addVertexWithUV(d10, d13, d15, d4, d6);
        int i2 = k1;

        if (j1 >= 2)
        {
            i2 = k1 - 1;
        }

        d4 = (double)icon.getMinU();
        d5 = (double)icon.getInterpolatedU((double)i2);
        d6 = (double)icon.getMinV();
        d7 = (double)icon.getInterpolatedV((double)i2);
        tessellator.addVertexWithUV(d10, d13, d15, d4, d7);
        tessellator.addVertexWithUV(d11, d13, d15, d5, d7);
        tessellator.addVertexWithUV(d11, d13, d14, d5, d6);
        tessellator.addVertexWithUV(d10, d13, d14, d4, d6);
        tessellator.addVertexWithUV(d10, d12, d14, d4, d6);
        tessellator.addVertexWithUV(d11, d12, d14, d5, d6);
        tessellator.addVertexWithUV(d11, d12, d15, d5, d7);
        tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
        d4 = (double)icon.getInterpolatedU(12.0D);
        d5 = (double)icon.getMaxU();
        d6 = (double)icon.getMinV();
        d7 = (double)icon.getInterpolatedV(4.0D);
        d8 = 8.0D;
        d9 = 0.0D;
        final double d16;

        switch (i1)
        {
            case 0:
                d8 = 8.0D;
                d9 = 12.0D;
                d16 = d4;
                d4 = d5;
                d5 = d16;
                break;
            case 1:
                d8 = 0.0D;
                d9 = 8.0D;
                break;
            case 2:
                d8 = 8.0D;
                d9 = 0.0D;
                break;
            case 3:
                d8 = 12.0D;
                d9 = 8.0D;
                d16 = d4;
                d4 = d5;
                d5 = d16;
        }

        d10 = (double)par2 + d8 / 16.0D;
        d11 = (double)par2 + (d8 + 4.0D) / 16.0D;
        d12 = (double)par3 + 0.75D;
        d13 = (double)par3 + 1.0D;
        d14 = (double)par4 + d9 / 16.0D;
        d15 = (double)par4 + (d9 + 4.0D) / 16.0D;

        if (i1 != 2 && i1 != 0)
        {
            if (i1 == 1 || i1 == 3)
            {
                tessellator.addVertexWithUV(d11, d12, d14, d4, d7);
                tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
                tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
                tessellator.addVertexWithUV(d11, d13, d14, d4, d6);
                tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
                tessellator.addVertexWithUV(d11, d12, d14, d4, d7);
                tessellator.addVertexWithUV(d11, d13, d14, d4, d6);
                tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
            }
        }
        else
        {
            tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
            tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
            tessellator.addVertexWithUV(d10, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
            tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
            tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
            tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
            tessellator.addVertexWithUV(d10, d13, d15, d4, d6);
        }

        return true;
    }

    /**
     * Renders beacon block
     */
    public boolean renderBlockBeacon(final BlockBeacon par1BlockBeacon, final int par2, final int par3, final int par4)
    {
        final float f = 0.1875F;
        this.setOverrideBlockTexture(this.getBlockIcon(Block.glass));
        this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        this.renderStandardBlock(par1BlockBeacon, par2, par3, par4);
        this.renderAllFaces = true;
        this.setOverrideBlockTexture(this.getBlockIcon(Block.obsidian));
        this.setRenderBounds(0.125D, 0.0062500000931322575D, 0.125D, 0.875D, (double)f, 0.875D);
        this.renderStandardBlock(par1BlockBeacon, par2, par3, par4);
        this.setOverrideBlockTexture(this.getBlockIcon(Block.beacon));
        this.setRenderBounds(0.1875D, (double)f, 0.1875D, 0.8125D, 0.875D, 0.8125D);
        this.renderStandardBlock(par1BlockBeacon, par2, par3, par4);
        this.renderAllFaces = false;
        this.clearOverrideBlockTexture();
        return true;
    }

    /**
     * Renders a cactus block at the given coordinates
     */
    public boolean renderBlockCactus(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final int l = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            final float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            final float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            final float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        return this.renderBlockCactusImpl(par1Block, par2, par3, par4, f, f1, f2);
    }

    /**
     * Render block cactus implementation
     */
    public boolean renderBlockCactusImpl(final Block par1Block, final int par2, final int par3, final int par4, final float par5, final float par6, final float par7)
    {
        final Tessellator tessellator = Tessellator.instance;
        final boolean flag = false;
        final float f3 = 0.5F;
        final float f4 = 1.0F;
        final float f5 = 0.8F;
        final float f6 = 0.6F;
        final float f7 = f3 * par5;
        final float f8 = f4 * par5;
        final float f9 = f5 * par5;
        final float f10 = f6 * par5;
        final float f11 = f3 * par6;
        final float f12 = f4 * par6;
        final float f13 = f5 * par6;
        final float f14 = f6 * par6;
        final float f15 = f3 * par7;
        final float f16 = f4 * par7;
        final float f17 = f5 * par7;
        final float f18 = f6 * par7;
        final float f19 = 0.0625F;
        final int l = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 - 1, par4, 0))
        {
            tessellator.setBrightness(this.renderMinY > 0.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4));
            tessellator.setColorOpaque_F(f7, f11, f15);
            this.renderFaceYNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0));
        }

        if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3 + 1, par4, 1))
        {
            tessellator.setBrightness(this.renderMaxY < 1.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
            tessellator.setColorOpaque_F(f8, f12, f16);
            this.renderFaceYPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1));
        }

        tessellator.setBrightness(l);
        tessellator.setColorOpaque_F(f9, f13, f17);
        tessellator.addTranslation(0.0F, 0.0F, f19);
        this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2));
        tessellator.addTranslation(0.0F, 0.0F, -f19);
        tessellator.addTranslation(0.0F, 0.0F, -f19);
        this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3));
        tessellator.addTranslation(0.0F, 0.0F, f19);
        tessellator.setColorOpaque_F(f10, f14, f18);
        tessellator.addTranslation(f19, 0.0F, 0.0F);
        this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4));
        tessellator.addTranslation(-f19, 0.0F, 0.0F);
        tessellator.addTranslation(-f19, 0.0F, 0.0F);
        this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5));
        tessellator.addTranslation(f19, 0.0F, 0.0F);
        return true;
    }

    public boolean renderBlockFence(final BlockFence par1BlockFence, final int par2, final int par3, final int par4)
    {
        boolean flag = false;
        float f = 0.375F;
        float f1 = 0.625F;
        this.setRenderBounds((double)f, 0.0D, (double)f, (double)f1, 1.0D, (double)f1);
        this.renderStandardBlock(par1BlockFence, par2, par3, par4);
        flag = true;
        boolean flag1 = false;
        boolean flag2 = false;

        if (par1BlockFence.canConnectFenceTo(this.blockAccess, par2 - 1, par3, par4) || par1BlockFence.canConnectFenceTo(this.blockAccess, par2 + 1, par3, par4))
        {
            flag1 = true;
        }

        if (par1BlockFence.canConnectFenceTo(this.blockAccess, par2, par3, par4 - 1) || par1BlockFence.canConnectFenceTo(this.blockAccess, par2, par3, par4 + 1))
        {
            flag2 = true;
        }

        final boolean flag3 = par1BlockFence.canConnectFenceTo(this.blockAccess, par2 - 1, par3, par4);
        final boolean flag4 = par1BlockFence.canConnectFenceTo(this.blockAccess, par2 + 1, par3, par4);
        final boolean flag5 = par1BlockFence.canConnectFenceTo(this.blockAccess, par2, par3, par4 - 1);
        final boolean flag6 = par1BlockFence.canConnectFenceTo(this.blockAccess, par2, par3, par4 + 1);

        if (!flag1 && !flag2)
        {
            flag1 = true;
        }

        f = 0.4375F;
        f1 = 0.5625F;
        float f2 = 0.75F;
        float f3 = 0.9375F;
        final float f4 = flag3 ? 0.0F : f;
        final float f5 = flag4 ? 1.0F : f1;
        final float f6 = flag5 ? 0.0F : f;
        final float f7 = flag6 ? 1.0F : f1;

        if (flag1)
        {
            this.setRenderBounds((double)f4, (double)f2, (double)f, (double)f5, (double)f3, (double)f1);
            this.renderStandardBlock(par1BlockFence, par2, par3, par4);
            flag = true;
        }

        if (flag2)
        {
            this.setRenderBounds((double)f, (double)f2, (double)f6, (double)f1, (double)f3, (double)f7);
            this.renderStandardBlock(par1BlockFence, par2, par3, par4);
            flag = true;
        }

        f2 = 0.375F;
        f3 = 0.5625F;

        if (flag1)
        {
            this.setRenderBounds((double)f4, (double)f2, (double)f, (double)f5, (double)f3, (double)f1);
            this.renderStandardBlock(par1BlockFence, par2, par3, par4);
            flag = true;
        }

        if (flag2)
        {
            this.setRenderBounds((double)f, (double)f2, (double)f6, (double)f1, (double)f3, (double)f7);
            this.renderStandardBlock(par1BlockFence, par2, par3, par4);
            flag = true;
        }

        par1BlockFence.setBlockBoundsBasedOnState(this.blockAccess, par2, par3, par4);
        return flag;
    }

    /**
     * Renders wall block
     */
    public boolean renderBlockWall(final BlockWall par1BlockWall, final int par2, final int par3, final int par4)
    {
        final boolean flag = par1BlockWall.canConnectWallTo(this.blockAccess, par2 - 1, par3, par4);
        final boolean flag1 = par1BlockWall.canConnectWallTo(this.blockAccess, par2 + 1, par3, par4);
        final boolean flag2 = par1BlockWall.canConnectWallTo(this.blockAccess, par2, par3, par4 - 1);
        final boolean flag3 = par1BlockWall.canConnectWallTo(this.blockAccess, par2, par3, par4 + 1);
        final boolean flag4 = flag2 && flag3 && !flag && !flag1;
        final boolean flag5 = !flag2 && !flag3 && flag && flag1;
        final boolean flag6 = this.blockAccess.isAirBlock(par2, par3 + 1, par4);

        if ((flag4 || flag5) && flag6)
        {
            if (flag4)
            {
                this.setRenderBounds(0.3125D, 0.0D, 0.0D, 0.6875D, 0.8125D, 1.0D);
                this.renderStandardBlock(par1BlockWall, par2, par3, par4);
            }
            else
            {
                this.setRenderBounds(0.0D, 0.0D, 0.3125D, 1.0D, 0.8125D, 0.6875D);
                this.renderStandardBlock(par1BlockWall, par2, par3, par4);
            }
        }
        else
        {
            this.setRenderBounds(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
            this.renderStandardBlock(par1BlockWall, par2, par3, par4);

            if (flag)
            {
                this.setRenderBounds(0.0D, 0.0D, 0.3125D, 0.25D, 0.8125D, 0.6875D);
                this.renderStandardBlock(par1BlockWall, par2, par3, par4);
            }

            if (flag1)
            {
                this.setRenderBounds(0.75D, 0.0D, 0.3125D, 1.0D, 0.8125D, 0.6875D);
                this.renderStandardBlock(par1BlockWall, par2, par3, par4);
            }

            if (flag2)
            {
                this.setRenderBounds(0.3125D, 0.0D, 0.0D, 0.6875D, 0.8125D, 0.25D);
                this.renderStandardBlock(par1BlockWall, par2, par3, par4);
            }

            if (flag3)
            {
                this.setRenderBounds(0.3125D, 0.0D, 0.75D, 0.6875D, 0.8125D, 1.0D);
                this.renderStandardBlock(par1BlockWall, par2, par3, par4);
            }
        }

        par1BlockWall.setBlockBoundsBasedOnState(this.blockAccess, par2, par3, par4);
        return true;
    }

    public boolean renderBlockDragonEgg(final BlockDragonEgg par1BlockDragonEgg, final int par2, final int par3, final int par4)
    {
        boolean flag = false;
        int l = 0;

        for (int i1 = 0; i1 < 8; ++i1)
        {
            byte b0 = 0;
            byte b1 = 1;

            if (i1 == 0)
            {
                b0 = 2;
            }

            if (i1 == 1)
            {
                b0 = 3;
            }

            if (i1 == 2)
            {
                b0 = 4;
            }

            if (i1 == 3)
            {
                b0 = 5;
                b1 = 2;
            }

            if (i1 == 4)
            {
                b0 = 6;
                b1 = 3;
            }

            if (i1 == 5)
            {
                b0 = 7;
                b1 = 5;
            }

            if (i1 == 6)
            {
                b0 = 6;
                b1 = 2;
            }

            if (i1 == 7)
            {
                b0 = 3;
            }

            final float f = (float)b0 / 16.0F;
            final float f1 = 1.0F - (float)l / 16.0F;
            final float f2 = 1.0F - (float)(l + b1) / 16.0F;
            l += b1;
            this.setRenderBounds((double)(0.5F - f), (double)f2, (double)(0.5F - f), (double)(0.5F + f), (double)f1, (double)(0.5F + f));
            this.renderStandardBlock(par1BlockDragonEgg, par2, par3, par4);
        }

        flag = true;
        this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        return flag;
    }

    /**
     * Render block fence gate
     */
    public boolean renderBlockFenceGate(final BlockFenceGate par1BlockFenceGate, final int par2, final int par3, final int par4)
    {
        final boolean flag = true;
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        final boolean flag1 = BlockFenceGate.isFenceGateOpen(l);
        final int i1 = BlockDirectional.getDirection(l);
        float f = 0.375F;
        float f1 = 0.5625F;
        float f2 = 0.75F;
        float f3 = 0.9375F;
        float f4 = 0.3125F;
        float f5 = 1.0F;

        if ((i1 == 2 || i1 == 0) && this.blockAccess.getBlockId(par2 - 1, par3, par4) == Block.cobblestoneWall.blockID && this.blockAccess.getBlockId(par2 + 1, par3, par4) == Block.cobblestoneWall.blockID || (i1 == 3 || i1 == 1) && this.blockAccess.getBlockId(par2, par3, par4 - 1) == Block.cobblestoneWall.blockID && this.blockAccess.getBlockId(par2, par3, par4 + 1) == Block.cobblestoneWall.blockID)
        {
            f -= 0.1875F;
            f1 -= 0.1875F;
            f2 -= 0.1875F;
            f3 -= 0.1875F;
            f4 -= 0.1875F;
            f5 -= 0.1875F;
        }

        this.renderAllFaces = true;
        float f6;
        float f7;
        float f8;
        float f9;

        if (i1 != 3 && i1 != 1)
        {
            f6 = 0.0F;
            f8 = 0.125F;
            f7 = 0.4375F;
            f9 = 0.5625F;
            this.setRenderBounds((double)f6, (double)f4, (double)f7, (double)f8, (double)f5, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f6 = 0.875F;
            f8 = 1.0F;
            this.setRenderBounds((double)f6, (double)f4, (double)f7, (double)f8, (double)f5, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
        }
        else
        {
            this.uvRotateTop = 1;
            f6 = 0.4375F;
            f8 = 0.5625F;
            f7 = 0.0F;
            f9 = 0.125F;
            this.setRenderBounds((double)f6, (double)f4, (double)f7, (double)f8, (double)f5, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f7 = 0.875F;
            f9 = 1.0F;
            this.setRenderBounds((double)f6, (double)f4, (double)f7, (double)f8, (double)f5, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.uvRotateTop = 0;
        }

        if (flag1)
        {
            if (i1 == 2 || i1 == 0)
            {
                this.uvRotateTop = 1;
            }

            final float f10;
            final float f11;
            final float f12;

            if (i1 == 3)
            {
                f6 = 0.0F;
                f8 = 0.125F;
                f7 = 0.875F;
                f9 = 1.0F;
                f10 = 0.5625F;
                f12 = 0.8125F;
                f11 = 0.9375F;
                this.setRenderBounds(0.8125D, (double)f, 0.0D, 0.9375D, (double)f3, 0.125D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.8125D, (double)f, 0.875D, 0.9375D, (double)f3, 1.0D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.5625D, (double)f, 0.0D, 0.8125D, (double)f1, 0.125D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.5625D, (double)f, 0.875D, 0.8125D, (double)f1, 1.0D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.5625D, (double)f2, 0.0D, 0.8125D, (double)f3, 0.125D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.5625D, (double)f2, 0.875D, 0.8125D, (double)f3, 1.0D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            }
            else if (i1 == 1)
            {
                f6 = 0.0F;
                f8 = 0.125F;
                f7 = 0.875F;
                f9 = 1.0F;
                f10 = 0.0625F;
                f12 = 0.1875F;
                f11 = 0.4375F;
                this.setRenderBounds(0.0625D, (double)f, 0.0D, 0.1875D, (double)f3, 0.125D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.0625D, (double)f, 0.875D, 0.1875D, (double)f3, 1.0D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.1875D, (double)f, 0.0D, 0.4375D, (double)f1, 0.125D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.1875D, (double)f, 0.875D, 0.4375D, (double)f1, 1.0D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.1875D, (double)f2, 0.0D, 0.4375D, (double)f3, 0.125D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.1875D, (double)f2, 0.875D, 0.4375D, (double)f3, 1.0D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            }
            else if (i1 == 0)
            {
                f6 = 0.0F;
                f8 = 0.125F;
                f7 = 0.875F;
                f9 = 1.0F;
                f10 = 0.5625F;
                f12 = 0.8125F;
                f11 = 0.9375F;
                this.setRenderBounds(0.0D, (double)f, 0.8125D, 0.125D, (double)f3, 0.9375D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.875D, (double)f, 0.8125D, 1.0D, (double)f3, 0.9375D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.0D, (double)f, 0.5625D, 0.125D, (double)f1, 0.8125D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.875D, (double)f, 0.5625D, 1.0D, (double)f1, 0.8125D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.0D, (double)f2, 0.5625D, 0.125D, (double)f3, 0.8125D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.875D, (double)f2, 0.5625D, 1.0D, (double)f3, 0.8125D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            }
            else if (i1 == 2)
            {
                f6 = 0.0F;
                f8 = 0.125F;
                f7 = 0.875F;
                f9 = 1.0F;
                f10 = 0.0625F;
                f12 = 0.1875F;
                f11 = 0.4375F;
                this.setRenderBounds(0.0D, (double)f, 0.0625D, 0.125D, (double)f3, 0.1875D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.875D, (double)f, 0.0625D, 1.0D, (double)f3, 0.1875D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.0D, (double)f, 0.1875D, 0.125D, (double)f1, 0.4375D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.875D, (double)f, 0.1875D, 1.0D, (double)f1, 0.4375D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.0D, (double)f2, 0.1875D, 0.125D, (double)f3, 0.4375D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                this.setRenderBounds(0.875D, (double)f2, 0.1875D, 1.0D, (double)f3, 0.4375D);
                this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            }
        }
        else if (i1 != 3 && i1 != 1)
        {
            f6 = 0.375F;
            f8 = 0.5F;
            f7 = 0.4375F;
            f9 = 0.5625F;
            this.setRenderBounds((double)f6, (double)f, (double)f7, (double)f8, (double)f3, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f6 = 0.5F;
            f8 = 0.625F;
            this.setRenderBounds((double)f6, (double)f, (double)f7, (double)f8, (double)f3, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f6 = 0.625F;
            f8 = 0.875F;
            this.setRenderBounds((double)f6, (double)f, (double)f7, (double)f8, (double)f1, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds((double)f6, (double)f2, (double)f7, (double)f8, (double)f3, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f6 = 0.125F;
            f8 = 0.375F;
            this.setRenderBounds((double)f6, (double)f, (double)f7, (double)f8, (double)f1, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds((double)f6, (double)f2, (double)f7, (double)f8, (double)f3, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
        }
        else
        {
            this.uvRotateTop = 1;
            f6 = 0.4375F;
            f8 = 0.5625F;
            f7 = 0.375F;
            f9 = 0.5F;
            this.setRenderBounds((double)f6, (double)f, (double)f7, (double)f8, (double)f3, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f7 = 0.5F;
            f9 = 0.625F;
            this.setRenderBounds((double)f6, (double)f, (double)f7, (double)f8, (double)f3, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f7 = 0.625F;
            f9 = 0.875F;
            this.setRenderBounds((double)f6, (double)f, (double)f7, (double)f8, (double)f1, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds((double)f6, (double)f2, (double)f7, (double)f8, (double)f3, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f7 = 0.125F;
            f9 = 0.375F;
            this.setRenderBounds((double)f6, (double)f, (double)f7, (double)f8, (double)f1, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            this.setRenderBounds((double)f6, (double)f2, (double)f7, (double)f8, (double)f3, (double)f9);
            this.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
        }

        this.renderAllFaces = false;
        this.uvRotateTop = 0;
        this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        return flag;
    }

    public boolean renderBlockHopper(final BlockHopper par1BlockHopper, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1BlockHopper.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        final float f = 1.0F;
        final int l = par1BlockHopper.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f1 = (float)(l >> 16 & 255) / 255.0F;
        float f2 = (float)(l >> 8 & 255) / 255.0F;
        float f3 = (float)(l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            final float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            final float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            final float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        return this.renderBlockHopperMetadata(par1BlockHopper, par2, par3, par4, this.blockAccess.getBlockMetadata(par2, par3, par4), false);
    }

    public boolean renderBlockHopperMetadata(final BlockHopper par1BlockHopper, final int par2, final int par3, final int par4, final int par5, final boolean par6)
    {
        final Tessellator tessellator = Tessellator.instance;
        final int i1 = BlockHopper.getDirectionFromMetadata(par5);
        final double d0 = 0.625D;
        this.setRenderBounds(0.0D, d0, 0.0D, 1.0D, 1.0D, 1.0D);

        if (par6)
        {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            this.renderFaceYNeg(par1BlockHopper, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 0, par5));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(par1BlockHopper, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 1, par5));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            this.renderFaceZNeg(par1BlockHopper, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 2, par5));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(par1BlockHopper, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 3, par5));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(par1BlockHopper, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 4, par5));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(par1BlockHopper, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1BlockHopper, 5, par5));
            tessellator.draw();
        }
        else
        {
            this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
        }

        float f;

        if (!par6)
        {
            tessellator.setBrightness(par1BlockHopper.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
            final float f1 = 1.0F;
            final int j1 = par1BlockHopper.colorMultiplier(this.blockAccess, par2, par3, par4);
            f = (float)(j1 >> 16 & 255) / 255.0F;
            float f2 = (float)(j1 >> 8 & 255) / 255.0F;
            float f3 = (float)(j1 & 255) / 255.0F;

            if (EntityRenderer.anaglyphEnable)
            {
                final float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
                final float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                final float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
                f = f4;
                f2 = f5;
                f3 = f6;
            }

            tessellator.setColorOpaque_F(f1 * f, f1 * f2, f1 * f3);
        }

        final Icon icon = BlockHopper.getHopperIcon("hopper_outside");
        final Icon icon1 = BlockHopper.getHopperIcon("hopper_inside");
        f = 0.125F;

        if (par6)
        {
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(par1BlockHopper, (double)(-1.0F + f), 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(par1BlockHopper, (double)(1.0F - f), 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(par1BlockHopper, 0.0D, 0.0D, (double)(-1.0F + f), icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            this.renderFaceZNeg(par1BlockHopper, 0.0D, 0.0D, (double)(1.0F - f), icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(par1BlockHopper, 0.0D, -1.0D + d0, 0.0D, icon1);
            tessellator.draw();
        }
        else
        {
            this.renderFaceXPos(par1BlockHopper, (double)((float)par2 - 1.0F + f), (double)par3, (double)par4, icon);
            this.renderFaceXNeg(par1BlockHopper, (double)((float)par2 + 1.0F - f), (double)par3, (double)par4, icon);
            this.renderFaceZPos(par1BlockHopper, (double)par2, (double)par3, (double)((float)par4 - 1.0F + f), icon);
            this.renderFaceZNeg(par1BlockHopper, (double)par2, (double)par3, (double)((float)par4 + 1.0F - f), icon);
            this.renderFaceYPos(par1BlockHopper, (double)par2, (double)((float)par3 - 1.0F) + d0, (double)par4, icon1);
        }

        this.setOverrideBlockTexture(icon);
        final double d1 = 0.25D;
        final double d2 = 0.25D;
        this.setRenderBounds(d1, d2, d1, 1.0D - d1, d0 - 0.002D, 1.0D - d1);

        if (par6)
        {
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(par1BlockHopper, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(par1BlockHopper, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(par1BlockHopper, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            this.renderFaceZNeg(par1BlockHopper, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(par1BlockHopper, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            this.renderFaceYNeg(par1BlockHopper, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
        }
        else
        {
            this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
        }

        if (!par6)
        {
            final double d3 = 0.375D;
            final double d4 = 0.25D;
            this.setOverrideBlockTexture(icon);

            if (i1 == 0)
            {
                this.setRenderBounds(d3, 0.0D, d3, 1.0D - d3, 0.25D, 1.0D - d3);
                this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
            }

            if (i1 == 2)
            {
                this.setRenderBounds(d3, d2, 0.0D, 1.0D - d3, d2 + d4, d1);
                this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
            }

            if (i1 == 3)
            {
                this.setRenderBounds(d3, d2, 1.0D - d1, 1.0D - d3, d2 + d4, 1.0D);
                this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
            }

            if (i1 == 4)
            {
                this.setRenderBounds(0.0D, d2, d3, d1, d2 + d4, 1.0D - d3);
                this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
            }

            if (i1 == 5)
            {
                this.setRenderBounds(1.0D - d1, d2, d3, 1.0D, d2 + d4, 1.0D - d3);
                this.renderStandardBlock(par1BlockHopper, par2, par3, par4);
            }
        }

        this.clearOverrideBlockTexture();
        return true;
    }

    /**
     * Renders a stair block at the given coordinates
     */
    public boolean renderBlockStairs(final BlockStairs par1BlockStairs, final int par2, final int par3, final int par4)
    {
        par1BlockStairs.func_82541_d(this.blockAccess, par2, par3, par4);
        this.setRenderBoundsFromBlock(par1BlockStairs);
        this.renderStandardBlock(par1BlockStairs, par2, par3, par4);
        final boolean flag = par1BlockStairs.func_82542_g(this.blockAccess, par2, par3, par4);
        this.setRenderBoundsFromBlock(par1BlockStairs);
        this.renderStandardBlock(par1BlockStairs, par2, par3, par4);

        if (flag && par1BlockStairs.func_82544_h(this.blockAccess, par2, par3, par4))
        {
            this.setRenderBoundsFromBlock(par1BlockStairs);
            this.renderStandardBlock(par1BlockStairs, par2, par3, par4);
        }

        return true;
    }

    /**
     * Renders a door block at the given coordinates
     */
    public boolean renderBlockDoor(final Block par1Block, final int par2, final int par3, final int par4)
    {
        final Tessellator tessellator = Tessellator.instance;
        final int l = this.blockAccess.getBlockMetadata(par2, par3, par4);

        if ((l & 8) != 0)
        {
            if (this.blockAccess.getBlockId(par2, par3 - 1, par4) != par1Block.blockID)
            {
                return false;
            }
        }
        else if (this.blockAccess.getBlockId(par2, par3 + 1, par4) != par1Block.blockID)
        {
            return false;
        }

        boolean flag = false;
        final float f = 0.5F;
        final float f1 = 1.0F;
        final float f2 = 0.8F;
        final float f3 = 0.6F;
        final int i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
        tessellator.setBrightness(this.renderMinY > 0.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4));
        tessellator.setColorOpaque_F(f, f, f);
        this.renderFaceYNeg(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0));
        flag = true;
        tessellator.setBrightness(this.renderMaxY < 1.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
        tessellator.setColorOpaque_F(f1, f1, f1);
        this.renderFaceYPos(par1Block, (double)par2, (double)par3, (double)par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1));
        flag = true;
        tessellator.setBrightness(this.renderMinZ > 0.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1));
        tessellator.setColorOpaque_F(f2, f2, f2);
        Icon icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2);
        this.renderFaceZNeg(par1Block, (double)par2, (double)par3, (double)par4, icon);
        flag = true;
        this.flipTexture = false;
        tessellator.setBrightness(this.renderMaxZ < 1.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1));
        tessellator.setColorOpaque_F(f2, f2, f2);
        icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3);
        this.renderFaceZPos(par1Block, (double)par2, (double)par3, (double)par4, icon);
        flag = true;
        this.flipTexture = false;
        tessellator.setBrightness(this.renderMinX > 0.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4));
        tessellator.setColorOpaque_F(f3, f3, f3);
        icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4);
        this.renderFaceXNeg(par1Block, (double)par2, (double)par3, (double)par4, icon);
        flag = true;
        this.flipTexture = false;
        tessellator.setBrightness(this.renderMaxX < 1.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4));
        tessellator.setColorOpaque_F(f3, f3, f3);
        icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5);
        this.renderFaceXPos(par1Block, (double)par2, (double)par3, (double)par4, icon);
        flag = true;
        this.flipTexture = false;
        return flag;
    }

    /**
     * Renders the given texture to the bottom face of the block. Args: block, x, y, z, texture
     */
    public void renderFaceYNeg(final Block par1Block, final double par2, final double par4, final double par6, Icon par8Icon)
    {
        Icon par8Icon1 = par8Icon;
        final Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            par8Icon1 = this.overrideBlockTexture;
        }

        double d3 = (double) par8Icon1.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double) par8Icon1.getInterpolatedV(this.renderMinZ * 16.0D);
        double d6 = (double) par8Icon1.getInterpolatedV(this.renderMaxZ * 16.0D);

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D)
        {
            d3 = (double) par8Icon1.getMinU();
            d4 = (double) par8Icon1.getMaxU();
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D)
        {
            d5 = (double) par8Icon1.getMinV();
            d6 = (double) par8Icon1.getMaxV();
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateBottom == 2)
        {
            d3 = (double) par8Icon1.getInterpolatedU(this.renderMinZ * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxZ * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateBottom == 1)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(this.renderMinX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(this.renderMaxX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateBottom == 3)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        final double d11 = par2 + this.renderMinX;
        final double d12 = par2 + this.renderMaxX;
        final double d13 = par4 + this.renderMinY;
        final double d14 = par6 + this.renderMinZ;
        final double d15 = par6 + this.renderMaxZ;

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
        }
    }

    /**
     * Renders the given texture to the top face of the block. Args: block, x, y, z, texture
     */
    public void renderFaceYPos(final Block par1Block, final double par2, final double par4, final double par6, Icon par8Icon)
    {
        Icon par8Icon1 = par8Icon;
        final Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            par8Icon1 = this.overrideBlockTexture;
        }

        double d3 = (double) par8Icon1.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double) par8Icon1.getInterpolatedV(this.renderMinZ * 16.0D);
        double d6 = (double) par8Icon1.getInterpolatedV(this.renderMaxZ * 16.0D);

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D)
        {
            d3 = (double) par8Icon1.getMinU();
            d4 = (double) par8Icon1.getMaxU();
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D)
        {
            d5 = (double) par8Icon1.getMinV();
            d6 = (double) par8Icon1.getMaxV();
        }

        double d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateTop == 1)
        {
            d3 = (double) par8Icon1.getInterpolatedU(this.renderMinZ * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxZ * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateTop == 2)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(this.renderMinX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(this.renderMaxX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateTop == 3)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        final double d11 = par2 + this.renderMinX;
        final double d12 = par2 + this.renderMaxX;
        final double d13 = par4 + this.renderMaxY;
        final double d14 = par6 + this.renderMinZ;
        final double d15 = par6 + this.renderMaxZ;

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
        }
        else
        {
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d12, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
        }
    }

    /**
     * Renders the given texture to the north (z-negative) face of the block.  Args: block, x, y, z, texture
     */
    public void renderFaceZNeg(final Block par1Block, final double par2, final double par4, final double par6, Icon par8Icon)
    {
        Icon par8Icon1 = par8Icon;
        final Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            par8Icon1 = this.overrideBlockTexture;
        }

        double d3 = (double) par8Icon1.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D)
        {
            d3 = (double) par8Icon1.getMinU();
            d4 = (double) par8Icon1.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
        {
            d5 = (double) par8Icon1.getMinV();
            d6 = (double) par8Icon1.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateEast == 2)
        {
            d3 = (double) par8Icon1.getInterpolatedU(this.renderMinY * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateEast == 1)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(this.renderMaxX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(this.renderMinX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateEast == 3)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        final double d11 = par2 + this.renderMinX;
        final double d12 = par2 + this.renderMaxX;
        final double d13 = par4 + this.renderMinY;
        final double d14 = par4 + this.renderMaxY;
        final double d15 = par6 + this.renderMinZ;

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d14, d15, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d12, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d14, d15, d7, d9);
            tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
            tessellator.addVertexWithUV(d12, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
        }
    }

    /**
     * Renders the given texture to the south (z-positive) face of the block.  Args: block, x, y, z, texture
     */
    public void renderFaceZPos(final Block par1Block, final double par2, final double par4, final double par6, Icon par8Icon)
    {
        Icon par8Icon1 = par8Icon;
        final Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            par8Icon1 = this.overrideBlockTexture;
        }

        double d3 = (double) par8Icon1.getInterpolatedU(this.renderMinX * 16.0D);
        double d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxX * 16.0D);
        double d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D)
        {
            d3 = (double) par8Icon1.getMinU();
            d4 = (double) par8Icon1.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
        {
            d5 = (double) par8Icon1.getMinV();
            d6 = (double) par8Icon1.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateWest == 1)
        {
            d3 = (double) par8Icon1.getInterpolatedU(this.renderMinY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxY * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateWest == 2)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(this.renderMinX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(this.renderMaxX * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateWest == 3)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        final double d11 = par2 + this.renderMinX;
        final double d12 = par2 + this.renderMaxX;
        final double d13 = par4 + this.renderMinY;
        final double d14 = par4 + this.renderMaxY;
        final double d15 = par6 + this.renderMaxZ;

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d14, d15, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d12, d14, d15, d7, d9);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d14, d15, d3, d5);
            tessellator.addVertexWithUV(d11, d13, d15, d8, d10);
            tessellator.addVertexWithUV(d12, d13, d15, d4, d6);
            tessellator.addVertexWithUV(d12, d14, d15, d7, d9);
        }
    }

    /**
     * Renders the given texture to the west (x-negative) face of the block.  Args: block, x, y, z, texture
     */
    public void renderFaceXNeg(final Block par1Block, final double par2, final double par4, final double par6, Icon par8Icon)
    {
        Icon par8Icon1 = par8Icon;
        final Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            par8Icon1 = this.overrideBlockTexture;
        }

        double d3 = (double) par8Icon1.getInterpolatedU(this.renderMinZ * 16.0D);
        double d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxZ * 16.0D);
        double d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D)
        {
            d3 = (double) par8Icon1.getMinU();
            d4 = (double) par8Icon1.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
        {
            d5 = (double) par8Icon1.getMinV();
            d6 = (double) par8Icon1.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateNorth == 1)
        {
            d3 = (double) par8Icon1.getInterpolatedU(this.renderMinY * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateNorth == 2)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(this.renderMinZ * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(this.renderMaxZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateNorth == 3)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        final double d11 = par2 + this.renderMinX;
        final double d12 = par4 + this.renderMinY;
        final double d13 = par4 + this.renderMaxY;
        final double d14 = par6 + this.renderMinZ;
        final double d15 = par6 + this.renderMaxZ;

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d13, d15, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d11, d12, d14, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d12, d15, d4, d6);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d13, d15, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d14, d3, d5);
            tessellator.addVertexWithUV(d11, d12, d14, d8, d10);
            tessellator.addVertexWithUV(d11, d12, d15, d4, d6);
        }
    }

    /**
     * Renders the given texture to the east (x-positive) face of the block.  Args: block, x, y, z, texture
     */
    public void renderFaceXPos(final Block par1Block, final double par2, final double par4, final double par6, Icon par8Icon)
    {
        Icon par8Icon1 = par8Icon;
        final Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture())
        {
            par8Icon1 = this.overrideBlockTexture;
        }

        double d3 = (double) par8Icon1.getInterpolatedU(this.renderMinZ * 16.0D);
        double d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxZ * 16.0D);
        double d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        double d7;

        if (this.flipTexture)
        {
            d7 = d3;
            d3 = d4;
            d4 = d7;
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D)
        {
            d3 = (double) par8Icon1.getMinU();
            d4 = (double) par8Icon1.getMaxU();
        }

        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
        {
            d5 = (double) par8Icon1.getMinV();
            d6 = (double) par8Icon1.getMaxV();
        }

        d7 = d4;
        double d8 = d3;
        double d9 = d5;
        double d10 = d6;

        if (this.uvRotateSouth == 2)
        {
            d3 = (double) par8Icon1.getInterpolatedU(this.renderMinY * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(this.renderMaxY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            d9 = d5;
            d10 = d6;
            d7 = d3;
            d8 = d4;
            d5 = d6;
            d6 = d9;
        }
        else if (this.uvRotateSouth == 1)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(this.renderMaxZ * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(this.renderMinZ * 16.0D);
            d7 = d4;
            d8 = d3;
            d3 = d4;
            d4 = d8;
            d9 = d6;
            d10 = d5;
        }
        else if (this.uvRotateSouth == 3)
        {
            d3 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            d4 = (double) par8Icon1.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            d5 = (double) par8Icon1.getInterpolatedV(this.renderMaxY * 16.0D);
            d6 = (double) par8Icon1.getInterpolatedV(this.renderMinY * 16.0D);
            d7 = d4;
            d8 = d3;
            d9 = d5;
            d10 = d6;
        }

        final double d11 = par2 + this.renderMaxX;
        final double d12 = par4 + this.renderMinY;
        final double d13 = par4 + this.renderMaxY;
        final double d14 = par6 + this.renderMinZ;
        final double d15 = par6 + this.renderMaxZ;

        if (this.enableAO)
        {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(d11, d12, d15, d8, d10);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(d11, d12, d14, d4, d6);
            tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(d11, d13, d14, d7, d9);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(d11, d13, d15, d3, d5);
        }
        else
        {
            tessellator.addVertexWithUV(d11, d12, d15, d8, d10);
            tessellator.addVertexWithUV(d11, d12, d14, d4, d6);
            tessellator.addVertexWithUV(d11, d13, d14, d7, d9);
            tessellator.addVertexWithUV(d11, d13, d15, d3, d5);
        }
    }

    /**
     * Is called to render the image of a block on an inventory, as a held item, or as a an item on the ground
     */
    public void renderBlockAsItem(final Block par1Block, int par2, final float par3)
    {
        int par21 = par2;
        final Tessellator tessellator = Tessellator.instance;
        final boolean flag = par1Block.blockID == Block.grass.blockID;

        if (par1Block == Block.dispenser || par1Block == Block.dropper || par1Block == Block.furnaceIdle)
        {
            par21 = 3;
        }

        int j;
        float f1;
        float f2;
        float f3;

        if (this.useInventoryTint)
        {
            j = par1Block.getRenderColor(par21);

            if (flag)
            {
                j = 16777215;
            }

            f1 = (float)(j >> 16 & 255) / 255.0F;
            f2 = (float)(j >> 8 & 255) / 255.0F;
            f3 = (float)(j & 255) / 255.0F;
            GL11.glColor4f(f1 * par3, f2 * par3, f3 * par3, 1.0F);
        }

        j = par1Block.getRenderType();
        this.setRenderBoundsFromBlock(par1Block);
        int k;

        if (j != 0 && j != 31 && j != 39 && j != 16 && j != 26)
        {
            if (j == 1)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                this.drawCrossedSquares(par1Block, par21, -0.5D, -0.5D, -0.5D, 1.0F);
                tessellator.draw();
            }
            else if (j == 19)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                par1Block.setBlockBoundsForItemRender();
                this.renderBlockStemSmall(par1Block, par21, this.renderMaxY, -0.5D, -0.5D, -0.5D);
                tessellator.draw();
            }
            else if (j == 23)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                par1Block.setBlockBoundsForItemRender();
                tessellator.draw();
            }
            else if (j == 13)
            {
                par1Block.setBlockBoundsForItemRender();
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                f1 = 0.0625F;
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                this.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 0));
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                this.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 1));
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                tessellator.addTranslation(0.0F, 0.0F, f1);
                this.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 2));
                tessellator.addTranslation(0.0F, 0.0F, -f1);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                tessellator.addTranslation(0.0F, 0.0F, -f1);
                this.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 3));
                tessellator.addTranslation(0.0F, 0.0F, f1);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                tessellator.addTranslation(f1, 0.0F, 0.0F);
                this.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 4));
                tessellator.addTranslation(-f1, 0.0F, 0.0F);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                tessellator.addTranslation(-f1, 0.0F, 0.0F);
                this.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 5));
                tessellator.addTranslation(f1, 0.0F, 0.0F);
                tessellator.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            else if (j == 22)
            {
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                ChestItemRenderHelper.instance.renderChest(par1Block, par21, par3);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            }
            else if (j == 6)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                this.renderBlockCropsImpl(par1Block, par21, -0.5D, -0.5D, -0.5D);
                tessellator.draw();
            }
            else if (j == 2)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                this.renderTorchAtAngle(par1Block, -0.5D, -0.5D, -0.5D, 0.0D, 0.0D, 0);
                tessellator.draw();
            }
            else if (j == 10)
            {
                for (k = 0; k < 2; ++k)
                {
                    if (k == 0)
                    {
                        this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
                    }

                    if (k == 1)
                    {
                        this.setRenderBounds(0.0D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 0));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 1));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 3));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 4));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 5));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }
            }
            else if (j == 27)
            {
                k = 0;
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                tessellator.startDrawingQuads();

                for (int l = 0; l < 8; ++l)
                {
                    byte b0 = 0;
                    byte b1 = 1;

                    if (l == 0)
                    {
                        b0 = 2;
                    }

                    if (l == 1)
                    {
                        b0 = 3;
                    }

                    if (l == 2)
                    {
                        b0 = 4;
                    }

                    if (l == 3)
                    {
                        b0 = 5;
                        b1 = 2;
                    }

                    if (l == 4)
                    {
                        b0 = 6;
                        b1 = 3;
                    }

                    if (l == 5)
                    {
                        b0 = 7;
                        b1 = 5;
                    }

                    if (l == 6)
                    {
                        b0 = 6;
                        b1 = 2;
                    }

                    if (l == 7)
                    {
                        b0 = 3;
                    }

                    final float f4 = (float)b0 / 16.0F;
                    final float f5 = 1.0F - (float)k / 16.0F;
                    final float f6 = 1.0F - (float)(k + b1) / 16.0F;
                    k += b1;
                    this.setRenderBounds((double)(0.5F - f4), (double)f6, (double)(0.5F - f4), (double)(0.5F + f4), (double)f5, (double)(0.5F + f4));
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 0));
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 1));
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 2));
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 3));
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 4));
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 5));
                }

                tessellator.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            }
            else if (j == 11)
            {
                for (k = 0; k < 4; ++k)
                {
                    f2 = 0.125F;

                    if (k == 0)
                    {
                        this.setRenderBounds((double)(0.5F - f2), 0.0D, 0.0D, (double)(0.5F + f2), 1.0D, (double)(f2 * 2.0F));
                    }

                    if (k == 1)
                    {
                        this.setRenderBounds((double)(0.5F - f2), 0.0D, (double)(1.0F - f2 * 2.0F), (double)(0.5F + f2), 1.0D, 1.0D);
                    }

                    f2 = 0.0625F;

                    if (k == 2)
                    {
                        this.setRenderBounds((double)(0.5F - f2), (double)(1.0F - f2 * 3.0F), (double)(-f2 * 2.0F), (double)(0.5F + f2), (double)(1.0F - f2), (double)(1.0F + f2 * 2.0F));
                    }

                    if (k == 3)
                    {
                        this.setRenderBounds((double)(0.5F - f2), (double)(0.5F - f2 * 3.0F), (double)(-f2 * 2.0F), (double)(0.5F + f2), (double)(0.5F - f2), (double)(1.0F + f2 * 2.0F));
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 0));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 1));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 3));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 4));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 5));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            }
            else if (j == 21)
            {
                for (k = 0; k < 3; ++k)
                {
                    f2 = 0.0625F;

                    if (k == 0)
                    {
                        this.setRenderBounds((double)(0.5F - f2), 0.30000001192092896D, 0.0D, (double)(0.5F + f2), 1.0D, (double)(f2 * 2.0F));
                    }

                    if (k == 1)
                    {
                        this.setRenderBounds((double)(0.5F - f2), 0.30000001192092896D, (double)(1.0F - f2 * 2.0F), (double)(0.5F + f2), 1.0D, 1.0D);
                    }

                    f2 = 0.0625F;

                    if (k == 2)
                    {
                        this.setRenderBounds((double)(0.5F - f2), 0.5D, 0.0D, (double)(0.5F + f2), (double)(1.0F - f2), 1.0D);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 0));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 1));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 3));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 4));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSide(par1Block, 5));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }
            }
            else if (j == 32)
            {
                for (k = 0; k < 2; ++k)
                {
                    if (k == 0)
                    {
                        this.setRenderBounds(0.0D, 0.0D, 0.3125D, 1.0D, 0.8125D, 0.6875D);
                    }

                    if (k == 1)
                    {
                        this.setRenderBounds(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 0, par21));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 1, par21));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 2, par21));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 3, par21));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 4, par21));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 5, par21));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            }
            else if (j == 35)
            {
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                this.renderBlockAnvilOrient((BlockAnvil)par1Block, 0, 0, 0, par21 << 2, true);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            else if (j == 34)
            {
                for (k = 0; k < 3; ++k)
                {
                    if (k == 0)
                    {
                        this.setRenderBounds(0.125D, 0.0D, 0.125D, 0.875D, 0.1875D, 0.875D);
                        this.setOverrideBlockTexture(this.getBlockIcon(Block.obsidian));
                    }
                    else if (k == 1)
                    {
                        this.setRenderBounds(0.1875D, 0.1875D, 0.1875D, 0.8125D, 0.875D, 0.8125D);
                        this.setOverrideBlockTexture(this.getBlockIcon(Block.beacon));
                    }
                    else if (k == 2)
                    {
                        this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                        this.setOverrideBlockTexture(this.getBlockIcon(Block.glass));
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 0, par21));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 1, par21));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 2, par21));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 3, par21));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 4, par21));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 5, par21));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                this.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                this.clearOverrideBlockTexture();
            }
            else if (j == 38)
            {
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                this.renderBlockHopperMetadata((BlockHopper)par1Block, 0, 0, 0, 0, true);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            else
            {
                FMLRenderAccessLibrary.renderInventoryBlock(this, par1Block, par21, j);
            }
        }
        else
        {
            if (j == 16)
            {
                par21 = 1;
            }

            par1Block.setBlockBoundsForItemRender();
            this.setRenderBoundsFromBlock(par1Block);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            this.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 0, par21));
            tessellator.draw();

            if (flag && this.useInventoryTint)
            {
                k = par1Block.getRenderColor(par21);
                f2 = (float)(k >> 16 & 255) / 255.0F;
                f3 = (float)(k >> 8 & 255) / 255.0F;
                final float f7 = (float)(k & 255) / 255.0F;
                GL11.glColor4f(f2 * par3, f3 * par3, f7 * par3, 1.0F);
            }

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 1, par21));
            tessellator.draw();

            if (flag && this.useInventoryTint)
            {
                GL11.glColor4f(par3, par3, par3, 1.0F);
            }

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            this.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 2, par21));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 3, par21));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 4, par21));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(par1Block, 5, par21));
            tessellator.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
    }

    /**
     * Checks to see if the item's render type indicates that it should be rendered as a regular block or not.
     */
    public static boolean renderItemIn3d(final int par0)
    {
        switch (par0)
        {
        case 0: return true;
        case 31: return true ;
        case 39: return true ;
        case 13: return true ;
        case 10: return true ;
        case 11: return true ;
        case 27: return true ;
        case 22: return true ;
        case 21: return true ;
        case 16: return true ;
        case 26: return true ;
        case 32: return true ;
        case 34: return true ;
        case 35: return true ;
        default: return FMLRenderAccessLibrary.renderItemAsFull3DBlock(par0);
        }

    }

    public Icon getBlockIcon(final Block par1Block, final IBlockAccess par2IBlockAccess, final int par3, final int par4, final int par5, final int par6)
    {
        return this.getIconSafe(par1Block.getBlockTexture(par2IBlockAccess, par3, par4, par5, par6));
    }

    public Icon getBlockIconFromSideAndMetadata(final Block par1Block, final int par2, final int par3)
    {
        return this.getIconSafe(par1Block.getIcon(par2, par3));
    }

    public Icon getBlockIconFromSide(final Block par1Block, final int par2)
    {
        return this.getIconSafe(par1Block.getBlockTextureFromSide(par2));
    }

    public Icon getBlockIcon(final Block par1Block)
    {
        return this.getIconSafe(par1Block.getBlockTextureFromSide(1));
    }

    public Icon getIconSafe(Icon par1Icon)
    {
        Icon par1Icon1 = par1Icon;
        if (par1Icon1 == null)
        {
            par1Icon1 = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
        }

        return (Icon) par1Icon1;
    }
}
