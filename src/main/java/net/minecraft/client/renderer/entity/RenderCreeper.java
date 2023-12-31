package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderCreeper extends RenderLiving
{
    private static final ResourceLocation armoredCreeperTextures = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private static final ResourceLocation creeperTextures = new ResourceLocation("textures/entity/creeper/creeper.png");

    /** The creeper model. */
    private ModelBase creeperModel = new ModelCreeper(2.0F);

    public RenderCreeper()
    {
        super(new ModelCreeper(), 0.5F);
    }

    /**
     * Updates creeper scale in prerender callback
     */
    protected void updateCreeperScale(final EntityCreeper par1EntityCreeper, final float par2)
    {
        float f1 = par1EntityCreeper.getCreeperFlashIntensity(par2);
        final float f2 = 1.0F + MathHelper.sin(f1 * 100.0F) * f1 * 0.01F;

        if (f1 < 0.0F)
        {
            f1 = 0.0F;
        }

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        f1 *= f1;
        f1 *= f1;
        final float f3 = (1.0F + f1 * 0.4F) * f2;
        final float f4 = (1.0F + f1 * 0.1F) / f2;
        GL11.glScalef(f3, f4, f3);
    }

    /**
     * Updates color multiplier based on creeper state called by getColorMultiplier
     */
    protected int updateCreeperColorMultiplier(final EntityCreeper par1EntityCreeper, final float par2, final float par3)
    {
        final float f2 = par1EntityCreeper.getCreeperFlashIntensity(par3);

        if ((int)(f2 * 10.0F) % 2 == 0)
        {
            return 0;
        }
        else
        {
            int i = (int)(f2 * 0.2F * 255.0F);

            if (i < 0)
            {
                i = 0;
            }

            if (i > 255)
            {
                i = 255;
            }

            final short short1 = 255;
            final short short2 = 255;
            final short short3 = 255;
            return i << 24 | short1 << 16 | short2 << 8 | short3;
        }
    }

    /**
     * A method used to render a creeper's powered form as a pass model.
     */
    protected int renderCreeperPassModel(final EntityCreeper par1EntityCreeper, final int par2, final float par3)
    {
        if (par1EntityCreeper.getPowered())
        {
            if (par1EntityCreeper.isInvisible())
            {
                GL11.glDepthMask(false);
            }
            else
            {
                GL11.glDepthMask(true);
            }

            if (par2 == 1)
            {
                final float f1 = (float)par1EntityCreeper.ticksExisted + par3;
                this.bindTexture(armoredCreeperTextures);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glLoadIdentity();
                final float f2 = f1 * 0.01F;
                final float f3 = f1 * 0.01F;
                GL11.glTranslatef(f2, f3, 0.0F);
                this.setRenderPassModel(this.creeperModel);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glEnable(GL11.GL_BLEND);
                final float f4 = 0.5F;
                GL11.glColor4f(f4, f4, f4, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
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

    protected int func_77061_b(final EntityCreeper par1EntityCreeper, final int par2, final float par3)
    {
        return -1;
    }

    protected ResourceLocation getCreeperTextures(final EntityCreeper par1EntityCreeper)
    {
        return creeperTextures;
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        this.updateCreeperScale((EntityCreeper)par1EntityLivingBase, par2);
    }

    /**
     * Returns an ARGB int color back. Args: entityLiving, lightBrightness, partialTickTime
     */
    protected int getColorMultiplier(final EntityLivingBase par1EntityLivingBase, final float par2, final float par3)
    {
        return this.updateCreeperColorMultiplier((EntityCreeper)par1EntityLivingBase, par2, par3);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3)
    {
        return this.renderCreeperPassModel((EntityCreeper)par1EntityLivingBase, par2, par3);
    }

    protected int inheritRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3)
    {
        return this.func_77061_b((EntityCreeper)par1EntityLivingBase, par2, par3);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.getCreeperTextures((EntityCreeper)par1Entity);
    }
}
