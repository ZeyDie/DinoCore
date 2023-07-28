package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class Render
{
    private static final ResourceLocation shadowTextures = new ResourceLocation("textures/misc/shadow.png");
    protected RenderManager renderManager;
    protected RenderBlocks renderBlocks = new RenderBlocks();
    protected float shadowSize;

    /**
     * Determines the darkness of the object's shadow. Higher value makes a darker shadow.
     */
    protected float shadowOpaque = 1.0F;

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public abstract void doRender(Entity entity, double d0, double d1, double d2, float f, float f1);

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected abstract ResourceLocation getEntityTexture(Entity entity);

    protected void bindEntityTexture(final Entity par1Entity)
    {
        this.bindTexture(this.getEntityTexture(par1Entity));
    }

    protected void bindTexture(final ResourceLocation par1ResourceLocation)
    {
        this.renderManager.renderEngine.bindTexture(par1ResourceLocation);
    }

    /**
     * Renders fire on top of the entity. Args: entity, x, y, z, partialTickTime
     */
    private void renderEntityOnFire(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        final Icon icon = Block.fire.getFireIcon(0);
        final Icon icon1 = Block.fire.getFireIcon(1);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        final float f1 = par1Entity.width * 1.4F;
        GL11.glScalef(f1, f1, f1);
        final Tessellator tessellator = Tessellator.instance;
        float f2 = 0.5F;
        final float f3 = 0.0F;
        float f4 = par1Entity.height / f1;
        float f5 = (float)(par1Entity.posY - par1Entity.boundingBox.minY);
        GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.0F, -0.3F + (float)((int)f4) * 0.02F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f6 = 0.0F;
        int i = 0;
        tessellator.startDrawingQuads();

        while (f4 > 0.0F)
        {
            final Icon icon2 = i % 2 == 0 ? icon : icon1;
            this.bindTexture(TextureMap.locationBlocksTexture);
            float f7 = icon2.getMinU();
            final float f8 = icon2.getMinV();
            float f9 = icon2.getMaxU();
            final float f10 = icon2.getMaxV();

            if (i / 2 % 2 == 0)
            {
                final float f11 = f9;
                f9 = f7;
                f7 = f11;
            }

            tessellator.addVertexWithUV((double)(f2 - f3), (double)(0.0F - f5), (double)f6, (double)f9, (double)f10);
            tessellator.addVertexWithUV((double)(-f2 - f3), (double)(0.0F - f5), (double)f6, (double)f7, (double)f10);
            tessellator.addVertexWithUV((double)(-f2 - f3), (double)(1.4F - f5), (double)f6, (double)f7, (double)f8);
            tessellator.addVertexWithUV((double)(f2 - f3), (double)(1.4F - f5), (double)f6, (double)f9, (double)f8);
            f4 -= 0.45F;
            f5 -= 0.45F;
            f2 *= 0.9F;
            f6 += 0.03F;
            ++i;
        }

        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * Renders the entity shadows at the position, shadow alpha and partialTickTime. Args: entity, x, y, z, shadowAlpha,
     * partialTickTime
     */
    private void renderShadow(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.renderManager.renderEngine.bindTexture(shadowTextures);
        final World world = this.getWorldFromRenderManager();
        GL11.glDepthMask(false);
        float f2 = this.shadowSize;

        if (par1Entity instanceof EntityLiving)
        {
            final EntityLiving entityliving = (EntityLiving)par1Entity;
            f2 *= entityliving.getRenderSizeModifier();

            if (entityliving.isChild())
            {
                f2 *= 0.5F;
            }
        }

        final double d3 = par1Entity.lastTickPosX + (par1Entity.posX - par1Entity.lastTickPosX) * (double)par9;
        final double d4 = par1Entity.lastTickPosY + (par1Entity.posY - par1Entity.lastTickPosY) * (double)par9 + (double)par1Entity.getShadowSize();
        final double d5 = par1Entity.lastTickPosZ + (par1Entity.posZ - par1Entity.lastTickPosZ) * (double)par9;
        final int i = MathHelper.floor_double(d3 - (double)f2);
        final int j = MathHelper.floor_double(d3 + (double)f2);
        final int k = MathHelper.floor_double(d4 - (double)f2);
        final int l = MathHelper.floor_double(d4);
        final int i1 = MathHelper.floor_double(d5 - (double)f2);
        final int j1 = MathHelper.floor_double(d5 + (double)f2);
        final double d6 = par2 - d3;
        final double d7 = par4 - d4;
        final double d8 = par6 - d5;
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        for (int k1 = i; k1 <= j; ++k1)
        {
            for (int l1 = k; l1 <= l; ++l1)
            {
                for (int i2 = i1; i2 <= j1; ++i2)
                {
                    final int j2 = world.getBlockId(k1, l1 - 1, i2);

                    if (j2 > 0 && world.getBlockLightValue(k1, l1, i2) > 3)
                    {
                        this.renderShadowOnBlock(Block.blocksList[j2], par2, par4 + (double)par1Entity.getShadowSize(), par6, k1, l1, i2, par8, f2, d6, d7 + (double)par1Entity.getShadowSize(), d8);
                    }
                }
            }
        }

        tessellator.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }

    /**
     * Returns the render manager's world object
     */
    private World getWorldFromRenderManager()
    {
        return this.renderManager.worldObj;
    }

    /**
     * Renders a shadow projected down onto the specified block. Brightness of the block plus how far away on the Y axis
     * determines the alpha of the shadow.  Args: block, centerX, centerY, centerZ, blockX, blockY, blockZ, baseAlpha,
     * shadowSize, xOffset, yOffset, zOffset
     */
    private void renderShadowOnBlock(final Block par1Block, final double par2, final double par4, final double par6, final int par8, final int par9, final int par10, final float par11, final float par12, final double par13, final double par15, final double par17)
    {
        final Tessellator tessellator = Tessellator.instance;

        if (par1Block.renderAsNormalBlock())
        {
            double d6 = ((double)par11 - (par4 - ((double)par9 + par15)) / 2.0D) * 0.5D * (double)this.getWorldFromRenderManager().getLightBrightness(par8, par9, par10);

            if (d6 >= 0.0D)
            {
                if (d6 > 1.0D)
                {
                    d6 = 1.0D;
                }

                tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, (float)d6);
                final double d7 = (double)par8 + par1Block.getBlockBoundsMinX() + par13;
                final double d8 = (double)par8 + par1Block.getBlockBoundsMaxX() + par13;
                final double d9 = (double)par9 + par1Block.getBlockBoundsMinY() + par15 + 0.015625D;
                final double d10 = (double)par10 + par1Block.getBlockBoundsMinZ() + par17;
                final double d11 = (double)par10 + par1Block.getBlockBoundsMaxZ() + par17;
                final float f2 = (float)((par2 - d7) / 2.0D / (double)par12 + 0.5D);
                final float f3 = (float)((par2 - d8) / 2.0D / (double)par12 + 0.5D);
                final float f4 = (float)((par6 - d10) / 2.0D / (double)par12 + 0.5D);
                final float f5 = (float)((par6 - d11) / 2.0D / (double)par12 + 0.5D);
                tessellator.addVertexWithUV(d7, d9, d10, (double)f2, (double)f4);
                tessellator.addVertexWithUV(d7, d9, d11, (double)f2, (double)f5);
                tessellator.addVertexWithUV(d8, d9, d11, (double)f3, (double)f5);
                tessellator.addVertexWithUV(d8, d9, d10, (double)f3, (double)f4);
            }
        }
    }

    /**
     * Renders a white box with the bounds of the AABB translated by the offset. Args: aabb, x, y, z
     */
    public static void renderOffsetAABB(final AxisAlignedBB par0AxisAlignedBB, final double par1, final double par3, final double par5)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        final Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.startDrawingQuads();
        tessellator.setTranslation(par1, par3, par5);
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Adds to the tesselator a box using the aabb for the bounds. Args: aabb
     */
    public static void renderAABB(final AxisAlignedBB par0AxisAlignedBB)
    {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.draw();
    }

    /**
     * Sets the RenderManager.
     */
    public void setRenderManager(final RenderManager par1RenderManager)
    {
        this.renderManager = par1RenderManager;
    }

    /**
     * Renders the entity's shadow and fire (if its on fire). Args: entity, x, y, z, yaw, partialTickTime
     */
    public void doRenderShadowAndFire(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        if (this.renderManager.options.fancyGraphics && this.shadowSize > 0.0F && !par1Entity.isInvisible())
        {
            final double d3 = this.renderManager.getDistanceToCamera(par1Entity.posX, par1Entity.posY, par1Entity.posZ);
            final float f2 = (float)((1.0D - d3 / 256.0D) * (double)this.shadowOpaque);

            if (f2 > 0.0F)
            {
                this.renderShadow(par1Entity, par2, par4, par6, f2, par9);
            }
        }

        if (par1Entity.canRenderOnFire())
        {
            this.renderEntityOnFire(par1Entity, par2, par4, par6, par9);
        }
    }

    /**
     * Returns the font renderer from the set render manager
     */
    public FontRenderer getFontRendererFromRenderManager()
    {
        return this.renderManager.getFontRenderer();
    }

    public void updateIcons(final IconRegister par1IconRegister) {}
}
