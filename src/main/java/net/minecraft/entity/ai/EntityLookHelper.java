package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.bukkit.craftbukkit.v1_6_R3.TrigMath;

public class EntityLookHelper
{
    private EntityLiving entity;

    /**
     * The amount of change that is made each update for an entity facing a direction.
     */
    private float deltaLookYaw;

    /**
     * The amount of change that is made each update for an entity facing a direction.
     */
    private float deltaLookPitch;

    /** Whether or not the entity is trying to look at something. */
    private boolean isLooking;
    private double posX;
    private double posY;
    private double posZ;

    public EntityLookHelper(final EntityLiving par1EntityLiving)
    {
        this.entity = par1EntityLiving;
    }

    /**
     * Sets position to look at using entity
     */
    public void setLookPositionWithEntity(final Entity par1Entity, final float par2, final float par3)
    {
        this.posX = par1Entity.posX;

        if (par1Entity instanceof EntityLivingBase)
        {
            this.posY = par1Entity.posY + (double)par1Entity.getEyeHeight();
        }
        else
        {
            this.posY = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D;
        }

        this.posZ = par1Entity.posZ;
        this.deltaLookYaw = par2;
        this.deltaLookPitch = par3;
        this.isLooking = true;
    }

    /**
     * Sets position to look at
     */
    public void setLookPosition(final double par1, final double par3, final double par5, final float par7, final float par8)
    {
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        this.deltaLookYaw = par7;
        this.deltaLookPitch = par8;
        this.isLooking = true;
    }

    /**
     * Updates look
     */
    public void onUpdateLook()
    {
        this.entity.rotationPitch = 0.0F;

        if (this.isLooking)
        {
            this.isLooking = false;
            final double d0 = this.posX - this.entity.posX;
            final double d1 = this.posY - (this.entity.posY + (double)this.entity.getEyeHeight());
            final double d2 = this.posZ - this.entity.posZ;
            final double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);
            // CraftBukkit start - Math -> TrigMath
            final float f = (float)(TrigMath.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
            final float f1 = (float)(-(TrigMath.atan2(d1, d3) * 180.0D / Math.PI));
            // CraftBukkit end
            this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, f1, this.deltaLookPitch);
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f, this.deltaLookYaw);
        }
        else
        {
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0F);
        }

        final float f2 = MathHelper.wrapAngleTo180_float(this.entity.rotationYawHead - this.entity.renderYawOffset);

        if (!this.entity.getNavigator().noPath())
        {
            if (f2 < -75.0F)
            {
                this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0F;
            }

            if (f2 > 75.0F)
            {
                this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0F;
            }
        }
    }

    private float updateRotation(final float par1, final float par2, final float par3)
    {
        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (f3 > par3)
        {
            f3 = par3;
        }

        if (f3 < -par3)
        {
            f3 = -par3;
        }

        return par1 + f3;
    }
}
