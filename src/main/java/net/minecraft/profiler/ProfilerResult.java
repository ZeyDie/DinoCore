package net.minecraft.profiler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ProfilerResult implements Comparable
{
    public double field_76332_a;
    public double field_76330_b;
    public String field_76331_c;

    public ProfilerResult(final String par1Str, final double par2, final double par4)
    {
        this.field_76331_c = par1Str;
        this.field_76332_a = par2;
        this.field_76330_b = par4;
    }

    public int func_76328_a(final ProfilerResult par1ProfilerResult)
    {
        return par1ProfilerResult.field_76332_a < this.field_76332_a ? -1 : (par1ProfilerResult.field_76332_a > this.field_76332_a ? 1 : par1ProfilerResult.field_76331_c.compareTo(this.field_76331_c));
    }

    @SideOnly(Side.CLIENT)
    public int func_76329_a()
    {
        return (this.field_76331_c.hashCode() & 11184810) + 4473924;
    }

    public int compareTo(final Object par1Obj)
    {
        return this.func_76328_a((ProfilerResult)par1Obj);
    }
}
