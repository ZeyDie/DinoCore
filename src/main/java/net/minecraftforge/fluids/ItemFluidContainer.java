package net.minecraftforge.fluids;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Reference implementation of {@link IFluidContainerItem}. Use/extend this or implement your own.
 *
 * @author King Lemming
 *
 */
public class ItemFluidContainer extends Item implements IFluidContainerItem
{
    protected int capacity;

    public ItemFluidContainer(final int itemID)
    {
        super(itemID);
    }

    public ItemFluidContainer(final int itemID, final int capacity)
    {
        super(itemID);
        this.capacity = capacity;
    }

    public ItemFluidContainer setCapacity(final int capacity)
    {
        this.capacity = capacity;
        return this;
    }

    /* IFluidContainerItem */
    @Override
    public FluidStack getFluid(final ItemStack container)
    {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid"))
        {
            return null;
        }
        return FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
    }

    @Override
    public int getCapacity(final ItemStack container)
    {
        return capacity;
    }

    @Override
    public int fill(final ItemStack container, final FluidStack resource, final boolean doFill)
    {
        if (resource == null)
        {
            return 0;
        }

        if (!doFill)
        {
            if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid"))
            {
                return Math.min(capacity, resource.amount);
            }

            final FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));

            if (stack == null)
            {
                return Math.min(capacity, resource.amount);
            }

            if (!stack.isFluidEqual(resource))
            {
                return 0;
            }

            return Math.min(capacity - stack.amount, resource.amount);
        }

        if (container.stackTagCompound == null)
        {
            container.stackTagCompound = new NBTTagCompound();
        }

        if (!container.stackTagCompound.hasKey("Fluid"))
        {
            final NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());

            if (capacity < resource.amount)
            {
                fluidTag.setInteger("Amount", capacity);
                container.stackTagCompound.setTag("Fluid", fluidTag);
                return capacity;
            }

            container.stackTagCompound.setTag("Fluid", fluidTag);
            return resource.amount;
        }

        final NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
        final FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidTag);

        if (!stack.isFluidEqual(resource))
        {
            return 0;
        }

        int filled = capacity - stack.amount;
        if (resource.amount < filled)
        {
            stack.amount += resource.amount;
            filled = resource.amount;
        }
        else
        {
            stack.amount = capacity;
        }

        container.stackTagCompound.setTag("Fluid", stack.writeToNBT(fluidTag));
        return filled;
    }

    @Override
    public FluidStack drain(final ItemStack container, final int maxDrain, final boolean doDrain)
    {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid") || maxDrain == 0)
        {
            return null;
        }

        final FluidStack stack = FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
        if (stack == null)
        {
            return null;
        }

        final int drained = Math.min(stack.amount, maxDrain);
        if (doDrain)
        {
            if (maxDrain >= stack.amount)
            {
                container.stackTagCompound.removeTag("Fluid");

                if (container.stackTagCompound.hasNoTags())
                {
                    container.stackTagCompound = null;
                }
                return stack;
            }

            final NBTTagCompound fluidTag = container.stackTagCompound.getCompoundTag("Fluid");
            fluidTag.setInteger("Amount", fluidTag.getInteger("Amount") - maxDrain);
            container.stackTagCompound.setTag("Fluid", fluidTag);
        }
        stack.amount = drained;
        return stack;
    }
}
