package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderBat extends RenderLiving
{
    private static final ResourceLocation batTextures = new ResourceLocation("textures/entity/bat.png");

    /**
     * not actually sure this is size, is not used as of now, but the model would be recreated if the value changed and
     * it seems a good match for a bats size
     */
    private int renderedBatSize;

    public RenderBat()
    {
        super(new ModelBat(), 0.25F);
        this.renderedBatSize = ((ModelBat)this.mainModel).getBatSize();
    }

    public void func_82443_a(final EntityBat par1EntityBat, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        final int i = ((ModelBat)this.mainModel).getBatSize();

        if (i != this.renderedBatSize)
        {
            this.renderedBatSize = i;
            this.mainModel = new ModelBat();
        }

        super.doRenderLiving(par1EntityBat, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getBatTextures(final EntityBat par1EntityBat)
    {
        return batTextures;
    }

    protected void func_82442_a(final EntityBat par1EntityBat, final float par2)
    {
        GL11.glScalef(0.35F, 0.35F, 0.35F);
    }

    protected void func_82445_a(final EntityBat par1EntityBat, final double par2, final double par4, final double par6)
    {
        super.renderLivingAt(par1EntityBat, par2, par4, par6);
    }

    protected void func_82444_a(final EntityBat par1EntityBat, final float par2, final float par3, final float par4)
    {
        if (!par1EntityBat.getIsBatHanging())
        {
            GL11.glTranslatef(0.0F, MathHelper.cos(par2 * 0.3F) * 0.1F, 0.0F);
        }
        else
        {
            GL11.glTranslatef(0.0F, -0.1F, 0.0F);
        }

        super.rotateCorpse(par1EntityBat, par2, par3, par4);
    }

    public void doRenderLiving(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.func_82443_a((EntityBat)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        this.func_82442_a((EntityBat)par1EntityLivingBase, par2);
    }

    protected void rotateCorpse(final EntityLivingBase par1EntityLivingBase, final float par2, final float par3, final float par4)
    {
        this.func_82444_a((EntityBat)par1EntityLivingBase, par2, par3, par4);
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6)
    {
        this.func_82445_a((EntityBat)par1EntityLivingBase, par2, par4, par6);
    }

    public void doRenderLiving(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.func_82443_a((EntityBat)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.getBatTextures((EntityBat)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.func_82443_a((EntityBat)par1Entity, par2, par4, par6, par8, par9);
    }
}
