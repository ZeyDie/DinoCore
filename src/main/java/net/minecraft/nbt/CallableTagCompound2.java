package net.minecraft.nbt;

import java.util.concurrent.Callable;

class CallableTagCompound2 implements Callable
{
    final int field_82588_a;

    final NBTTagCompound theNBTTagCompound;

    CallableTagCompound2(final NBTTagCompound par1NBTTagCompound, final int par2)
    {
        this.theNBTTagCompound = par1NBTTagCompound;
        this.field_82588_a = par2;
    }

    public String func_82586_a()
    {
        return NBTBase.NBTTypes[this.field_82588_a];
    }

    public Object call()
    {
        return this.func_82586_a();
    }
}
