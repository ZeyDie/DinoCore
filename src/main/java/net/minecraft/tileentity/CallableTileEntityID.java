package net.minecraft.tileentity;

import net.minecraft.block.Block;

import java.util.concurrent.Callable;

class CallableTileEntityID implements Callable
{
    final TileEntity theTileEntity;

    CallableTileEntityID(final TileEntity par1TileEntity)
    {
        this.theTileEntity = par1TileEntity;
    }

    public String callTileEntityID()
    {
        final int i = this.theTileEntity.worldObj.getBlockId(this.theTileEntity.xCoord, this.theTileEntity.yCoord, this.theTileEntity.zCoord);

        try
        {
            return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(i), Block.blocksList[i].getUnlocalizedName(), Block.blocksList[i].getClass().getCanonicalName()});
        }
        catch (final Throwable throwable)
        {
            return "ID #" + i;
        }
    }

    public Object call()
    {
        return this.callTileEntityID();
    }
}
