package net.minecraft.scoreboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class Team
{
    /**
     * Same as ==
     */
    public boolean isSameTeam(final Team par1Team)
    {
        return par1Team == null ? false : this == par1Team;
    }

    public abstract String func_96661_b();

    public abstract String func_142053_d(String s);

    @SideOnly(Side.CLIENT)
    public abstract boolean func_98297_h();

    public abstract boolean getAllowFriendlyFire();
}
