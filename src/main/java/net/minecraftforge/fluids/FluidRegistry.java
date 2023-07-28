package net.minecraftforge.fluids;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles Fluid registrations. Fluids MUST be registered in order to function.
 *
 * @author King Lemming, CovertJaguar (LiquidDictionary)
 *
 */
public abstract class FluidRegistry
{
    static int maxID = 0;

    static HashMap<String, Fluid> fluids = new HashMap();
    static BiMap<String, Integer> fluidIDs = HashBiMap.create();
    static BiMap<Block, Fluid> fluidBlocks;
    
    public static final Fluid WATER = new Fluid("water") {
        @Override
        public String getLocalizedName() {
            return StatCollector.translateToLocal("tile.water.name");
        }
    }.setBlockID(Block.waterStill.blockID).setUnlocalizedName(Block.waterStill.getUnlocalizedName());
    
    public static final Fluid LAVA = new Fluid("lava") {
        @Override
        public String getLocalizedName() {
            return StatCollector.translateToLocal("tile.lava.name");
        }
    }.setBlockID(Block.lavaStill.blockID).setLuminosity(15).setDensity(3000).setViscosity(6000).setTemperature(1300).setUnlocalizedName(Block.lavaStill.getUnlocalizedName());

    public static int renderIdFluid = -1;

    static
    {
        registerFluid(WATER);
        registerFluid(LAVA);
    }

    private FluidRegistry(){}

    /**
     * Called by Forge to prepare the ID map for server -> client sync.
     */
    static void initFluidIDs(final BiMap<String, Integer> newfluidIDs)
    {
        maxID = newfluidIDs.size();
        fluidIDs.clear();
        fluidIDs.putAll(newfluidIDs);
    }

    /**
     * Register a new Fluid. If a fluid with the same name already exists, registration is denied.
     *
     * @param fluid
     *            The fluid to register.
     * @return True if the fluid was successfully registered; false if there is a name clash.
     */
    public static boolean registerFluid(final Fluid fluid)
    {
        if (fluidIDs.containsKey(fluid.getName()))
        {
            return false;
        }
        fluids.put(fluid.getName(), fluid);
        fluidIDs.put(fluid.getName(), ++maxID);

        MinecraftForge.EVENT_BUS.post(new FluidRegisterEvent(fluid.getName(), maxID));
        return true;
    }

    public static boolean isFluidRegistered(final Fluid fluid)
    {
        return fluidIDs.containsKey(fluid.getName());
    }

    public static boolean isFluidRegistered(final String fluidName)
    {
        return fluidIDs.containsKey(fluidName);
    }

    public static Fluid getFluid(final String fluidName)
    {
        return fluids.get(fluidName);
    }

    public static Fluid getFluid(final int fluidID)
    {
        return fluids.get(getFluidName(fluidID));
    }

    public static String getFluidName(final int fluidID)
    {
        return fluidIDs.inverse().get(fluidID);
    }

    public static String getFluidName(final FluidStack stack)
    {
        return getFluidName(stack.fluidID);
    }

    public static int getFluidID(final String fluidName)
    {
        return fluidIDs.get(fluidName);
    }

    public static FluidStack getFluidStack(final String fluidName, final int amount)
    {
        if (!fluidIDs.containsKey(fluidName))
        {
            return null;
        }
        return new FluidStack(getFluidID(fluidName), amount);
    }

    /**
     * Returns a read-only map containing Fluid Names and their associated Fluids.
     */
    public static Map<String, Fluid> getRegisteredFluids()
    {
        return ImmutableMap.copyOf(fluids);
    }

    /**
     * Returns a read-only map containing Fluid Names and their associated IDs.
     */
    public static Map<String, Integer> getRegisteredFluidIDs()
    {
        return ImmutableMap.copyOf(fluidIDs);
    }

    public static Fluid lookupFluidForBlock(final Block block)
    {
        if (fluidBlocks == null)
        {
            fluidBlocks = HashBiMap.create();
            for (final Fluid fluid : fluids.values())
            {
                if (fluid.canBePlacedInWorld() && Block.blocksList[fluid.getBlockID()] != null)
                {
                    fluidBlocks.put(Block.blocksList[fluid.getBlockID()], fluid);
                }
            }
        }
        return fluidBlocks.get(block);
    }
    
    public static class FluidRegisterEvent extends Event
    {
        public final String fluidName;
        public final int fluidID;

        public FluidRegisterEvent(final String fluidName, final int fluidID)
        {
            this.fluidName = fluidName;
            this.fluidID = fluidID;
        }
    }
}
