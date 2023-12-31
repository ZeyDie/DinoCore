package net.minecraft.scoreboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ScorePlayerTeam extends Team
{
    private final Scoreboard theScoreboard;
    private final String field_96675_b;

    /** A set of all team member usernames. */
    private final Set membershipSet = new HashSet();
    private String field_96673_d;
    private String field_96674_e = "";
    private String colorSuffix = "";
    private boolean allowFriendlyFire = true;
    private boolean field_98301_h = true;

    public ScorePlayerTeam(final Scoreboard par1Scoreboard, final String par2Str)
    {
        this.theScoreboard = par1Scoreboard;
        this.field_96675_b = par2Str;
        this.field_96673_d = par2Str;
    }

    public String func_96661_b()
    {
        return this.field_96675_b;
    }

    public String func_96669_c()
    {
        return this.field_96673_d;
    }

    public void setTeamName(final String par1Str)
    {
        if (par1Str == null)
        {
            throw new IllegalArgumentException("Name cannot be null");
        }
        else
        {
            this.field_96673_d = par1Str;
            this.theScoreboard.func_96538_b(this);
        }
    }

    public Collection getMembershipCollection()
    {
        return this.membershipSet;
    }

    /**
     * Returns the color prefix for the player's team name
     */
    public String getColorPrefix()
    {
        return this.field_96674_e;
    }

    public void setNamePrefix(final String par1Str)
    {
        if (par1Str == null)
        {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        else
        {
            this.field_96674_e = par1Str;
            this.theScoreboard.func_96538_b(this);
        }
    }

    /**
     * Returns the color suffix for the player's team name
     */
    public String getColorSuffix()
    {
        return this.colorSuffix;
    }

    public void setNameSuffix(final String par1Str)
    {
        if (par1Str == null)
        {
            throw new IllegalArgumentException("Suffix cannot be null");
        }
        else
        {
            this.colorSuffix = par1Str;
            this.theScoreboard.func_96538_b(this);
        }
    }

    public String func_142053_d(final String par1Str)
    {
        return this.getColorPrefix() + par1Str + this.getColorSuffix();
    }

    /**
     * Returns the player name including the color prefixes and suffixes
     */
    public static String formatPlayerName(final Team par0Team, final String par1Str)
    {
        return par0Team == null ? par1Str : par0Team.func_142053_d(par1Str);
    }

    public boolean getAllowFriendlyFire()
    {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(final boolean par1)
    {
        this.allowFriendlyFire = par1;
        this.theScoreboard.func_96538_b(this);
    }

    public boolean func_98297_h()
    {
        return this.field_98301_h;
    }

    public void setSeeFriendlyInvisiblesEnabled(final boolean par1)
    {
        this.field_98301_h = par1;
        this.theScoreboard.func_96538_b(this);
    }

    public int func_98299_i()
    {
        int i = 0;

        if (this.getAllowFriendlyFire())
        {
            i |= 1;
        }

        if (this.func_98297_h())
        {
            i |= 2;
        }

        return i;
    }

    @SideOnly(Side.CLIENT)
    public void func_98298_a(final int par1)
    {
        this.setAllowFriendlyFire((par1 & 1) > 0);
        this.setSeeFriendlyInvisiblesEnabled((par1 & 2) > 0);
    }
}
