package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.EnumArt;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderPainting extends Render
{
    private static final ResourceLocation field_110807_a = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

    public void renderThePainting(final EntityPainting par1EntityPainting, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(par8, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        this.bindEntityTexture(par1EntityPainting);
        final EnumArt enumart = par1EntityPainting.art;
        final float f2 = 0.0625F;
        GL11.glScalef(f2, f2, f2);
        this.func_77010_a(par1EntityPainting, enumart.sizeX, enumart.sizeY, enumart.offsetX, enumart.offsetY);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    protected ResourceLocation func_110806_a(final EntityPainting par1EntityPainting)
    {
        return field_110807_a;
    }

    private void func_77010_a(final EntityPainting par1EntityPainting, final int par2, final int par3, final int par4, final int par5)
    {
        final float f = (float)(-par2) / 2.0F;
        final float f1 = (float)(-par3) / 2.0F;
        final float f2 = 0.5F;
        final float f3 = 0.75F;
        final float f4 = 0.8125F;
        final float f5 = 0.0F;
        final float f6 = 0.0625F;
        final float f7 = 0.75F;
        final float f8 = 0.8125F;
        final float f9 = 0.001953125F;
        final float f10 = 0.001953125F;
        final float f11 = 0.7519531F;
        final float f12 = 0.7519531F;
        final float f13 = 0.0F;
        final float f14 = 0.0625F;

        for (int i1 = 0; i1 < par2 / 16; ++i1)
        {
            for (int j1 = 0; j1 < par3 / 16; ++j1)
            {
                final float f15 = f + (float)((i1 + 1) * 16);
                final float f16 = f + (float)(i1 * 16);
                final float f17 = f1 + (float)((j1 + 1) * 16);
                final float f18 = f1 + (float)(j1 * 16);
                this.func_77008_a(par1EntityPainting, (f15 + f16) / 2.0F, (f17 + f18) / 2.0F);
                final float f19 = (float)(par4 + par2 - i1 * 16) / 256.0F;
                final float f20 = (float)(par4 + par2 - (i1 + 1) * 16) / 256.0F;
                final float f21 = (float)(par5 + par3 - j1 * 16) / 256.0F;
                final float f22 = (float)(par5 + par3 - (j1 + 1) * 16) / 256.0F;
                final Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)(-f2), (double)f20, (double)f21);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)(-f2), (double)f19, (double)f21);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)(-f2), (double)f19, (double)f22);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)(-f2), (double)f20, (double)f22);
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)f2, (double)f3, (double)f5);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)f2, (double)f4, (double)f5);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)f2, (double)f4, (double)f6);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)f2, (double)f3, (double)f6);
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)(-f2), (double)f7, (double)f9);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)(-f2), (double)f8, (double)f9);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)f2, (double)f8, (double)f10);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)f2, (double)f7, (double)f10);
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)f2, (double)f7, (double)f9);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)f2, (double)f8, (double)f9);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)(-f2), (double)f8, (double)f10);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)(-f2), (double)f7, (double)f10);
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)f2, (double)f12, (double)f13);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)f2, (double)f12, (double)f14);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)(-f2), (double)f11, (double)f14);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)(-f2), (double)f11, (double)f13);
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)(-f2), (double)f12, (double)f13);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)(-f2), (double)f12, (double)f14);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)f2, (double)f11, (double)f14);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)f2, (double)f11, (double)f13);
                tessellator.draw();
            }
        }
    }

    private void func_77008_a(final EntityPainting par1EntityPainting, final float par2, final float par3)
    {
        int i = MathHelper.floor_double(par1EntityPainting.posX);
        final int j = MathHelper.floor_double(par1EntityPainting.posY + (double)(par3 / 16.0F));
        int k = MathHelper.floor_double(par1EntityPainting.posZ);

        if (par1EntityPainting.hangingDirection == 2)
        {
            i = MathHelper.floor_double(par1EntityPainting.posX + (double)(par2 / 16.0F));
        }

        if (par1EntityPainting.hangingDirection == 1)
        {
            k = MathHelper.floor_double(par1EntityPainting.posZ - (double)(par2 / 16.0F));
        }

        if (par1EntityPainting.hangingDirection == 0)
        {
            i = MathHelper.floor_double(par1EntityPainting.posX - (double)(par2 / 16.0F));
        }

        if (par1EntityPainting.hangingDirection == 3)
        {
            k = MathHelper.floor_double(par1EntityPainting.posZ + (double)(par2 / 16.0F));
        }

        final int l = this.renderManager.worldObj.getLightBrightnessForSkyBlocks(i, j, k, 0);
        final int i1 = l % 65536;
        final int j1 = l / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)i1, (float)j1);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.func_110806_a((EntityPainting)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderThePainting((EntityPainting)par1Entity, par2, par4, par6, par8, par9);
    }
}
