package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.*;

public abstract class ModelBase
{
    public float onGround;
    public boolean isRiding;

    /**
     * This is a list of all the boxes (ModelRenderer.class) in the current model.
     */
    public List boxList = new ArrayList();
    public boolean isChild = true;

    /** A mapping for all texture offsets */
    private Map modelTextureMap = new HashMap();
    public int textureWidth = 64;
    public int textureHeight = 32;

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(final Entity par1Entity, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {}

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity par7Entity) {}

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(final EntityLivingBase par1EntityLivingBase, final float par2, final float par3, final float par4) {}

    public ModelRenderer getRandomModelBox(final Random par1Random)
    {
        return (ModelRenderer)this.boxList.get(par1Random.nextInt(this.boxList.size()));
    }

    protected void setTextureOffset(final String par1Str, final int par2, final int par3)
    {
        this.modelTextureMap.put(par1Str, new TextureOffset(par2, par3));
    }

    public TextureOffset getTextureOffset(final String par1Str)
    {
        return (TextureOffset)this.modelTextureMap.get(par1Str);
    }
}
