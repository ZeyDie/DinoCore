package net.minecraft.scoreboard;

import java.util.*;

public class Scoreboard
{
    /** Map of objective names to ScoreObjective objects. */
    private final Map scoreObjectives = new HashMap();
    private final Map field_96543_b = new HashMap();
    private final Map field_96544_c = new HashMap();
    private final ScoreObjective[] field_96541_d = new ScoreObjective[3];
    private final Map field_96542_e = new HashMap();

    /** Map of usernames to ScorePlayerTeam objects. */
    private final Map teamMemberships = new HashMap();

    /**
     * Returns a ScoreObjective for the objective name
     */
    public ScoreObjective getObjective(final String par1Str)
    {
        return (ScoreObjective)this.scoreObjectives.get(par1Str);
    }

    public ScoreObjective func_96535_a(final String par1Str, final ScoreObjectiveCriteria par2ScoreObjectiveCriteria)
    {
        ScoreObjective scoreobjective = this.getObjective(par1Str);

        if (scoreobjective != null)
        {
            throw new IllegalArgumentException("An objective with the name \'" + par1Str + "\' already exists!");
        }
        else
        {
            scoreobjective = new ScoreObjective(this, par1Str, par2ScoreObjectiveCriteria);
            Object object = (List)this.field_96543_b.get(par2ScoreObjectiveCriteria);

            if (object == null)
            {
                object = new ArrayList();
                this.field_96543_b.put(par2ScoreObjectiveCriteria, object);
            }

            ((List)object).add(scoreobjective);
            this.scoreObjectives.put(par1Str, scoreobjective);
            this.func_96522_a(scoreobjective);
            return scoreobjective;
        }
    }

    public Collection func_96520_a(final ScoreObjectiveCriteria par1ScoreObjectiveCriteria)
    {
        final Collection collection = (Collection)this.field_96543_b.get(par1ScoreObjectiveCriteria);
        return collection == null ? new ArrayList() : new ArrayList(collection);
    }

    public Score func_96529_a(final String par1Str, final ScoreObjective par2ScoreObjective)
    {
        Object object = (Map)this.field_96544_c.get(par1Str);

        if (object == null)
        {
            object = new HashMap();
            this.field_96544_c.put(par1Str, object);
        }

        Score score = (Score)((Map)object).get(par2ScoreObjective);

        if (score == null)
        {
            score = new Score(this, par2ScoreObjective, par1Str);
            ((Map)object).put(par2ScoreObjective, score);
        }

        return score;
    }

    public Collection func_96534_i(final ScoreObjective par1ScoreObjective)
    {
        final ArrayList arraylist = new ArrayList();
        final Iterator iterator = this.field_96544_c.values().iterator();

        while (iterator.hasNext())
        {
            final Map map = (Map)iterator.next();
            final Score score = (Score)map.get(par1ScoreObjective);

            if (score != null)
            {
                arraylist.add(score);
            }
        }

        Collections.sort(arraylist, Score.field_96658_a);
        return arraylist;
    }

    public Collection getScoreObjectives()
    {
        return this.scoreObjectives.values();
    }

    public Collection getObjectiveNames()
    {
        return this.field_96544_c.keySet();
    }

    public void func_96515_c(final String par1Str)
    {
        final Map map = (Map)this.field_96544_c.remove(par1Str);

        if (map != null)
        {
            this.func_96516_a(par1Str);
        }
    }

    public Collection func_96528_e()
    {
        final Collection collection = this.field_96544_c.values();
        final ArrayList arraylist = new ArrayList();
        final Iterator iterator = collection.iterator();

        while (iterator.hasNext())
        {
            final Map map = (Map)iterator.next();
            arraylist.addAll(map.values());
        }

        return arraylist;
    }

    public Map func_96510_d(final String par1Str)
    {
        Object object = (Map)this.field_96544_c.get(par1Str);

        if (object == null)
        {
            object = new HashMap();
        }

        return (Map)object;
    }

    public void func_96519_k(final ScoreObjective par1ScoreObjective)
    {
        this.scoreObjectives.remove(par1ScoreObjective.getName());

        for (int i = 0; i < 3; ++i)
        {
            if (this.func_96539_a(i) == par1ScoreObjective)
            {
                this.func_96530_a(i, (ScoreObjective)null);
            }
        }

        final List list = (List)this.field_96543_b.get(par1ScoreObjective.getCriteria());

        if (list != null)
        {
            list.remove(par1ScoreObjective);
        }

        final Iterator iterator = this.field_96544_c.values().iterator();

        while (iterator.hasNext())
        {
            final Map map = (Map)iterator.next();
            map.remove(par1ScoreObjective);
        }

        this.func_96533_c(par1ScoreObjective);
    }

    public void func_96530_a(final int par1, final ScoreObjective par2ScoreObjective)
    {
        this.field_96541_d[par1] = par2ScoreObjective;
    }

    public ScoreObjective func_96539_a(final int par1)
    {
        return this.field_96541_d[par1];
    }

    public ScorePlayerTeam func_96508_e(final String par1Str)
    {
        return (ScorePlayerTeam)this.field_96542_e.get(par1Str);
    }

    public ScorePlayerTeam createTeam(final String par1Str)
    {
        ScorePlayerTeam scoreplayerteam = this.func_96508_e(par1Str);

        if (scoreplayerteam != null)
        {
            throw new IllegalArgumentException("An objective with the name \'" + par1Str + "\' already exists!");
        }
        else
        {
            scoreplayerteam = new ScorePlayerTeam(this, par1Str);
            this.field_96542_e.put(par1Str, scoreplayerteam);
            this.func_96523_a(scoreplayerteam);
            return scoreplayerteam;
        }
    }

    public void func_96511_d(final ScorePlayerTeam par1ScorePlayerTeam)
    {
        this.field_96542_e.remove(par1ScorePlayerTeam.func_96661_b());
        final Iterator iterator = par1ScorePlayerTeam.getMembershipCollection().iterator();

        while (iterator.hasNext())
        {
            final String s = (String)iterator.next();
            this.teamMemberships.remove(s);
        }

        this.func_96513_c(par1ScorePlayerTeam);
    }

    public void addPlayerToTeam(final String par1Str, final ScorePlayerTeam par2ScorePlayerTeam)
    {
        if (this.getPlayersTeam(par1Str) != null)
        {
            this.removePlayerFromTeams(par1Str);
        }

        this.teamMemberships.put(par1Str, par2ScorePlayerTeam);
        par2ScorePlayerTeam.getMembershipCollection().add(par1Str);
    }

    public boolean removePlayerFromTeams(final String par1Str)
    {
        final ScorePlayerTeam scoreplayerteam = this.getPlayersTeam(par1Str);

        if (scoreplayerteam != null)
        {
            this.removePlayerFromTeam(par1Str, scoreplayerteam);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Removes the given username from the given ScorePlayerTeam. If the player is not on the team then an
     * IllegalStateException is thrown.
     */
    public void removePlayerFromTeam(final String par1Str, final ScorePlayerTeam par2ScorePlayerTeam)
    {
        if (this.getPlayersTeam(par1Str) != par2ScorePlayerTeam)
        {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team \'" + par2ScorePlayerTeam.func_96661_b() + "\'.");
        }
        else
        {
            this.teamMemberships.remove(par1Str);
            par2ScorePlayerTeam.getMembershipCollection().remove(par1Str);
        }
    }

    public Collection func_96531_f()
    {
        return this.field_96542_e.keySet();
    }

    public Collection func_96525_g()
    {
        return this.field_96542_e.values();
    }

    /**
     * Gets the ScorePlayerTeam object for the given username.
     */
    public ScorePlayerTeam getPlayersTeam(final String par1Str)
    {
        return (ScorePlayerTeam)this.teamMemberships.get(par1Str);
    }

    public void func_96522_a(final ScoreObjective par1ScoreObjective) {}

    public void func_96532_b(final ScoreObjective par1ScoreObjective) {}

    public void func_96533_c(final ScoreObjective par1ScoreObjective) {}

    public void func_96536_a(final Score par1Score) {}

    public void func_96516_a(final String par1Str) {}

    public void func_96523_a(final ScorePlayerTeam par1ScorePlayerTeam) {}

    public void func_96538_b(final ScorePlayerTeam par1ScorePlayerTeam) {}

    public void func_96513_c(final ScorePlayerTeam par1ScorePlayerTeam) {}

    /**
     * Returns 'list' for 0, 'sidebar' for 1, 'belowName for 2, otherwise null.
     */
    public static String getObjectiveDisplaySlot(final int par0)
    {
        switch (par0)
        {
            case 0:
                return "list";
            case 1:
                return "sidebar";
            case 2:
                return "belowName";
            default:
                return null;
        }
    }

    /**
     * Returns 0 for (case-insensitive) 'list', 1 for 'sidebar', 2 for 'belowName', otherwise -1.
     */
    public static int getObjectiveDisplaySlotNumber(final String par0Str)
    {
        return par0Str.equalsIgnoreCase("list") ? 0 : (par0Str.equalsIgnoreCase("sidebar") ? 1 : (par0Str.equalsIgnoreCase("belowName") ? 2 : -1));
    }
}
