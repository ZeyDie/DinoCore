package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderMooshroom extends RenderLiving
{
    private static final ResourceLocation mooshroomTextures = new ResourceLocation("textures/entity/cow/mooshroom.png");

    public RenderMooshroom(final ModelBase par1ModelBase, final float par2)
    {
        super(par1ModelBase, par2);
    }

    public void renderLivingMooshroom(final EntityMooshroom par1EntityMooshroom, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        super.doRenderLiving(par1EntityMooshroom, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getMooshroomTextures(final EntityMooshroom par1EntityMooshroom)
    {
        return mooshroomTextures;
    }

    protected void renderMooshroomEquippedItems(final EntityMooshroom par1EntityMooshroom, final float par2)
    {
        super.renderEquippedItems(par1EntityMooshroom, par2);

        if (!par1EntityMooshroom.isChild())
        {
            this.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPushMatrix();
            GL11.glScalef(1.0F, -1.0F, 1.0F);
            GL11.glTranslatef(0.2F, 0.4F, 0.5F);
            GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
            this.renderBlocks.renderBlockAsItem(Block.mushroomRed, 0, 1.0F);
            GL11.glTranslatef(0.1F, 0.0F, -0.6F);
            GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
            this.renderBlocks.renderBlockAsItem(Block.mushroomRed, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            ((ModelQuadruped)this.mainModel).head.postRender(0.0625F);
            GL11.glScalef(1.0F, -1.0F, 1.0F);
            GL11.glTranslatef(0.0F, 0.75F, -0.2F);
            GL11.glRotatef(12.0F, 0.0F, 1.0F, 0.0F);
            this.renderBlocks.renderBlockAsItem(Block.mushroomRed, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
    }

    public void doRenderLiving(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderLivingMooshroom((EntityMooshroom)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    protected void renderEquippedItems(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        this.renderMooshroomEquippedItems((EntityMooshroom)par1EntityLivingBase, par2);
    }

    public void renderPlayer(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderLivingMooshroom((EntityMooshroom)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.getMooshroomTextures((EntityMooshroom)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderLivingMooshroom((EntityMooshroom)par1Entity, par2, par4, par6, par8, par9);
    }
}
