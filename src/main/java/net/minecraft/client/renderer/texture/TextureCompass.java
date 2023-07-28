package net.minecraft.client.renderer.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class TextureCompass extends TextureAtlasSprite
{
    /** Current compass heading in radians */
    public double currentAngle;

    /** Speed and direction of compass rotation */
    public double angleDelta;

    public TextureCompass(final String par1Str)
    {
        super(par1Str);
    }

    public void updateAnimation()
    {
        final Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.theWorld != null && minecraft.thePlayer != null)
        {
            this.updateCompass(minecraft.theWorld, minecraft.thePlayer.posX, minecraft.thePlayer.posZ, (double)minecraft.thePlayer.rotationYaw, false, false);
        }
        else
        {
            this.updateCompass((World)null, 0.0D, 0.0D, 0.0D, true, false);
        }
    }

    /**
     * Updates the compass based on the given x,z coords and camera direction
     */
    public void updateCompass(final World par1World, final double par2, final double par4, double par6, final boolean par8, final boolean par9)
    {
        double par61 = par6;
        if (!this.framesTextureData.isEmpty())
        {
            double d3 = 0.0D;

            if (par1World != null && !par8)
            {
                final ChunkCoordinates chunkcoordinates = par1World.getSpawnPoint();
                final double d4 = (double)chunkcoordinates.posX - par2;
                final double d5 = (double)chunkcoordinates.posZ - par4;
                par61 %= 360.0D;
                d3 = -((par61 - 90.0D) * Math.PI / 180.0D - Math.atan2(d5, d4));

                if (!par1World.provider.isSurfaceWorld())
                {
                    d3 = Math.random() * Math.PI * 2.0D;
                }
            }

            if (par9)
            {
                this.currentAngle = d3;
            }
            else
            {
                double d6;

                for (d6 = d3 - this.currentAngle; d6 < -Math.PI; d6 += (Math.PI * 2.0D))
                {
                    ;
                }

                while (d6 >= Math.PI)
                {
                    d6 -= (Math.PI * 2.0D);
                }

                if (d6 < -1.0D)
                {
                    d6 = -1.0D;
                }

                if (d6 > 1.0D)
                {
                    d6 = 1.0D;
                }

                this.angleDelta += d6 * 0.1D;
                this.angleDelta *= 0.8D;
                this.currentAngle += this.angleDelta;
            }

            int i;

            for (i = (int)((this.currentAngle / (Math.PI * 2.0D) + 1.0D) * (double)this.framesTextureData.size()) % this.framesTextureData.size(); i < 0; i = (i + this.framesTextureData.size()) % this.framesTextureData.size())
            {
                ;
            }

            if (i != this.frameCounter)
            {
                this.frameCounter = i;
                TextureUtil.uploadTextureSub((int[])this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            }
        }
    }
}
