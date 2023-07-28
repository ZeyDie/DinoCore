package net.minecraft.scoreboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ScoreObjective
{
    private final Scoreboard theScoreboard;
    private final String name;

    /** The ScoreObjectiveCriteria for this objetive */
    private final ScoreObjectiveCriteria objectiveCriteria;
    private String displayName;

    public ScoreObjective(final Scoreboard par1Scoreboard, final String par2Str, final ScoreObjectiveCriteria par3ScoreObjectiveCriteria)
    {
        this.theScoreboard = par1Scoreboard;
        this.name = par2Str;
        this.objectiveCriteria = par3ScoreObjectiveCriteria;
        this.displayName = par2Str;
    }

    @SideOnly(Side.CLIENT)
    public Scoreboard getScoreboard()
    {
        return this.theScoreboard;
    }

    public String getName()
    {
        return this.name;
    }

    public ScoreObjectiveCriteria getCriteria()
    {
        return this.objectiveCriteria;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public void setDisplayName(final String par1Str)
    {
        this.displayName = par1Str;
        this.theScoreboard.func_96532_b(this);
    }
}
