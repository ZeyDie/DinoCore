package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelSquid extends ModelBase
{
    /** The squid's body */
    ModelRenderer squidBody;

    /** The squid's tentacles */
    ModelRenderer[] squidTentacles = new ModelRenderer[8];

    public ModelSquid()
    {
        final byte b0 = -16;
        this.squidBody = new ModelRenderer(this, 0, 0);
        this.squidBody.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
        this.squidBody.rotationPointY += (float)(24 + b0);

        for (int i = 0; i < this.squidTentacles.length; ++i)
        {
            this.squidTentacles[i] = new ModelRenderer(this, 48, 0);
            double d0 = (double)i * Math.PI * 2.0D / (double)this.squidTentacles.length;
            final float f = (float)Math.cos(d0) * 5.0F;
            final float f1 = (float)Math.sin(d0) * 5.0F;
            this.squidTentacles[i].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
            this.squidTentacles[i].rotationPointX = f;
            this.squidTentacles[i].rotationPointZ = f1;
            this.squidTentacles[i].rotationPointY = (float)(31 + b0);
            d0 = (double)i * Math.PI * -2.0D / (double)this.squidTentacles.length + (Math.PI / 2.0D);
            this.squidTentacles[i].rotateAngleY = (float)d0;
        }
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity par7Entity)
    {
        final ModelRenderer[] amodelrenderer = this.squidTentacles;
        final int i = amodelrenderer.length;

        for (int j = 0; j < i; ++j)
        {
            final ModelRenderer modelrenderer = amodelrenderer[j];
            modelrenderer.rotateAngleX = par3;
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(final Entity par1Entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.squidBody.render(par7);

        for (int i = 0; i < this.squidTentacles.length; ++i)
        {
            this.squidTentacles[i].render(par7);
        }
    }
}
