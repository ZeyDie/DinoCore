package net.minecraft.world;

import net.minecraft.block.Block;

import java.util.concurrent.Callable;

class CallableLvl1 implements Callable
{
    final int field_85179_a;

    /** Reference to the World object. */
    final World theWorld;

    CallableLvl1(final World par1World, final int par2)
    {
        this.theWorld = par1World;
        this.field_85179_a = par2;
    }

    public String getWorldEntitiesAsString()
    {
        try
        {
            return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(this.field_85179_a), Block.blocksList[this.field_85179_a].getUnlocalizedName(), Block.blocksList[this.field_85179_a].getClass().getCanonicalName()});
        }
        catch (final Throwable throwable)
        {
            return "ID #" + this.field_85179_a;
        }
    }

    public Object call()
    {
        return this.getWorldEntitiesAsString();
    }
}
