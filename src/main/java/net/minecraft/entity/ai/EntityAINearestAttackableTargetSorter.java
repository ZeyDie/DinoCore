package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;

import java.util.Comparator;

public class EntityAINearestAttackableTargetSorter implements Comparator
{
    private final Entity theEntity;

    public EntityAINearestAttackableTargetSorter(final Entity par1Entity)
    {
        this.theEntity = par1Entity;
    }

    public int compareDistanceSq(final Entity par1Entity, final Entity par2Entity)
    {
        final double d0 = this.theEntity.getDistanceSqToEntity(par1Entity);
        final double d1 = this.theEntity.getDistanceSqToEntity(par2Entity);
        return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
    }

    public int compare(final Object par1Obj, final Object par2Obj)
    {
        return this.compareDistanceSq((Entity)par1Obj, (Entity)par2Obj);
    }
}
