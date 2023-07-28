package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class EntityPickupFX extends EntityFX
{
    private Entity entityToPickUp;
    private Entity entityPickingUp;
    private int age;
    private int maxAge;

    /** renamed from yOffset to fix shadowing Entity.yOffset */
    private float yOffs;

    public EntityPickupFX(final World par1World, final Entity par2Entity, final Entity par3Entity, final float par4)
    {
        super(par1World, par2Entity.posX, par2Entity.posY, par2Entity.posZ, par2Entity.motionX, par2Entity.motionY, par2Entity.motionZ);
        this.entityToPickUp = par2Entity;
        this.entityPickingUp = par3Entity;
        this.maxAge = 3;
        this.yOffs = par4;
    }

    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7)
    {
        float f6 = ((float)this.age + par2) / (float)this.maxAge;
        f6 *= f6;
        final double d0 = this.entityToPickUp.posX;
        final double d1 = this.entityToPickUp.posY;
        final double d2 = this.entityToPickUp.posZ;
        final double d3 = this.entityPickingUp.lastTickPosX + (this.entityPickingUp.posX - this.entityPickingUp.lastTickPosX) * (double)par2;
        final double d4 = this.entityPickingUp.lastTickPosY + (this.entityPickingUp.posY - this.entityPickingUp.lastTickPosY) * (double)par2 + (double)this.yOffs;
        final double d5 = this.entityPickingUp.lastTickPosZ + (this.entityPickingUp.posZ - this.entityPickingUp.lastTickPosZ) * (double)par2;
        double d6 = d0 + (d3 - d0) * (double)f6;
        double d7 = d1 + (d4 - d1) * (double)f6;
        double d8 = d2 + (d5 - d2) * (double)f6;
        final int i = MathHelper.floor_double(d6);
        final int j = MathHelper.floor_double(d7 + (double)(this.yOffset / 2.0F));
        final int k = MathHelper.floor_double(d8);
        final int l = this.getBrightnessForRender(par2);
        final int i1 = l % 65536;
        final int j1 = l / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)i1 / 1.0F, (float)j1 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        d6 -= interpPosX;
        d7 -= interpPosY;
        d8 -= interpPosZ;
        RenderManager.instance.renderEntityWithPosYaw(this.entityToPickUp, (double)((float)d6), (double)((float)d7), (double)((float)d8), this.entityToPickUp.rotationYaw, par2);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        ++this.age;

        if (this.age == this.maxAge)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }
}
