package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityCrit2FX extends EntityFX
{
    /** Entity that had been hit and done the Critical hit on. */
    private Entity theEntity;
    private int currentLife;
    private int maximumLife;
    private String particleName;

    public EntityCrit2FX(final World par1World, final Entity par2Entity)
    {
        this(par1World, par2Entity, "crit");
    }

    public EntityCrit2FX(final World par1World, final Entity par2Entity, final String par3Str)
    {
        super(par1World, par2Entity.posX, par2Entity.boundingBox.minY + (double)(par2Entity.height / 2.0F), par2Entity.posZ, par2Entity.motionX, par2Entity.motionY, par2Entity.motionZ);
        this.theEntity = par2Entity;
        this.maximumLife = 3;
        this.particleName = par3Str;
        this.onUpdate();
    }

    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {}

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        for (int i = 0; i < 16; ++i)
        {
            final double d0 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            final double d1 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            final double d2 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);

            if (d0 * d0 + d1 * d1 + d2 * d2 <= 1.0D)
            {
                final double d3 = this.theEntity.posX + d0 * (double)this.theEntity.width / 4.0D;
                final double d4 = this.theEntity.boundingBox.minY + (double)(this.theEntity.height / 2.0F) + d1 * (double)this.theEntity.height / 4.0D;
                final double d5 = this.theEntity.posZ + d2 * (double)this.theEntity.width / 4.0D;
                this.worldObj.spawnParticle(this.particleName, d3, d4, d5, d0, d1 + 0.2D, d2);
            }
        }

        ++this.currentLife;

        if (this.currentLife >= this.maximumLife)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }
}
