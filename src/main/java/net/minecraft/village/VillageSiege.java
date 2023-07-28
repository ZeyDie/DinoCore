package net.minecraft.village;

import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class VillageSiege
{
    private World worldObj;
    private boolean field_75535_b;
    private int field_75536_c = -1;
    private int field_75533_d;
    private int field_75534_e;

    /** Instance of Village. */
    private Village theVillage;
    private int field_75532_g;
    private int field_75538_h;
    private int field_75539_i;

    public VillageSiege(final World par1World)
    {
        this.worldObj = par1World;
    }

    /**
     * Runs a single tick for the village siege
     */
    public void tick()
    {
        final boolean flag = false;

        if (flag)
        {
            if (this.field_75536_c == 2)
            {
                this.field_75533_d = 100;
                return;
            }
        }
        else
        {
            if (this.worldObj.isDaytime())
            {
                this.field_75536_c = 0;
                return;
            }

            if (this.field_75536_c == 2)
            {
                return;
            }

            if (this.field_75536_c == 0)
            {
                final float f = this.worldObj.getCelestialAngle(0.0F);

                if ((double)f < 0.5D || (double)f > 0.501D)
                {
                    return;
                }

                this.field_75536_c = this.worldObj.rand.nextInt(10) == 0 ? 1 : 2;
                this.field_75535_b = false;

                if (this.field_75536_c == 2)
                {
                    return;
                }
            }
        }

        if (!this.field_75535_b)
        {
            if (!this.func_75529_b())
            {
                return;
            }

            this.field_75535_b = true;
        }

        if (this.field_75534_e > 0)
        {
            --this.field_75534_e;
        }
        else
        {
            this.field_75534_e = 2;

            if (this.field_75533_d > 0)
            {
                this.spawnZombie();
                --this.field_75533_d;
            }
            else
            {
                this.field_75536_c = 2;
            }
        }
    }

    private boolean func_75529_b()
    {
        final List list = this.worldObj.playerEntities;
        final Iterator iterator = list.iterator();

        while (iterator.hasNext())
        {
            final EntityPlayer entityplayer = (EntityPlayer)iterator.next();
            this.theVillage = this.worldObj.villageCollectionObj.findNearestVillage((int)entityplayer.posX, (int)entityplayer.posY, (int)entityplayer.posZ, 1);

            if (this.theVillage != null && this.theVillage.getNumVillageDoors() >= 10 && this.theVillage.getTicksSinceLastDoorAdding() >= 20 && this.theVillage.getNumVillagers() >= 20)
            {
                final ChunkCoordinates chunkcoordinates = this.theVillage.getCenter();
                final float f = (float)this.theVillage.getVillageRadius();
                boolean flag = false;
                int i = 0;

                while (true)
                {
                    if (i < 10)
                    {
                        this.field_75532_g = chunkcoordinates.posX + (int)((double)(MathHelper.cos(this.worldObj.rand.nextFloat() * (float)Math.PI * 2.0F) * f) * 0.9D);
                        this.field_75538_h = chunkcoordinates.posY;
                        this.field_75539_i = chunkcoordinates.posZ + (int)((double)(MathHelper.sin(this.worldObj.rand.nextFloat() * (float)Math.PI * 2.0F) * f) * 0.9D);
                        flag = false;
                        final Iterator iterator1 = this.worldObj.villageCollectionObj.getVillageList().iterator();

                        while (iterator1.hasNext())
                        {
                            final Village village = (Village)iterator1.next();

                            if (village != this.theVillage && village.isInRange(this.field_75532_g, this.field_75538_h, this.field_75539_i))
                            {
                                flag = true;
                                break;
                            }
                        }

                        if (flag)
                        {
                            ++i;
                            continue;
                        }
                    }

                    if (flag)
                    {
                        return false;
                    }

                    final Vec3 vec3 = this.func_75527_a(this.field_75532_g, this.field_75538_h, this.field_75539_i);

                    if (vec3 != null)
                    {
                        this.field_75534_e = 0;
                        this.field_75533_d = 20;
                        return true;
                    }

                    break;
                }
            }
        }

        return false;
    }

    private boolean spawnZombie()
    {
        final Vec3 vec3 = this.func_75527_a(this.field_75532_g, this.field_75538_h, this.field_75539_i);

        if (vec3 == null)
        {
            return false;
        }
        else
        {
            final EntityZombie entityzombie;

            try
            {
                entityzombie = new EntityZombie(this.worldObj);
                entityzombie.onSpawnWithEgg((EntityLivingData)null);
                entityzombie.setVillager(false);
            }
            catch (final Exception exception)
            {
                exception.printStackTrace();
                return false;
            }

            entityzombie.setLocationAndAngles(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
            this.worldObj.addEntity(entityzombie, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION); // CraftBukkit
            final ChunkCoordinates chunkcoordinates = this.theVillage.getCenter();
            entityzombie.setHomeArea(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this.theVillage.getVillageRadius());
            return true;
        }
    }

    private Vec3 func_75527_a(final int par1, final int par2, final int par3)
    {
        for (int l = 0; l < 10; ++l)
        {
            final int i1 = par1 + this.worldObj.rand.nextInt(16) - 8;
            final int j1 = par2 + this.worldObj.rand.nextInt(6) - 3;
            final int k1 = par3 + this.worldObj.rand.nextInt(16) - 8;

            if (this.theVillage.isInRange(i1, j1, k1) && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, this.worldObj, i1, j1, k1))
            {
                // CraftBukkit - add Return
                return this.worldObj.getWorldVec3Pool().getVecFromPool((double) i1, (double) j1, (double) k1);
            }
        }

        return null;
    }
}
