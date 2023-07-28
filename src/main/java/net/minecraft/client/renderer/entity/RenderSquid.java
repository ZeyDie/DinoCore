package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSquid extends RenderLiving
{
    private static final ResourceLocation squidTextures = new ResourceLocation("textures/entity/squid.png");

    public RenderSquid(final ModelBase par1ModelBase, final float par2)
    {
        super(par1ModelBase, par2);
    }

    /**
     * Renders the Living Squid.
     */
    public void renderLivingSquid(final EntitySquid par1EntitySquid, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        super.doRenderLiving(par1EntitySquid, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getSquidTextures(final EntitySquid par1EntitySquid)
    {
        return squidTextures;
    }

    /**
     * Rotates the Squid's corpse.
     */
    protected void rotateSquidsCorpse(final EntitySquid par1EntitySquid, final float par2, final float par3, final float par4)
    {
        final float f3 = par1EntitySquid.prevSquidPitch + (par1EntitySquid.squidPitch - par1EntitySquid.prevSquidPitch) * par4;
        final float f4 = par1EntitySquid.prevSquidYaw + (par1EntitySquid.squidYaw - par1EntitySquid.prevSquidYaw) * par4;
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        GL11.glRotatef(180.0F - par3, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(f3, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(f4, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -1.2F, 0.0F);
    }

    protected float handleRotationFloat(final EntitySquid par1EntitySquid, final float par2)
    {
        return par1EntitySquid.prevTentacleAngle + (par1EntitySquid.tentacleAngle - par1EntitySquid.prevTentacleAngle) * par2;
    }

    public void doRenderLiving(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderLivingSquid((EntitySquid)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        return this.handleRotationFloat((EntitySquid)par1EntityLivingBase, par2);
    }

    protected void rotateCorpse(final EntityLivingBase par1EntityLivingBase, final float par2, final float par3, final float par4)
    {
        this.rotateSquidsCorpse((EntitySquid)par1EntityLivingBase, par2, par3, par4);
    }

    public void renderPlayer(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderLivingSquid((EntitySquid)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.getSquidTextures((EntitySquid)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderLivingSquid((EntitySquid)par1Entity, par2, par4, par6, par8, par9);
    }
}
