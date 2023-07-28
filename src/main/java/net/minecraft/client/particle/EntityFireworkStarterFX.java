package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityFireworkStarterFX extends EntityFX
{
    private int fireworkAge;
    private final EffectRenderer theEffectRenderer;
    private NBTTagList fireworkExplosions;
    boolean twinkle;

    public EntityFireworkStarterFX(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12, final EffectRenderer par14EffectRenderer, final NBTTagCompound par15NBTTagCompound)
    {
        super(par1World, par2, par4, par6, 0.0D, 0.0D, 0.0D);
        this.motionX = par8;
        this.motionY = par10;
        this.motionZ = par12;
        this.theEffectRenderer = par14EffectRenderer;
        this.particleMaxAge = 8;

        if (par15NBTTagCompound != null)
        {
            this.fireworkExplosions = par15NBTTagCompound.getTagList("Explosions");

            if (this.fireworkExplosions != null && this.fireworkExplosions.tagCount() == 0)
            {
                this.fireworkExplosions = null;
            }
            else if (this.fireworkExplosions != null)
            {
                this.particleMaxAge = this.fireworkExplosions.tagCount() * 2 - 1;

                for (int i = 0; i < this.fireworkExplosions.tagCount(); ++i)
                {
                    final NBTTagCompound nbttagcompound1 = (NBTTagCompound)this.fireworkExplosions.tagAt(i);

                    if (nbttagcompound1.getBoolean("Flicker"))
                    {
                        this.twinkle = true;
                        this.particleMaxAge += 15;
                        break;
                    }
                }
            }
        }
    }

    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {}

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        boolean flag;

        if (this.fireworkAge == 0 && this.fireworkExplosions != null)
        {
            flag = this.func_92037_i();
            boolean flag1 = false;

            if (this.fireworkExplosions.tagCount() >= 3)
            {
                flag1 = true;
            }
            else
            {
                for (int i = 0; i < this.fireworkExplosions.tagCount(); ++i)
                {
                    final NBTTagCompound nbttagcompound = (NBTTagCompound)this.fireworkExplosions.tagAt(i);

                    if (nbttagcompound.getByte("Type") == 1)
                    {
                        flag1 = true;
                        break;
                    }
                }
            }

            final String s = "fireworks." + (flag1 ? "largeBlast" : "blast") + (flag ? "_far" : "");
            this.worldObj.playSound(this.posX, this.posY, this.posZ, s, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
        }

        if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.tagCount())
        {
            final int j = this.fireworkAge / 2;
            final NBTTagCompound nbttagcompound1 = (NBTTagCompound)this.fireworkExplosions.tagAt(j);
            final byte b0 = nbttagcompound1.getByte("Type");
            final boolean flag2 = nbttagcompound1.getBoolean("Trail");
            final boolean flag3 = nbttagcompound1.getBoolean("Flicker");
            final int[] aint = nbttagcompound1.getIntArray("Colors");
            final int[] aint1 = nbttagcompound1.getIntArray("FadeColors");

            if (b0 == 1)
            {
                this.createBall(0.5D, 4, aint, aint1, flag2, flag3);
            }
            else if (b0 == 2)
            {
                this.createShaped(0.5D, new double[][] {{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, aint, aint1, flag2, flag3, false);
            }
            else if (b0 == 3)
            {
                this.createShaped(0.5D, new double[][] {{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, aint, aint1, flag2, flag3, true);
            }
            else if (b0 == 4)
            {
                this.createBurst(aint, aint1, flag2, flag3);
            }
            else
            {
                this.createBall(0.25D, 2, aint, aint1, flag2, flag3);
            }

            final int k = aint[0];
            final float f = (float)((k & 16711680) >> 16) / 255.0F;
            final float f1 = (float)((k & 65280) >> 8) / 255.0F;
            final float f2 = (float)((k & 255) >> 0) / 255.0F;
            final EntityFireworkOverlayFX entityfireworkoverlayfx = new EntityFireworkOverlayFX(this.worldObj, this.posX, this.posY, this.posZ);
            entityfireworkoverlayfx.setRBGColorF(f, f1, f2);
            this.theEffectRenderer.addEffect(entityfireworkoverlayfx);
        }

        ++this.fireworkAge;

        if (this.fireworkAge > this.particleMaxAge)
        {
            if (this.twinkle)
            {
                flag = this.func_92037_i();
                final String s1 = "fireworks." + (flag ? "twinkle_far" : "twinkle");
                this.worldObj.playSound(this.posX, this.posY, this.posZ, s1, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
            }

            this.setDead();
        }
    }

    private boolean func_92037_i()
    {
        final Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft == null || minecraft.renderViewEntity == null || minecraft.renderViewEntity.getDistanceSq(this.posX, this.posY, this.posZ) >= 256.0D;
    }

    /**
     * Creates a single particle. Args: x, y, z, x velocity, y velocity, z velocity, colours, fade colours, whether to
     * trail, whether to twinkle
     */
    private void createParticle(final double par1, final double par3, final double par5, final double par7, final double par9, final double par11, final int[] par13ArrayOfInteger, final int[] par14ArrayOfInteger, final boolean par15, final boolean par16)
    {
        final EntityFireworkSparkFX entityfireworksparkfx = new EntityFireworkSparkFX(this.worldObj, par1, par3, par5, par7, par9, par11, this.theEffectRenderer);
        entityfireworksparkfx.setTrail(par15);
        entityfireworksparkfx.setTwinkle(par16);
        final int i = this.rand.nextInt(par13ArrayOfInteger.length);
        entityfireworksparkfx.setColour(par13ArrayOfInteger[i]);

        if (par14ArrayOfInteger != null && par14ArrayOfInteger.length > 0)
        {
            entityfireworksparkfx.setFadeColour(par14ArrayOfInteger[this.rand.nextInt(par14ArrayOfInteger.length)]);
        }

        this.theEffectRenderer.addEffect(entityfireworksparkfx);
    }

    /**
     * Creates a small ball or large ball type explosion. Args: particle speed, size, colours, fade colours, whether to
     * trail, whether to flicker
     */
    private void createBall(final double par1, final int par3, final int[] par4ArrayOfInteger, final int[] par5ArrayOfInteger, final boolean par6, final boolean par7)
    {
        final double d1 = this.posX;
        final double d2 = this.posY;
        final double d3 = this.posZ;

        for (int j = -par3; j <= par3; ++j)
        {
            for (int k = -par3; k <= par3; ++k)
            {
                for (int l = -par3; l <= par3; ++l)
                {
                    final double d4 = (double)k + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                    final double d5 = (double)j + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                    final double d6 = (double)l + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                    final double d7 = (double)MathHelper.sqrt_double(d4 * d4 + d5 * d5 + d6 * d6) / par1 + this.rand.nextGaussian() * 0.05D;
                    this.createParticle(d1, d2, d3, d4 / d7, d5 / d7, d6 / d7, par4ArrayOfInteger, par5ArrayOfInteger, par6, par7);

                    if (j != -par3 && j != par3 && k != -par3 && k != par3)
                    {
                        l += par3 * 2 - 1;
                    }
                }
            }
        }
    }

    /**
     * Creates a creeper-shaped or star-shaped explosion. Args: particle speed, shape, colours, fade colours, whether to
     * trail, whether to flicker, unknown
     */
    private void createShaped(final double par1, final double[][] par3ArrayOfDouble, final int[] par4ArrayOfInteger, final int[] par5ArrayOfInteger, final boolean par6, final boolean par7, final boolean par8)
    {
        final double d1 = par3ArrayOfDouble[0][0];
        final double d2 = par3ArrayOfDouble[0][1];
        this.createParticle(this.posX, this.posY, this.posZ, d1 * par1, d2 * par1, 0.0D, par4ArrayOfInteger, par5ArrayOfInteger, par6, par7);
        final float f = this.rand.nextFloat() * (float)Math.PI;
        final double d3 = par8 ? 0.034D : 0.34D;

        for (int i = 0; i < 3; ++i)
        {
            final double d4 = (double)f + (double)((float)i * (float)Math.PI) * d3;
            double d5 = d1;
            double d6 = d2;

            for (int j = 1; j < par3ArrayOfDouble.length; ++j)
            {
                final double d7 = par3ArrayOfDouble[j][0];
                final double d8 = par3ArrayOfDouble[j][1];

                for (double d9 = 0.25D; d9 <= 1.0D; d9 += 0.25D)
                {
                    double d10 = (d5 + (d7 - d5) * d9) * par1;
                    final double d11 = (d6 + (d8 - d6) * d9) * par1;
                    final double d12 = d10 * Math.sin(d4);
                    d10 *= Math.cos(d4);

                    for (double d13 = -1.0D; d13 <= 1.0D; d13 += 2.0D)
                    {
                        this.createParticle(this.posX, this.posY, this.posZ, d10 * d13, d11, d12 * d13, par4ArrayOfInteger, par5ArrayOfInteger, par6, par7);
                    }
                }

                d5 = d7;
                d6 = d8;
            }
        }
    }

    /**
     * Creates a burst type explosion. Args: colours, fade colours, whether to trail, whether to flicker
     */
    private void createBurst(final int[] par1ArrayOfInteger, final int[] par2ArrayOfInteger, final boolean par3, final boolean par4)
    {
        final double d0 = this.rand.nextGaussian() * 0.05D;
        final double d1 = this.rand.nextGaussian() * 0.05D;

        for (int i = 0; i < 70; ++i)
        {
            final double d2 = this.motionX * 0.5D + this.rand.nextGaussian() * 0.15D + d0;
            final double d3 = this.motionZ * 0.5D + this.rand.nextGaussian() * 0.15D + d1;
            final double d4 = this.motionY * 0.5D + this.rand.nextDouble() * 0.5D;
            this.createParticle(this.posX, this.posY, this.posZ, d2, d4, d3, par1ArrayOfInteger, par2ArrayOfInteger, par3, par4);
        }
    }

    public int getFXLayer()
    {
        return 0;
    }
}
