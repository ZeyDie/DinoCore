package net.minecraftforge.event.terraingen;

import net.minecraft.world.World;
import net.minecraftforge.event.Event.HasResult;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Random;

/**
 * This event is fired when a sapling grows a tree.
 * 
 * You can set the result to DENY to prevent the default tree growth.
 */
@HasResult
public class SaplingGrowTreeEvent extends WorldEvent
{
    public final int x;
    public final int y;
    public final int z;
    public final Random rand;
    
    public SaplingGrowTreeEvent(final World world, final Random rand, final int x, final int y, final int z)
    {
        super(world);
        this.rand = rand;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
