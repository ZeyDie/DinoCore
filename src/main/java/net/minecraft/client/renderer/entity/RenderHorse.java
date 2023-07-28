package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderHorse extends RenderLiving
{
    private static final Map field_110852_a = Maps.newHashMap();
    private static final ResourceLocation whiteHorseTextures = new ResourceLocation("textures/entity/horse/horse_white.png");
    private static final ResourceLocation muleTextures = new ResourceLocation("textures/entity/horse/mule.png");
    private static final ResourceLocation donkeyTextures = new ResourceLocation("textures/entity/horse/donkey.png");
    private static final ResourceLocation zombieHorseTextures = new ResourceLocation("textures/entity/horse/horse_zombie.png");
    private static final ResourceLocation skeletonHorseTextures = new ResourceLocation("textures/entity/horse/horse_skeleton.png");

    public RenderHorse(final ModelBase par1ModelBase, final float par2)
    {
        super(par1ModelBase, par2);
    }

    protected void func_110847_a(final EntityHorse par1EntityHorse, final float par2)
    {
        float f1 = 1.0F;
        final int i = par1EntityHorse.getHorseType();

        if (i == 1)
        {
            f1 *= 0.87F;
        }
        else if (i == 2)
        {
            f1 *= 0.92F;
        }

        GL11.glScalef(f1, f1, f1);
        super.preRenderCallback(par1EntityHorse, par2);
    }

    protected void func_110846_a(final EntityHorse par1EntityHorse, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7)
    {
        if (par1EntityHorse.isInvisible())
        {
            this.mainModel.setRotationAngles(par2, par3, par4, par5, par6, par7, par1EntityHorse);
        }
        else
        {
            this.bindEntityTexture(par1EntityHorse);
            this.mainModel.render(par1EntityHorse, par2, par3, par4, par5, par6, par7);
        }
    }

    protected ResourceLocation func_110849_a(final EntityHorse par1EntityHorse)
    {
        if (!par1EntityHorse.func_110239_cn())
        {
            switch (par1EntityHorse.getHorseType())
            {
                case 0:
                default:
                    return whiteHorseTextures;
                case 1:
                    return donkeyTextures;
                case 2:
                    return muleTextures;
                case 3:
                    return zombieHorseTextures;
                case 4:
                    return skeletonHorseTextures;
            }
        }
        else
        {
            return this.func_110848_b(par1EntityHorse);
        }
    }

    private ResourceLocation func_110848_b(final EntityHorse par1EntityHorse)
    {
        final String s = par1EntityHorse.getHorseTexture();
        ResourceLocation resourcelocation = (ResourceLocation)field_110852_a.get(s);

        if (resourcelocation == null)
        {
            resourcelocation = new ResourceLocation(s);
            Minecraft.getMinecraft().getTextureManager().loadTexture(resourcelocation, new LayeredTexture(par1EntityHorse.getVariantTexturePaths()));
            field_110852_a.put(s, resourcelocation);
        }

        return resourcelocation;
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        this.func_110847_a((EntityHorse)par1EntityLivingBase, par2);
    }

    /**
     * Renders the model in RenderLiving
     */
    protected void renderModel(final EntityLivingBase par1EntityLivingBase, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7)
    {
        this.func_110846_a((EntityHorse)par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final Entity par1Entity)
    {
        return this.func_110849_a((EntityHorse)par1Entity);
    }
}
