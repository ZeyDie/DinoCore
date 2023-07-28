package net.minecraftforge.liquids;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
@Deprecated //See new net.minecraftforge.fluids
public class LiquidEvent extends Event {
    public final LiquidStack liquid;
    public final int x;
    public final int y;
    public final int z;
    public final World world;

    public LiquidEvent(final LiquidStack liquid, final World world, final int x, final int y, final int z)
    {
        this.liquid = liquid;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Mods should fire this event when they move liquids around (pipe networks etc)
     *
     * @author cpw
     *
     */
    public static class LiquidMotionEvent extends LiquidEvent
    {
        public LiquidMotionEvent(final LiquidStack liquid, final World world, final int x, final int y, final int z)
        {
            super(liquid, world, x, y, z);
        }
    }

    /**
     * Mods should fire this event when a liquid is {@link ILiquidTank#fill(LiquidStack, boolean)} their tank implementation.
     * {@link LiquidTank} does.
     *
     * @author cpw
     *
     */
    public static class LiquidFillingEvent extends LiquidEvent
    {
        public final ILiquidTank tank;

        public LiquidFillingEvent(final LiquidStack liquid, final World world, final int x, final int y, final int z, final ILiquidTank tank)
        {
            super(liquid, world, x, y, z);
            this.tank = tank;
        }
    }

    /**
     * Mods should fire this event when a liquid is {@link ILiquidTank#drain(int, boolean)} from their tank.
     * @author cpw
     *
     */
    public static class LiquidDrainingEvent extends LiquidEvent
    {
        public final ILiquidTank tank;

        public LiquidDrainingEvent(final LiquidStack liquid, final World world, final int x, final int y, final int z, final ILiquidTank tank)
        {
            super(liquid, world, x, y, z);
            this.tank = tank;
        }
    }


    /**
     * Mods should fire this event when a liquid "spills", for example, if a block containing liquid is broken.
     *
     * @author cpw
     *
     */
    public static class LiquidSpilledEvent extends LiquidEvent
    {
        public LiquidSpilledEvent(final LiquidStack liquid, final World world, final int x, final int y, final int z)
        {
            super(liquid, world, x, y, z);
        }
    }

    /**
     * A handy shortcut for firing the various liquid events
     *
     * @param event
     */
    public static final void fireEvent(final LiquidEvent event)
    {
        MinecraftForge.EVENT_BUS.post(event);
    }
}
