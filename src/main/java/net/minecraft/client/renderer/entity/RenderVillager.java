package net.minecraft.client.renderer.entity;

import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderVillager extends RenderLiving
{
    private static final ResourceLocation villagerTextures = new ResourceLocation("textures/entity/villager/villager.png");
    private static final ResourceLocation farmerVillagerTextures = new ResourceLocation("textures/entity/villager/farmer.png");
    private static final ResourceLocation librarianVillagerTextures = new ResourceLocation("textures/entity/villager/librarian.png");
    private static final ResourceLocation priestVillagerTextures = new ResourceLocation("textures/entity/villager/priest.png");
    private static final ResourceLocation smithVillagerTextures = new ResourceLocation("textures/entity/villager/smith.png");
    private static final ResourceLocation butcherVillagerTextures = new ResourceLocation("textures/entity/villager/butcher.png");

    /** Model of the villager. */
    protected ModelVillager villagerModel;

    public RenderVillager()
    {
        super(new ModelVillager(0.0F), 0.5F);
        this.villagerModel = (ModelVillager)this.mainModel;
    }

    /**
     * Determines wether Villager Render pass or not.
     */
    protected int shouldVillagerRenderPass(final EntityVillager par1EntityVillager, final int par2, final float par3)
    {
        return -1;
    }

    public void renderVillager(final EntityVillager par1EntityVillager, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        super.doRenderLiving(par1EntityVillager, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation func_110902_a(final EntityVillager par1EntityVillager)
    {
        switch (par1EntityVillager.getProfession())
        {
            case 0:
                return farmerVillagerTextures;
            case 1:
                return librarianVillagerTextures;
            case 2:
                return priestVillagerTextures;
            case 3:
                return smithVillagerTextures;
            case 4:
                return butcherVillagerTextures;
            default:
                return VillagerRegistry.getVillagerSkin(par1EntityVillager.getProfession(), villagerTextures);
        }
    }

    protected void renderVillagerEquipedItems(final EntityVillager par1EntityVillager, final float par2)
    {
        super.renderEquippedItems(par1EntityVillager, par2);
    }

    protected void preRenderVillager(final EntityVillager par1EntityVillager, final float par2)
    {
        float f1 = 0.9375F;

        if (par1EntityVillager.getGrowingAge() < 0)
        {
            f1 = (float)((double)f1 * 0.5D);
            this.shadowSize = 0.25F;
        }
        else
        {
            this.shadowSize = 0.5F;
        }

        GL11.glScalef(f1, f1, f1);
    }

    public void doRenderLiving(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderVillager((EntityVillager)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        this.preRenderVillager((EntityVillager)par1EntityLivingBase, par2);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3)
    {
        return this.shouldVillagerRenderPass((EntityVillager)par1EntityLivingBase, par2, par3);
    }

    protected void renderEquippedItems(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        this.renderVillagerEquipedItems((EntityVillager)par1EntityLivingBase, par2);
    }

    public void renderPlayer(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderVillager((EntityVillager)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.func_110902_a((EntityVillager)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.renderVillager((EntityVillager)par1Entity, par2, par4, par6, par8, par9);
    }
}
