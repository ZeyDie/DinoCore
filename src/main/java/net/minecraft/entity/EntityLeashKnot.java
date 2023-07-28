package net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;

import java.util.Iterator;
import java.util.List;

// CraftBukkit start
// CraftBukkit end

public class EntityLeashKnot extends EntityHanging
{
    public EntityLeashKnot(final World par1World)
    {
        super(par1World);
    }

    public EntityLeashKnot(final World par1World, final int par2, final int par3, final int par4)
    {
        super(par1World, par2, par3, par4, 0);
        this.setPosition((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D);
    }

    protected void entityInit()
    {
        super.entityInit();
    }

    public void setDirection(final int par1) {}

    public int getWidthPixels()
    {
        return 9;
    }

    public int getHeightPixels()
    {
        return 9;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(final double par1)
    {
        return par1 < 1024.0D;
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(final Entity par1Entity) {}

    /**
     * Either write this entity to the NBT tag given and return true, or return false without doing anything. If this
     * returns false the entity is not saved on disk. Ridden entities return false here as they are saved with their
     * rider.
     */
    public boolean writeToNBTOptional(final NBTTagCompound par1NBTTagCompound)
    {
        return false;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {}

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {}

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(final EntityPlayer par1EntityPlayer)
    {
        final ItemStack itemstack = par1EntityPlayer.getHeldItem();
        boolean flag = false;
        double d0;
        List list;
        Iterator iterator;
        EntityLiving entityliving;

        if (itemstack != null && itemstack.itemID == Item.leash.itemID && !this.worldObj.isRemote)
        {
            d0 = 7.0D;
            list = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB(this.posX - d0, this.posY - d0, this.posZ - d0, this.posX + d0, this.posY + d0, this.posZ + d0));

            if (list != null)
            {
                iterator = list.iterator();

                while (iterator.hasNext())
                {
                    entityliving = (EntityLiving)iterator.next();

                    if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == par1EntityPlayer)
                    {
                        // CraftBukkit start
                        if (CraftEventFactory.callPlayerLeashEntityEvent(entityliving, this, par1EntityPlayer).isCancelled())
                        {
                            ((EntityPlayerMP) par1EntityPlayer).playerNetServerHandler.sendPacketToPlayer(new Packet39AttachEntity(1, entityliving, entityliving.getLeashedToEntity()));
                            continue;
                        }
                        // CraftBukkit end
                        entityliving.setLeashedToEntity(this, true);
                        flag = true;
                    }
                }
            }
        }

        if (!this.worldObj.isRemote && !flag)
        {
            // CraftBukkit start - Move below
            // this.setDead();
            boolean die = true;

            // CraftBukkit end
            if (true || par1EntityPlayer.capabilities.isCreativeMode)   // CraftBukkit - Process for non-creative as well
            {
                d0 = 7.0D;
                list = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB(this.posX - d0, this.posY - d0, this.posZ - d0, this.posX + d0, this.posY + d0, this.posZ + d0));

                if (list != null)
                {
                    iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        entityliving = (EntityLiving)iterator.next();

                        if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == this)
                        {
                            // CraftBukkit start
                            if (CraftEventFactory.callPlayerUnleashEntityEvent(entityliving, par1EntityPlayer).isCancelled())
                            {
                                die = false;
                                continue;
                            }

                            entityliving.clearLeashed(true, !par1EntityPlayer.capabilities.isCreativeMode); // false -> survival mode boolean
                            // CraftBukkit end
                        }
                    }
                }
            }

            // CraftBukkit start
            if (die)
            {
                this.setDead();
            }

            // CraftBukkit end
        }

        return true;
    }

    /**
     * checks to make sure painting can be placed there
     */
    public boolean onValidSurface()
    {
        final int i = this.worldObj.getBlockId(this.xPosition, this.yPosition, this.zPosition);
        return Block.blocksList[i] != null && Block.blocksList[i].getRenderType() == 11;
    }

    public static EntityLeashKnot func_110129_a(final World par0World, final int par1, final int par2, final int par3)
    {
        final EntityLeashKnot entityleashknot = new EntityLeashKnot(par0World, par1, par2, par3);
        entityleashknot.forceSpawn = true;
        par0World.spawnEntityInWorld(entityleashknot);
        return entityleashknot;
    }

    public static EntityLeashKnot getKnotForBlock(final World par0World, final int par1, final int par2, final int par3)
    {
        final List list = par0World.getEntitiesWithinAABB(EntityLeashKnot.class, AxisAlignedBB.getAABBPool().getAABB((double)par1 - 1.0D, (double)par2 - 1.0D, (double)par3 - 1.0D, (double)par1 + 1.0D, (double)par2 + 1.0D, (double)par3 + 1.0D));
        final Object object = null;

        if (list != null)
        {
            final Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                final EntityLeashKnot entityleashknot = (EntityLeashKnot)iterator.next();

                if (entityleashknot.xPosition == par1 && entityleashknot.yPosition == par2 && entityleashknot.zPosition == par3)
                {
                    return entityleashknot;
                }
            }
        }

        return null;
    }
}
