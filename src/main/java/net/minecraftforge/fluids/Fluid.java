package net.minecraftforge.fluids;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoaderException;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDummyContainer;

import java.util.Locale;
import java.util.Map;

/**
 * Minecraft Forge Fluid Implementation
 *
 * This class is a fluid (liquid or gas) equivalent to "Item." It describes the nature of a fluid
 * and contains its general properties.
 *
 * These properties do not have inherent gameplay mechanics - they are provided so that mods may
 * choose to take advantage of them.
 *
 * Fluid implementations are not required to actively use these properties, nor are objects
 * interfacing with fluids required to make use of them, but it is encouraged.
 *
 * The default values can be used as a reference point for mods adding fluids such as oil or heavy
 * water.
 *
 * @author King Lemming
 *
 */
public class Fluid
{
    /** The unique identification name for this fluid. */
    protected final String fluidName;

    /** The unlocalized name of this fluid. */
    protected String unlocalizedName;

    /** The Icons for this fluid. */
    protected Icon stillIcon;
    protected Icon flowingIcon;

    /**
     * The light level emitted by this fluid.
     *
     * Default value is 0, as most fluids do not actively emit light.
     */
    protected int luminosity = 0;

    /**
     * Density of the fluid - completely arbitrary; negative density indicates that the fluid is
     * lighter than air.
     *
     * Default value is approximately the real-life density of water in kg/m^3.
     */
    protected int density = 1000;

    /**
     * Temperature of the fluid - completely arbitrary; higher temperature indicates that the fluid is
     * hotter than air.
     * 
     * Default value is approximately the real-life room temperature of water in degrees Kelvin.
     */
    protected int temperature = 295;

    /**
     * Viscosity ("thickness") of the fluid - completely arbitrary; negative values are not
     * permissible.
     *
     * Default value is approximately the real-life density of water in m/s^2 (x10^-3).
     */
    protected int viscosity = 1000;

    /**
     * This indicates if the fluid is gaseous.
     *
     * Useful for rendering the fluid in containers and the world.
     *
     * Generally this is associated with negative density fluids.
     */
    protected boolean isGaseous;

    /**
     * The rarity of the fluid.
     *
     * Used primarily in tool tips.
     */
    protected EnumRarity rarity = EnumRarity.common;

    /**
     * If there is a Block implementation of the Fluid, the BlockID is linked here.
     *
     * The default value of -1 should remain for any Fluid without a Block implementation.
     */
    protected int blockID = -1;

    public Fluid(final String fluidName)
    {
        this.fluidName = fluidName.toLowerCase(Locale.ENGLISH);
        this.unlocalizedName = fluidName;
    }

    public Fluid setUnlocalizedName(final String unlocalizedName)
    {
        this.unlocalizedName = unlocalizedName;
        return this;
    }

    public Fluid setBlockID(final int blockID)
    {
        if (this.blockID == -1 || this.blockID == blockID)
        {
            this.blockID = blockID;
        }
        else if (!ForgeDummyContainer.forceDuplicateFluidBlockCrash)
        {
            FMLLog.warning("A mod has attempted to assign BlockID " + blockID + " to the Fluid '" + fluidName + "' but this Fluid has already been linked to BlockID "
                    + this.blockID + ". Configure your mods to prevent this from happening.");
        }
        else
        {
            FMLLog.severe("A mod has attempted to assign BlockID " + blockID + " to the Fluid '" + fluidName + "' but this Fluid has already been linked to BlockID "
                    + this.blockID + ". Configure your mods to prevent this from happening.");
            throw new LoaderException(new RuntimeException("A mod has attempted to assign BlockID " + blockID + " to the Fluid '" + fluidName
                    + "' but this Fluid has already been linked to BlockID " + this.blockID + ". Configure your mods to prevent this from happening."));
        }
        return this;
    }

    public Fluid setBlockID(final Block block)
    {
        return setBlockID(block.blockID);
    }

    public Fluid setLuminosity(final int luminosity)
    {
        this.luminosity = luminosity;
        return this;
    }

    public Fluid setDensity(final int density)
    {
        this.density = density;
        return this;
    }

    public Fluid setTemperature(final int temperature)
    {
        this.temperature = temperature;
        return this;
    }

    public Fluid setViscosity(final int viscosity)
    {
        this.viscosity = viscosity;
        return this;
    }

    public Fluid setGaseous(final boolean isGaseous)
    {
        this.isGaseous = isGaseous;
        return this;
    }

    public Fluid setRarity(final EnumRarity rarity)
    {
        this.rarity = rarity;
        return this;
    }

    public final String getName()
    {
        return this.fluidName;
    }

    public final int getID()
    {
        return FluidRegistry.getFluidID(this.fluidName);
    }

    public final int getBlockID()
    {
        return blockID;
    }

    public final boolean canBePlacedInWorld()
    {
        return blockID != -1;
    }

    /**
     * Returns the localized name of this fluid.
     */
    public String getLocalizedName()
    {
        final String s = this.getUnlocalizedName();
        return s == null ? "" : StatCollector.translateToLocal(s);
    }

    /**
     * Returns the unlocalized name of this fluid.
     */
    public String getUnlocalizedName()
    {
        return "fluid." + this.unlocalizedName;
    }

    /**
     * Returns 0 for "/terrain.png". ALL FLUID TEXTURES MUST BE ON THIS SHEET.
     */
    public final int getSpriteNumber()
    {
        return 0;
    }

    /* Default Accessors */
    public final int getLuminosity()
    {
        return this.luminosity;
    }

    public final int getDensity()
    {
        return this.density;
    }

    public final int getTemperature()
    {
        return this.temperature;
    }

    public final int getViscosity()
    {
        return this.viscosity;
    }

    public final boolean isGaseous()
    {
        return this.isGaseous;
    }
    
    public EnumRarity getRarity()
    {
        return rarity;
    }

    public int getColor()
    {
        return 0xFFFFFF;
    }

    public final Fluid setStillIcon(final Icon stillIcon)
    {
        this.stillIcon = stillIcon;
        return this;
    }

    public final Fluid setFlowingIcon(final Icon flowingIcon)
    {
        this.flowingIcon = flowingIcon;
        return this;
    }

    public final Fluid setIcons(final Icon stillIcon, final Icon flowingIcon)
    {
        return this.setStillIcon(stillIcon).setFlowingIcon(flowingIcon);
    }

    public final Fluid setIcons(final Icon commonIcon)
    {
        return this.setStillIcon(commonIcon).setFlowingIcon(commonIcon);
    }

    public Icon getIcon(){ return getStillIcon(); }

    public Icon getStillIcon()
    {
        return this.stillIcon;
    }

    public Icon getFlowingIcon()
    {
        return this.flowingIcon;
    }

    /* Stack-based Accessors */
    public int getLuminosity(final FluidStack stack){ return getLuminosity(); }
    public int getDensity(final FluidStack stack){ return getDensity(); }
    public int getTemperature(final FluidStack stack){ return getTemperature(); }
    public int getViscosity(final FluidStack stack){ return getViscosity(); }
    public boolean isGaseous(final FluidStack stack){ return isGaseous(); }
    public EnumRarity getRarity(final FluidStack stack){ return getRarity(); }
    public int getColor(final FluidStack stack){ return getColor(); }
    public Icon getIcon(final FluidStack stack){ return getIcon(); }
    /* World-based Accessors */
    public int getLuminosity(final World world, final int x, final int y, final int z){ return getLuminosity(); }
    public int getDensity(final World world, final int x, final int y, final int z){ return getDensity(); }
    public int getTemperature(final World world, final int x, final int y, final int z){ return getTemperature(); }
    public int getViscosity(final World world, final int x, final int y, final int z){ return getViscosity(); }
    public boolean isGaseous(final World world, final int x, final int y, final int z){ return isGaseous(); }
    public EnumRarity getRarity(final World world, final int x, final int y, final int z){ return getRarity(); }
    public int getColor(final World world, final int x, final int y, final int z){ return getColor(); }
    public Icon getIcon(final World world, final int x, final int y, final int z){ return getIcon(); }

    private static Map<String, String> legacyNames = Maps.newHashMap();
    static String convertLegacyName(final String fluidName)
    {
        return fluidName != null && legacyNames.containsKey(fluidName) ? legacyNames.get(fluidName) : fluidName;
    }

    /**
     * Register a legacy liquid name with the Fluids system
     * @param legacyName The legacy name to recognize
     * @param canonicalName The canonical fluid name it will become
     */
    public static void registerLegacyName(final String legacyName, final String canonicalName)
    {
        legacyNames.put(legacyName.toLowerCase(Locale.ENGLISH), canonicalName);
    }
}
