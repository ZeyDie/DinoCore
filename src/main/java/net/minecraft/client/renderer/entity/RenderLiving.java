package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class RenderLiving extends RendererLivingEntity
{
    public RenderLiving(final ModelBase par1ModelBase, final float par2)
    {
        super(par1ModelBase, par2);
    }

    protected boolean func_130007_b(final EntityLiving par1EntityLiving)
    {
        return super.func_110813_b(par1EntityLiving) && (par1EntityLiving.getAlwaysRenderNameTagForRender() || par1EntityLiving.hasCustomNameTag() && par1EntityLiving == this.renderManager.field_96451_i);
    }

    public void doRenderLiving(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        super.doRenderLiving(par1EntityLiving, par2, par4, par6, par8, par9);
        this.func_110827_b(par1EntityLiving, par2, par4, par6, par8, par9);
    }

    private double func_110828_a(final double par1, final double par3, final double par5)
    {
        return par1 + (par3 - par1) * par5;
    }

    protected void func_110827_b(final EntityLiving par1EntityLiving, double par2, double par4, double par6, final float par8, final float par9)
    {
        double par41 = par4;
        double par21 = par2;
        double par61 = par6;
        final Entity entity = par1EntityLiving.getLeashedToEntity();

        if (entity != null)
        {
            par41 -= (1.6D - (double)par1EntityLiving.height) * 0.5D;
            final Tessellator tessellator = Tessellator.instance;
            final double d3 = this.func_110828_a((double)entity.prevRotationYaw, (double)entity.rotationYaw, (double)(par9 * 0.5F)) * 0.01745329238474369D;
            final double d4 = this.func_110828_a((double)entity.prevRotationPitch, (double)entity.rotationPitch, (double)(par9 * 0.5F)) * 0.01745329238474369D;
            double d5 = Math.cos(d3);
            double d6 = Math.sin(d3);
            double d7 = Math.sin(d4);

            if (entity instanceof EntityHanging)
            {
                d5 = 0.0D;
                d6 = 0.0D;
                d7 = -1.0D;
            }

            final double d8 = Math.cos(d4);
            final double d9 = this.func_110828_a(entity.prevPosX, entity.posX, (double)par9) - d5 * 0.7D - d6 * 0.5D * d8;
            final double d10 = this.func_110828_a(entity.prevPosY + (double)entity.getEyeHeight() * 0.7D, entity.posY + (double)entity.getEyeHeight() * 0.7D, (double)par9) - d7 * 0.5D - 0.25D;
            final double d11 = this.func_110828_a(entity.prevPosZ, entity.posZ, (double)par9) - d6 * 0.7D + d5 * 0.5D * d8;
            final double d12 = this.func_110828_a((double)par1EntityLiving.prevRenderYawOffset, (double)par1EntityLiving.renderYawOffset, (double)par9) * 0.01745329238474369D + (Math.PI / 2.0D);
            d5 = Math.cos(d12) * (double)par1EntityLiving.width * 0.4D;
            d6 = Math.sin(d12) * (double)par1EntityLiving.width * 0.4D;
            final double d13 = this.func_110828_a(par1EntityLiving.prevPosX, par1EntityLiving.posX, (double)par9) + d5;
            final double d14 = this.func_110828_a(par1EntityLiving.prevPosY, par1EntityLiving.posY, (double)par9);
            final double d15 = this.func_110828_a(par1EntityLiving.prevPosZ, par1EntityLiving.posZ, (double)par9) + d6;
            par21 += d5;
            par61 += d6;
            final double d16 = (double)((float)(d9 - d13));
            final double d17 = (double)((float)(d10 - d14));
            final double d18 = (double)((float)(d11 - d15));
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            final boolean flag = true;
            final double d19 = 0.025D;
            tessellator.startDrawing(5);
            int i;
            float f2;

            for (i = 0; i <= 24; ++i)
            {
                if (i % 2 == 0)
                {
                    tessellator.setColorRGBA_F(0.5F, 0.4F, 0.3F, 1.0F);
                }
                else
                {
                    tessellator.setColorRGBA_F(0.35F, 0.28F, 0.21000001F, 1.0F);
                }

                f2 = (float)i / 24.0F;
                tessellator.addVertex(par21 + d16 * (double)f2 + 0.0D, par41 + d17 * (double)(f2 * f2 + f2) * 0.5D + (double)((24.0F - (float)i) / 18.0F + 0.125F), par61 + d18 * (double)f2);
                tessellator.addVertex(par21 + d16 * (double)f2 + 0.025D, par41 + d17 * (double)(f2 * f2 + f2) * 0.5D + (double)((24.0F - (float)i) / 18.0F + 0.125F) + 0.025D, par61 + d18 * (double)f2);
            }

            tessellator.draw();
            tessellator.startDrawing(5);

            for (i = 0; i <= 24; ++i)
            {
                if (i % 2 == 0)
                {
                    tessellator.setColorRGBA_F(0.5F, 0.4F, 0.3F, 1.0F);
                }
                else
                {
                    tessellator.setColorRGBA_F(0.35F, 0.28F, 0.21000001F, 1.0F);
                }

                f2 = (float)i / 24.0F;
                tessellator.addVertex(par21 + d16 * (double)f2 + 0.0D, par41 + d17 * (double)(f2 * f2 + f2) * 0.5D + (double)((24.0F - (float)i) / 18.0F + 0.125F) + 0.025D, par61 + d18 * (double)f2);
                tessellator.addVertex(par21 + d16 * (double)f2 + 0.025D, par41 + d17 * (double)(f2 * f2 + f2) * 0.5D + (double)((24.0F - (float)i) / 18.0F + 0.125F), par61 + d18 * (double)f2 + 0.025D);
            }

            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    protected boolean func_110813_b(final EntityLivingBase par1EntityLivingBase)
    {
        return this.func_130007_b((EntityLiving)par1EntityLivingBase);
    }

    public void renderPlayer(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.doRenderLiving((EntityLiving)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.doRenderLiving((EntityLiving)par1Entity, par2, par4, par6, par8, par9);
    }
}
