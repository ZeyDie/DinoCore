
package net.minecraftforge.fluids;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

/**
 * Reference Tile Entity implementation of {@link IFluidHandler}. Use/extend this or write your own.
 * 
 * @author King Lemming
 * 
 */
public class TileFluidHandler extends TileEntity implements IFluidHandler
{
    protected FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        tank.writeToNBT(tag);
    }

    @Override
    public void writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tank.readFromNBT(tag);
    }

    /* IFluidHandler */
    @Override
    public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill)
    {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain)
    {
        if (resource == null || !resource.isFluidEqual(tank.getFluid()))
        {
            return null;
        }
        return tank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain)
    {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(final ForgeDirection from, final Fluid fluid)
    {
        return true;
    }

    @Override
    public boolean canDrain(final ForgeDirection from, final Fluid fluid)
    {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(final ForgeDirection from)
    {
        return new FluidTankInfo[] { tank.getInfo() };
    }
}
