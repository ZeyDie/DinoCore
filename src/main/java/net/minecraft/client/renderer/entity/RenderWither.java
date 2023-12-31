package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelWither;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderWither extends RenderLiving
{
    private static final ResourceLocation invulnerableWitherTextures = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation witherTextures = new ResourceLocation("textures/entity/wither/wither.png");
    private int field_82419_a;

    public RenderWither()
    {
        super(new ModelWither(), 1.0F);
        this.field_82419_a = ((ModelWither)this.mainModel).func_82903_a();
    }

    public void func_82418_a(final EntityWither par1EntityWither, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        BossStatus.setBossStatus(par1EntityWither, true);
        final int i = ((ModelWither)this.mainModel).func_82903_a();

        if (i != this.field_82419_a)
        {
            this.field_82419_a = i;
            this.mainModel = new ModelWither();
        }

        super.doRenderLiving(par1EntityWither, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation func_110911_a(final EntityWither par1EntityWither)
    {
        final int i = par1EntityWither.func_82212_n();
        return i > 0 && (i > 80 || i / 5 % 2 != 1) ? invulnerableWitherTextures : witherTextures;
    }

    protected void func_82415_a(final EntityWither par1EntityWither, final float par2)
    {
        final int i = par1EntityWither.func_82212_n();

        if (i > 0)
        {
            final float f1 = 2.0F - ((float)i - par2) / 220.0F * 0.5F;
            GL11.glScalef(f1, f1, f1);
        }
        else
        {
            GL11.glScalef(2.0F, 2.0F, 2.0F);
        }
    }

    protected int func_82417_a(final EntityWither par1EntityWither, final int par2, final float par3)
    {
        if (par1EntityWither.isArmored())
        {
            if (par1EntityWither.isInvisible())
            {
                GL11.glDepthMask(false);
            }
            else
            {
                GL11.glDepthMask(true);
            }

            if (par2 == 1)
            {
                final float f1 = (float)par1EntityWither.ticksExisted + par3;
                this.bindTexture(invulnerableWitherTextures);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glLoadIdentity();
                final float f2 = MathHelper.cos(f1 * 0.02F) * 3.0F;
                final float f3 = f1 * 0.01F;
                GL11.glTranslatef(f2, f3, 0.0F);
                this.setRenderPassModel(this.mainModel);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glEnable(GL11.GL_BLEND);
                final float f4 = 0.5F;
                GL11.glColor4f(f4, f4, f4, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
                GL11.glTranslatef(0.0F, -0.01F, 0.0F);
                GL11.glScalef(1.1F, 1.1F, 1.1F);
                return 1;
            }

            if (par2 == 2)
            {
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }

        return -1;
    }

    protected int func_82416_b(final EntityWither par1EntityWither, final int par2, final float par3)
    {
        return -1;
    }

    public void doRenderLiving(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.func_82418_a((EntityWither)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        this.func_82415_a((EntityWither)par1EntityLivingBase, par2);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3)
    {
        return this.func_82417_a((EntityWither)par1EntityLivingBase, par2, par3);
    }

    protected int inheritRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3)
    {
        return this.func_82416_b((EntityWither)par1EntityLivingBase, par2, par3);
    }

    public void renderPlayer(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.func_82418_a((EntityWither)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.func_110911_a((EntityWither)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.func_82418_a((EntityWither)par1Entity, par2, par4, par6, par8, par9);
    }
}
