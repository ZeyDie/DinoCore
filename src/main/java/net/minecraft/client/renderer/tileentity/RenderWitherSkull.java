package net.minecraft.client.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderWitherSkull extends Render
{
    private static final ResourceLocation invulnerableWitherTextures = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation witherTextures = new ResourceLocation("textures/entity/wither/wither.png");

    /** The Skeleton's head model. */
    private final ModelSkeletonHead skeletonHeadModel = new ModelSkeletonHead();

    private float func_82400_a(final float par1, final float par2, final float par3)
    {
        float f3;

        for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return par1 + par3 * f3;
    }

    public void func_82399_a(final EntityWitherSkull par1EntityWitherSkull, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        final float f2 = this.func_82400_a(par1EntityWitherSkull.prevRotationYaw, par1EntityWitherSkull.rotationYaw, par9);
        final float f3 = par1EntityWitherSkull.prevRotationPitch + (par1EntityWitherSkull.rotationPitch - par1EntityWitherSkull.prevRotationPitch) * par9;
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        final float f4 = 0.0625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        this.bindEntityTexture(par1EntityWitherSkull);
        this.skeletonHeadModel.render(par1EntityWitherSkull, 0.0F, 0.0F, 0.0F, f2, f3, f4);
        GL11.glPopMatrix();
    }

    protected ResourceLocation func_110809_a(final EntityWitherSkull par1EntityWitherSkull)
    {
        return par1EntityWitherSkull.isInvulnerable() ? invulnerableWitherTextures : witherTextures;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.func_110809_a((EntityWitherSkull)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.func_82399_a((EntityWitherSkull)par1Entity, par2, par4, par6, par8, par9);
    }
}
