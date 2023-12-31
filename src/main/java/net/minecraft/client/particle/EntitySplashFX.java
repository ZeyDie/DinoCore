package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySplashFX extends EntityRainFX
{
    public EntitySplashFX(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12)
    {
        super(par1World, par2, par4, par6);
        this.particleGravity = 0.04F;
        this.nextTextureIndexX();

        if (par10 == 0.0D && (par8 != 0.0D || par12 != 0.0D))
        {
            this.motionX = par8;
            this.motionY = par10 + 0.1D;
            this.motionZ = par12;
        }
    }
}
