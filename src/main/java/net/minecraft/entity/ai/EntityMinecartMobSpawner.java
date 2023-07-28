package net.minecraft.entity.ai;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart
{
    /** Mob spawner logic for this spawner minecart. */
    private final MobSpawnerBaseLogic mobSpawnerLogic = new EntityMinecartMobSpawnerLogic(this);

    public EntityMinecartMobSpawner(final World par1World)
    {
        super(par1World);
    }

    public EntityMinecartMobSpawner(final World par1World, final double par2, final double par4, final double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public int getMinecartType()
    {
        return 4;
    }

    public Block getDefaultDisplayTile()
    {
        return Block.mobSpawner;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.mobSpawnerLogic.readFromNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        this.mobSpawnerLogic.writeToNBT(par1NBTTagCompound);
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(final byte par1)
    {
        this.mobSpawnerLogic.setDelayToMin(par1);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
        this.mobSpawnerLogic.updateSpawner();
    }

    @SideOnly(Side.CLIENT)
    public MobSpawnerBaseLogic func_98039_d()
    {
        return this.mobSpawnerLogic;
    }
}
