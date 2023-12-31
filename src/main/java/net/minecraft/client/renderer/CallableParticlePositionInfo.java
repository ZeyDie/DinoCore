package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.crash.CrashReportCategory;

import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
class CallableParticlePositionInfo implements Callable
{
    final double posX;

    final double posY;

    final double posZ;

    final RenderGlobal globalRenderer;

    CallableParticlePositionInfo(final RenderGlobal par1RenderGlobal, final double par2, final double par4, final double par6)
    {
        this.globalRenderer = par1RenderGlobal;
        this.posX = par2;
        this.posY = par4;
        this.posZ = par6;
    }

    public String callParticlePositionInfo()
    {
        return CrashReportCategory.func_85074_a(this.posX, this.posY, this.posZ);
    }

    public Object call()
    {
        return this.callParticlePositionInfo();
    }
}
