package net.minecraft.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
class MinecraftINNER13 implements Callable
{
    final Minecraft field_142056_a;

    MinecraftINNER13(final Minecraft par1Minecraft)
    {
        this.field_142056_a = par1Minecraft;
    }

    public String func_142055_a()
    {
        final int i = this.field_142056_a.theWorld.getWorldVec3Pool().getPoolSize();
        final int j = 56 * i;
        final int k = j / 1024 / 1024;
        final int l = this.field_142056_a.theWorld.getWorldVec3Pool().func_82590_d();
        final int i1 = 56 * l;
        final int j1 = i1 / 1024 / 1024;
        return i + " (" + j + " bytes; " + k + " MB) allocated, " + l + " (" + i1 + " bytes; " + j1 + " MB) used";
    }

    public Object call()
    {
        return this.func_142055_a();
    }
}
