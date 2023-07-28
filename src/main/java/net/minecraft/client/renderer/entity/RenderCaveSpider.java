package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderCaveSpider extends RenderSpider
{
    private static final ResourceLocation caveSpiderTextures = new ResourceLocation("textures/entity/spider/cave_spider.png");

    public RenderCaveSpider()
    {
        this.shadowSize *= 0.7F;
    }

    protected void scaleSpider(final EntityCaveSpider par1EntityCaveSpider, final float par2)
    {
        GL11.glScalef(0.7F, 0.7F, 0.7F);
    }

    protected ResourceLocation getCaveSpiderTextures(final EntityCaveSpider par1EntityCaveSpider)
    {
        return caveSpiderTextures;
    }

    protected ResourceLocation getSpiderTextures(final EntitySpider par1EntitySpider)
    {
        return this.getCaveSpiderTextures((EntityCaveSpider)par1EntitySpider);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        this.scaleSpider((EntityCaveSpider)par1EntityLivingBase, par2);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.getCaveSpiderTextures((EntityCaveSpider)par1Entity);
    }
}
