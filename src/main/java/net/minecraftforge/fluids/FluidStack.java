
package net.minecraftforge.fluids;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Locale;

/**
 * ItemStack substitute for Fluids.
 *
 * NOTE: Equality is based on the Fluid, not the amount. Use
 * {@link #isFluidStackIdentical(FluidStack)} to determine if FluidID, Amount and NBT Tag are all
 * equal.
 *
 * @author King Lemming, SirSengir (LiquidStack)
 *
 */
public class FluidStack
{
    public int fluidID;
    public int amount;
    public NBTTagCompound tag;

    public FluidStack(final Fluid fluid, final int amount)
    {
        this.fluidID = fluid.getID();
        this.amount = amount;
    }

    public FluidStack(final int fluidID, final int amount)
    {
        this.fluidID = fluidID;
        this.amount = amount;
    }

    public FluidStack(final int fluidID, final int amount, final NBTTagCompound nbt)
    {
        this(fluidID, amount);

        if (nbt != null)
        {
            tag = (NBTTagCompound) nbt.copy();
        }
    }

    public FluidStack(final FluidStack stack, final int amount)
    {
        this(stack.fluidID, amount, stack.tag);
    }

    /**
     * This provides a safe method for retrieving a FluidStack - if the Fluid is invalid, the stack
     * will return as null.
     */
    public static FluidStack loadFluidStackFromNBT(final NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            return null;
        }
        String fluidName = nbt.getString("FluidName");
        if (fluidName == null)
        {
            fluidName = nbt.hasKey("LiquidName") ? nbt.getString("LiquidName").toLowerCase(Locale.ENGLISH) : null;
            fluidName = Fluid.convertLegacyName(fluidName);
        }

        if (fluidName ==null || FluidRegistry.getFluid(fluidName) == null)
        {
            return null;
        }
        final FluidStack stack = new FluidStack(FluidRegistry.getFluidID(fluidName), nbt.getInteger("Amount"));

        if (nbt.hasKey("Tag"))
        {
            stack.tag = nbt.getCompoundTag("Tag");
        }
        else if (nbt.hasKey("extra"))
        {
            stack.tag = nbt.getCompoundTag("extra");
        }
        return stack;
    }

    public NBTTagCompound writeToNBT(final NBTTagCompound nbt)
    {
        nbt.setString("FluidName", FluidRegistry.getFluidName(fluidID));
        nbt.setInteger("Amount", amount);

        if (tag != null)
        {
            nbt.setTag("Tag", tag);
        }
        return nbt;
    }

    public final Fluid getFluid()
    {
        return FluidRegistry.getFluid(fluidID);
    }

    /**
     * @return A copy of this FluidStack
     */
    public FluidStack copy()
    {
        return new FluidStack(fluidID, amount, tag);
    }

    /**
     * Determines if the FluidIDs and NBT Tags are equal. This does not check amounts.
     *
     * @param other
     *            The FluidStack for comparison
     * @return true if the Fluids (IDs and NBT Tags) are the same
     */
    public boolean isFluidEqual(final FluidStack other)
    {
        return other != null && fluidID == other.fluidID && isFluidStackTagEqual(other);
    }

    private boolean isFluidStackTagEqual(final FluidStack other)
    {
        return tag == null ? other.tag == null : other.tag == null ? false : tag.equals(other.tag);
    }

    /**
     * Determines if the NBT Tags are equal. Useful if the FluidIDs are known to be equal.
     */
    public static boolean areFluidStackTagsEqual(final FluidStack stack1, final FluidStack stack2)
    {
        return stack1 == null && stack2 == null ? true : stack1 == null || stack2 == null ? false : stack1.isFluidStackTagEqual(stack2);
    }

    /**
     * Determines if the Fluids are equal and this stack is larger.
     *
     * @param other
     * @return true if this FluidStack contains the other FluidStack (same fluid and >= amount)
     */
    public boolean containsFluid(final FluidStack other)
    {
        return isFluidEqual(other) && amount >= other.amount;
    }

    /**
     * Determines if the FluidIDs, Amounts, and NBT Tags are all equal.
     *
     * @param other
     *            - the FluidStack for comparison
     * @return true if the two FluidStacks are exactly the same
     */
    public boolean isFluidStackIdentical(final FluidStack other)
    {
        return isFluidEqual(other) && amount == other.amount;
    }

    /**
     * Determines if the FluidIDs and NBT Tags are equal compared to a registered container
     * ItemStack. This does not check amounts.
     *
     * @param other
     *            The ItemStack for comparison
     * @return true if the Fluids (IDs and NBT Tags) are the same
     */
    public boolean isFluidEqual(final ItemStack other)
    {
        if (other == null)
        {
            return false;
        }

        if (other.getItem() instanceof IFluidContainerItem)
        {
            return isFluidEqual(((IFluidContainerItem) other.getItem()).getFluid(other));
        }

        return isFluidEqual(FluidContainerRegistry.getFluidForFilledItem(other));
    }

    @Override
    public final int hashCode()
    {
        return fluidID;
    }

    /**
     * Default equality comparison for a FluidStack. Same functionality as isFluidEqual().
     *
     * This is included for use in data structures.
     */
    @Override
    public final boolean equals(final Object o)
    {
        if (!(o instanceof FluidStack))
        {
            return false;
        }

        return isFluidEqual((FluidStack) o);
    }
}
