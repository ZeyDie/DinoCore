package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSpider extends RenderLiving
{
    private static final ResourceLocation spiderEyesTextures = new ResourceLocation("textures/entity/spider_eyes.png");
    private static final ResourceLocation spiderTextures = new ResourceLocation("textures/entity/spider/spider.png");

    public RenderSpider()
    {
        super(new ModelSpider(), 1.0F);
        this.setRenderPassModel(new ModelSpider());
    }

    protected float setSpiderDeathMaxRotation(final EntitySpider par1EntitySpider)
    {
        return 180.0F;
    }

    /**
     * Sets the spider's glowing eyes
     */
    protected int setSpiderEyeBrightness(final EntitySpider par1EntitySpider, final int par2, final float par3)
    {
        if (par2 != 0)
        {
            return -1;
        }
        else
        {
            this.bindTexture(spiderEyesTextures);
            final float f1 = 1.0F;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

            if (par1EntitySpider.isInvisible())
            {
                GL11.glDepthMask(false);
            }
            else
            {
                GL11.glDepthMask(true);
            }

            final char c0 = 61680;
            final int j = c0 % 65536;
            final int k = c0 / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, f1);
            return 1;
        }
    }

    protected ResourceLocation getSpiderTextures(final EntitySpider par1EntitySpider)
    {
        return spiderTextures;
    }

    protected float getDeathMaxRotation(final EntityLivingBase par1EntityLivingBase)
    {
        return this.setSpiderDeathMaxRotation((EntitySpider)par1EntityLivingBase);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3)
    {
        return this.setSpiderEyeBrightness((EntitySpider)par1EntityLivingBase, par2, par3);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.getSpiderTextures((EntitySpider)par1Entity);
    }
}
