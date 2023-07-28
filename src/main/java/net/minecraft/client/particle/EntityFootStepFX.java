package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class EntityFootStepFX extends EntityFX
{
    private static final ResourceLocation field_110126_a = new ResourceLocation("textures/particle/footprint.png");
    private int footstepAge;
    private int footstepMaxAge;
    private TextureManager currentFootSteps;

    public EntityFootStepFX(final TextureManager par1TextureManager, final World par2World, final double par3, final double par5, final double par7)
    {
        super(par2World, par3, par5, par7, 0.0D, 0.0D, 0.0D);
        this.currentFootSteps = par1TextureManager;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.footstepMaxAge = 200;
    }

    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7)
    {
        float f6 = ((float)this.footstepAge + par2) / (float)this.footstepMaxAge;
        f6 *= f6;
        float f7 = 2.0F - f6 * 2.0F;

        if (f7 > 1.0F)
        {
            f7 = 1.0F;
        }

        f7 *= 0.2F;
        GL11.glDisable(GL11.GL_LIGHTING);
        final float f8 = 0.125F;
        final float f9 = (float)(this.posX - interpPosX);
        final float f10 = (float)(this.posY - interpPosY);
        final float f11 = (float)(this.posZ - interpPosZ);
        final float f12 = this.worldObj.getLightBrightness(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
        this.currentFootSteps.bindTexture(field_110126_a);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        par1Tessellator.startDrawingQuads();
        par1Tessellator.setColorRGBA_F(f12, f12, f12, f7);
        par1Tessellator.addVertexWithUV((double)(f9 - f8), (double)f10, (double)(f11 + f8), 0.0D, 1.0D);
        par1Tessellator.addVertexWithUV((double)(f9 + f8), (double)f10, (double)(f11 + f8), 1.0D, 1.0D);
        par1Tessellator.addVertexWithUV((double)(f9 + f8), (double)f10, (double)(f11 - f8), 1.0D, 0.0D);
        par1Tessellator.addVertexWithUV((double)(f9 - f8), (double)f10, (double)(f11 - f8), 0.0D, 0.0D);
        par1Tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        ++this.footstepAge;

        if (this.footstepAge == this.footstepMaxAge)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }
}
