package net.minecraft.entity;

import java.util.concurrent.Callable;

class CallableEntityType implements Callable
{
    final Entity theEntity;

    CallableEntityType(final Entity par1Entity)
    {
        this.theEntity = par1Entity;
    }

    public String callEntityType()
    {
        return EntityList.getEntityString(this.theEntity) + " (" + this.theEntity.getClass().getCanonicalName() + ")";
    }

    public Object call()
    {
        return this.callEntityType();
    }
}
