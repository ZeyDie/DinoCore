package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityDiggingFX extends EntityFX
{
    private Block blockInstance;
    private int side;

    public EntityDiggingFX(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12, final Block par14Block, final int par15)
    {
        this(par1World, par2, par4, par6, par8, par10, par12, par14Block, par15, par1World.rand.nextInt(6));
    }

    public EntityDiggingFX(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12, final Block par14Block, final int par15, final int side)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.blockInstance = par14Block;
        this.setParticleIcon(par14Block.getIcon(side, par15));
        this.particleGravity = par14Block.blockParticleGravity;
        this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
        this.particleScale /= 2.0F;
        this.side = side;
    }

    /**
     * If the block has a colour multiplier, copies it to this particle and returns this particle.
     */
    public EntityDiggingFX applyColourMultiplier(final int par1, final int par2, final int par3)
    {
        if (this.blockInstance == Block.grass && this.side != 1)
        {
            return this;
        }
        else
        {
            final int l = this.blockInstance.colorMultiplier(this.worldObj, par1, par2, par3);
            this.particleRed *= (float)(l >> 16 & 255) / 255.0F;
            this.particleGreen *= (float)(l >> 8 & 255) / 255.0F;
            this.particleBlue *= (float)(l & 255) / 255.0F;
            return this;
        }
    }

    /**
     * Creates a new EntityDiggingFX with the block render color applied to the base particle color
     */
    public EntityDiggingFX applyRenderColor(final int par1)
    {
        if (this.blockInstance == Block.grass)
        {
            return this;
        }
        else
        {
            final int j = this.blockInstance.getRenderColor(par1);
            this.particleRed *= (float)(j >> 16 & 255) / 255.0F;
            this.particleGreen *= (float)(j >> 8 & 255) / 255.0F;
            this.particleBlue *= (float)(j & 255) / 255.0F;
            return this;
        }
    }

    public int getFXLayer()
    {
        return 1;
    }

    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7)
    {
        float f6 = ((float)this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
        float f7 = f6 + 0.015609375F;
        float f8 = ((float)this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
        float f9 = f8 + 0.015609375F;
        final float f10 = 0.1F * this.particleScale;

        if (this.particleIcon != null)
        {
            f6 = this.particleIcon.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
            f7 = this.particleIcon.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
            f8 = this.particleIcon.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
            f9 = this.particleIcon.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
        }

        final float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
        final float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
        final float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
        final float f14 = 1.0F;
        par1Tessellator.setColorOpaque_F(f14 * this.particleRed, f14 * this.particleGreen, f14 * this.particleBlue);
        par1Tessellator.addVertexWithUV((double)(f11 - par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 - par5 * f10 - par7 * f10), (double)f6, (double)f9);
        par1Tessellator.addVertexWithUV((double)(f11 - par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 - par5 * f10 + par7 * f10), (double)f6, (double)f8);
        par1Tessellator.addVertexWithUV((double)(f11 + par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 + par5 * f10 + par7 * f10), (double)f7, (double)f8);
        par1Tessellator.addVertexWithUV((double)(f11 + par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 + par5 * f10 - par7 * f10), (double)f7, (double)f9);
    }
}
