package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSilverfish extends RenderLiving
{
    private static final ResourceLocation silverfishTextures = new ResourceLocation("textures/entity/silverfish.png");

    public RenderSilverfish()
    {
        super(new ModelSilverfish(), 0.3F);
    }

    /**
     * Return the silverfish's maximum death rotation.
     */
    protected float getSilverfishDeathRotation(final EntitySilverfish par1EntitySilverfish)
    {
        return 180.0F;
    }

    /**
     * Renders the silverfish.
     */
    public void renderSilverfish(final EntitySilverfish par1EntitySilverfish, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        super.doRenderLiving(par1EntitySilverfish, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getSilverfishTextures(final EntitySilverfish par1EntitySilverfish)
    {
        return silverfishTextures;
    }

    /**
     * Disallows the silverfish to render the renderPassModel.
     */
    protected int shouldSilverfishRenderPass(final EntitySilverfish par1EntitySilverfish, final int par2, final float par3)
    {
        return -1;
    }

    public void doRenderLiving(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderSilverfish((EntitySilverfish)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    protected float getDeathMaxRotation(final EntityLivingBase par1EntityLivingBase)
    {
        return this.getSilverfishDeathRotation((EntitySilverfish)par1EntityLivingBase);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3)
    {
        return this.shouldSilverfishRenderPass((EntitySilverfish)par1EntityLivingBase, par2, par3);
    }

    public void renderPlayer(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderSilverfish((EntitySilverfish)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.getSilverfishTextures((EntitySilverfish)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderSilverfish((EntitySilverfish)par1Entity, par2, par4, par6, par8, par9);
    }
}
