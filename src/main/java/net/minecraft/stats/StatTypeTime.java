package net.minecraft.stats;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

final class StatTypeTime implements IStatType
{
    @SideOnly(Side.CLIENT)

    /**
     * Formats a given stat for human consumption.
     */
    public String format(final int par1)
    {
        final double d0 = (double)par1 / 20.0D;
        final double d1 = d0 / 60.0D;
        final double d2 = d1 / 60.0D;
        final double d3 = d2 / 24.0D;
        final double d4 = d3 / 365.0D;
        return d4 > 0.5D ? StatBase.getDecimalFormat().format(d4) + " y" : (d3 > 0.5D ? StatBase.getDecimalFormat().format(d3) + " d" : (d2 > 0.5D ? StatBase.getDecimalFormat().format(d2) + " h" : (d1 > 0.5D ? StatBase.getDecimalFormat().format(d1) + " m" : d0 + " s")));
    }
}
