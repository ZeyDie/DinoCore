package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelSlime extends ModelBase
{
    /** The slime's bodies, both the inside box and the outside box */
    ModelRenderer slimeBodies;

    /** The slime's right eye */
    ModelRenderer slimeRightEye;

    /** The slime's left eye */
    ModelRenderer slimeLeftEye;

    /** The slime's mouth */
    ModelRenderer slimeMouth;

    public ModelSlime(final int par1)
    {
        this.slimeBodies = new ModelRenderer(this, 0, par1);
        this.slimeBodies.addBox(-4.0F, 16.0F, -4.0F, 8, 8, 8);

        if (par1 > 0)
        {
            this.slimeBodies = new ModelRenderer(this, 0, par1);
            this.slimeBodies.addBox(-3.0F, 17.0F, -3.0F, 6, 6, 6);
            this.slimeRightEye = new ModelRenderer(this, 32, 0);
            this.slimeRightEye.addBox(-3.25F, 18.0F, -3.5F, 2, 2, 2);
            this.slimeLeftEye = new ModelRenderer(this, 32, 4);
            this.slimeLeftEye.addBox(1.25F, 18.0F, -3.5F, 2, 2, 2);
            this.slimeMouth = new ModelRenderer(this, 32, 8);
            this.slimeMouth.addBox(0.0F, 21.0F, -3.5F, 1, 1, 1);
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(final Entity par1Entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        this.slimeBodies.render(par7);

        if (this.slimeRightEye != null)
        {
            this.slimeRightEye.render(par7);
            this.slimeLeftEye.render(par7);
            this.slimeMouth.render(par7);
        }
    }
}
