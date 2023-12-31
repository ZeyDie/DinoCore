package net.minecraft.crash;

import net.minecraft.block.Block;

import java.util.concurrent.Callable;

final class CallableBlockType implements Callable
{
    final int blockID;

    CallableBlockType(final int par1)
    {
        this.blockID = par1;
    }

    public String callBlockType()
    {
        try
        {
            return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(this.blockID), Block.blocksList[this.blockID].getUnlocalizedName(), Block.blocksList[this.blockID].getClass().getCanonicalName()});
        }
        catch (final Throwable throwable)
        {
            return "ID #" + this.blockID;
        }
    }

    public Object call()
    {
        return this.callBlockType();
    }
}
