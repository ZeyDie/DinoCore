package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderOcelot extends RenderLiving
{
    private static final ResourceLocation blackOcelotTextures = new ResourceLocation("textures/entity/cat/black.png");
    private static final ResourceLocation ocelotTextures = new ResourceLocation("textures/entity/cat/ocelot.png");
    private static final ResourceLocation redOcelotTextures = new ResourceLocation("textures/entity/cat/red.png");
    private static final ResourceLocation siameseOcelotTextures = new ResourceLocation("textures/entity/cat/siamese.png");

    public RenderOcelot(final ModelBase par1ModelBase, final float par2)
    {
        super(par1ModelBase, par2);
    }

    public void renderLivingOcelot(final EntityOcelot par1EntityOcelot, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        super.doRenderLiving(par1EntityOcelot, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation func_110874_a(final EntityOcelot par1EntityOcelot)
    {
        switch (par1EntityOcelot.getTameSkin())
        {
            case 0:
            default:
                return ocelotTextures;
            case 1:
                return blackOcelotTextures;
            case 2:
                return redOcelotTextures;
            case 3:
                return siameseOcelotTextures;
        }
    }

    /**
     * Pre-Renders the Ocelot.
     */
    protected void preRenderOcelot(final EntityOcelot par1EntityOcelot, final float par2)
    {
        super.preRenderCallback(par1EntityOcelot, par2);

        if (par1EntityOcelot.isTamed())
        {
            GL11.glScalef(0.8F, 0.8F, 0.8F);
        }
    }

    public void doRenderLiving(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderLivingOcelot((EntityOcelot)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        this.preRenderOcelot((EntityOcelot)par1EntityLivingBase, par2);
    }

    public void renderPlayer(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderLivingOcelot((EntityOcelot)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.func_110874_a((EntityOcelot)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderLivingOcelot((EntityOcelot)par1Entity, par2, par4, par6, par8, par9);
    }
}
