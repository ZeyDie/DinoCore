package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityHugeExplodeFX extends EntityFX
{
    private int timeSinceStart;

    /** the maximum time for the explosion */
    private int maximumTime = 8;

    public EntityHugeExplodeFX(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12)
    {
        super(par1World, par2, par4, par6, 0.0D, 0.0D, 0.0D);
    }

    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {}

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        for (int i = 0; i < 6; ++i)
        {
            final double d0 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            final double d1 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            final double d2 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
            this.worldObj.spawnParticle("largeexplode", d0, d1, d2, (double)((float)this.timeSinceStart / (float)this.maximumTime), 0.0D, 0.0D);
        }

        ++this.timeSinceStart;

        if (this.timeSinceStart == this.maximumTime)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 1;
    }
}
