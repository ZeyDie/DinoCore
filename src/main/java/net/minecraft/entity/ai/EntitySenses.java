package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

import java.util.ArrayList;
import java.util.List;

public class EntitySenses
{
    EntityLiving entityObj;

    /** Cache of entities which we can see */
    List seenEntities = new ArrayList();

    /** Cache of entities which we cannot see */
    List unseenEntities = new ArrayList();

    public EntitySenses(final EntityLiving par1EntityLiving)
    {
        this.entityObj = par1EntityLiving;
    }

    /**
     * Clears canSeeCachePositive and canSeeCacheNegative.
     */
    public void clearSensingCache()
    {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }

    /**
     * Checks, whether 'our' entity can see the entity given as argument (true) or not (false), caching the result.
     */
    public boolean canSee(final Entity par1Entity)
    {
        if (this.seenEntities.contains(par1Entity))
        {
            return true;
        }
        else if (this.unseenEntities.contains(par1Entity))
        {
            return false;
        }
        else
        {
            this.entityObj.worldObj.theProfiler.startSection("canSee");
            final boolean flag = this.entityObj.canEntityBeSeen(par1Entity);
            this.entityObj.worldObj.theProfiler.endSection();

            if (flag)
            {
                this.seenEntities.add(par1Entity);
            }
            else
            {
                this.unseenEntities.add(par1Entity);
            }

            return flag;
        }
    }
}
