package net.minecraftforge.liquids;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

import java.util.Map;

/**
 * When creating liquids you should register them with this class.
 *
 * @author CovertJaguar <railcraft.wikispaces.com>
 */
@Deprecated //See new net.minecraftforge.fluids
public abstract class LiquidDictionary
{

    private static BiMap<String, LiquidStack> liquids = HashBiMap.create();

    /**
     * When creating liquids you should call this function.
     *
     * Upon passing it a name and liquid item it will return either
     * a preexisting implementation of that liquid or the liquid passed in.
     *
     *
     * @param name the name of the liquid
     * @param liquid the liquid to use if one doesn't exist
     * @return the matching liquid stack
     */
    public static LiquidStack getOrCreateLiquid(final String name, final LiquidStack liquid)
    {
        if (liquid == null)
        {
            throw new NullPointerException("You cannot register a null LiquidStack");
        }
        final LiquidStack existing = liquids.get(name);
        if(existing != null) {
            return existing.copy();
        }
        liquids.put(name, liquid.copy());

        MinecraftForge.EVENT_BUS.post(new LiquidRegisterEvent(name, liquid));
        return liquid;
    }

    /**
     * Returns the liquid matching the name,
     * if such a liquid exists.
     *
     * Can return null.
     *
     * @param name the name of the liquid
     * @param amount the amout of liquid
     * @return a liquidstack for the requested liquid
     */
    public static LiquidStack getLiquid(final String name, final int amount)
    {
        LiquidStack liquid = liquids.get(name);
        if(liquid == null)
            return null;

        liquid = liquid.copy();
        liquid.amount = amount;
        return liquid;
    }

    public static LiquidStack getCanonicalLiquid(final String name)
    {
        return liquids.get(name);
    }
    /**
     * Get an immutable list of the liquids defined
     *
     * @return the defined liquids
     */
    public static Map<String, LiquidStack> getLiquids()
    {
        return ImmutableMap.copyOf(liquids);
    }
    /**
     * Fired when a new liquid is created
     *
     */
    public static class LiquidRegisterEvent extends Event
    {
        public final String Name;
        public final LiquidStack Liquid;

        public LiquidRegisterEvent(final String name, final LiquidStack liquid)
        {
            this.Name = name;
            this.Liquid = liquid.copy();
        }
    }

    static
    {
        getOrCreateLiquid("Water", new LiquidStack(Block.waterStill, LiquidContainerRegistry.BUCKET_VOLUME));
        getOrCreateLiquid("Lava", new LiquidStack(Block.lavaStill, LiquidContainerRegistry.BUCKET_VOLUME));
    }

    public static String findLiquidName(final LiquidStack reference)
    {
        if (reference != null)
        {
            return liquids.inverse().get(reference);
        }
        else
        {
            return null;
        }
    }

    public static LiquidStack getCanonicalLiquid(final LiquidStack liquidStack)
    {
        return liquids.get(liquids.inverse().get(liquidStack));
    }
}
