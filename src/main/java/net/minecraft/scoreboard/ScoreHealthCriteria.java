package net.minecraft.scoreboard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.util.Iterator;
import java.util.List;

public class ScoreHealthCriteria extends ScoreDummyCriteria
{
    public ScoreHealthCriteria(final String par1Str)
    {
        super(par1Str);
    }

    public int func_96635_a(final List par1List)
    {
        float f = 0.0F;
        EntityPlayer entityplayer;

        for (final Iterator iterator = par1List.iterator(); iterator.hasNext(); f += entityplayer.getHealth() + entityplayer.getAbsorptionAmount())
        {
            entityplayer = (EntityPlayer)iterator.next();
        }

        if (!par1List.isEmpty())
        {
            f /= (float)par1List.size();
        }

        return MathHelper.ceiling_float_int(f);
    }

    public boolean isReadOnly()
    {
        return true;
    }
}
