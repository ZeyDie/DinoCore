package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderIronGolem extends RenderLiving
{
    private static final ResourceLocation ironGolemTextures = new ResourceLocation("textures/entity/iron_golem.png");

    /** Iron Golem's Model. */
    private final ModelIronGolem ironGolemModel;

    public RenderIronGolem()
    {
        super(new ModelIronGolem(), 0.5F);
        this.ironGolemModel = (ModelIronGolem)this.mainModel;
    }

    /**
     * Renders the Iron Golem.
     */
    public void doRenderIronGolem(final EntityIronGolem par1EntityIronGolem, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        super.doRenderLiving(par1EntityIronGolem, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getIronGolemTextures(final EntityIronGolem par1EntityIronGolem)
    {
        return ironGolemTextures;
    }

    /**
     * Rotates Iron Golem corpse.
     */
    protected void rotateIronGolemCorpse(final EntityIronGolem par1EntityIronGolem, final float par2, final float par3, final float par4)
    {
        super.rotateCorpse(par1EntityIronGolem, par2, par3, par4);

        if ((double)par1EntityIronGolem.limbSwingAmount >= 0.01D)
        {
            final float f3 = 13.0F;
            final float f4 = par1EntityIronGolem.limbSwing - par1EntityIronGolem.limbSwingAmount * (1.0F - par4) + 6.0F;
            final float f5 = (Math.abs(f4 % f3 - f3 * 0.5F) - f3 * 0.25F) / (f3 * 0.25F);
            GL11.glRotatef(6.5F * f5, 0.0F, 0.0F, 1.0F);
        }
    }

    /**
     * Renders Iron Golem Equipped items.
     */
    protected void renderIronGolemEquippedItems(final EntityIronGolem par1EntityIronGolem, final float par2)
    {
        super.renderEquippedItems(par1EntityIronGolem, par2);

        if (par1EntityIronGolem.getHoldRoseTick() != 0)
        {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glPushMatrix();
            GL11.glRotatef(5.0F + 180.0F * this.ironGolemModel.ironGolemRightArm.rotateAngleX / (float)Math.PI, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(-0.6875F, 1.25F, -0.9375F);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            final float f1 = 0.8F;
            GL11.glScalef(f1, -f1, f1);
            final int i = par1EntityIronGolem.getBrightnessForRender(par2);
            final int j = i % 65536;
            final int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindTexture(TextureMap.locationBlocksTexture);
            this.renderBlocks.renderBlockAsItem(Block.plantRed, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
    }

    public void doRenderLiving(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.doRenderIronGolem((EntityIronGolem)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    protected void renderEquippedItems(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        this.renderIronGolemEquippedItems((EntityIronGolem)par1EntityLivingBase, par2);
    }

    protected void rotateCorpse(final EntityLivingBase par1EntityLivingBase, final float par2, final float par3, final float par4)
    {
        this.rotateIronGolemCorpse((EntityIronGolem)par1EntityLivingBase, par2, par3, par4);
    }

    public void renderPlayer(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.doRenderIronGolem((EntityIronGolem)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.getIronGolemTextures((EntityIronGolem)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.doRenderIronGolem((EntityIronGolem)par1Entity, par2, par4, par6, par8, par9);
    }
}
