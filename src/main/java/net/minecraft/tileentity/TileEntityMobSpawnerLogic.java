package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.world.World;

class TileEntityMobSpawnerLogic extends MobSpawnerBaseLogic
{
    /** The mob spawner we deal with */
    final TileEntityMobSpawner mobSpawnerEntity;

    TileEntityMobSpawnerLogic(final TileEntityMobSpawner par1TileEntityMobSpawner)
    {
        this.mobSpawnerEntity = par1TileEntityMobSpawner;
    }

    public void func_98267_a(final int par1)
    {
        this.mobSpawnerEntity.worldObj.addBlockEvent(this.mobSpawnerEntity.xCoord, this.mobSpawnerEntity.yCoord, this.mobSpawnerEntity.zCoord, Block.mobSpawner.blockID, par1, 0);
    }

    public World getSpawnerWorld()
    {
        return this.mobSpawnerEntity.worldObj;
    }

    public int getSpawnerX()
    {
        return this.mobSpawnerEntity.xCoord;
    }

    public int getSpawnerY()
    {
        return this.mobSpawnerEntity.yCoord;
    }

    public int getSpawnerZ()
    {
        return this.mobSpawnerEntity.zCoord;
    }

    public void setRandomMinecart(final WeightedRandomMinecart par1WeightedRandomMinecart)
    {
        super.setRandomMinecart(par1WeightedRandomMinecart);

        if (this.getSpawnerWorld() != null)
        {
            this.getSpawnerWorld().markBlockForUpdate(this.mobSpawnerEntity.xCoord, this.mobSpawnerEntity.yCoord, this.mobSpawnerEntity.zCoord);
        }
    }
}
