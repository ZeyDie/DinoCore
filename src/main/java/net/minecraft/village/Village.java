package net.minecraft.village;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class Village
{
    private World worldObj;

    /** list of VillageDoorInfo objects */
    private final List villageDoorInfoList = new ArrayList();

    /**
     * This is the sum of all door coordinates and used to calculate the actual village center by dividing by the number
     * of doors.
     */
    private final ChunkCoordinates centerHelper = new ChunkCoordinates(0, 0, 0);

    /** This is the actual village center. */
    private final ChunkCoordinates center = new ChunkCoordinates(0, 0, 0);
    private int villageRadius;
    private int lastAddDoorTimestamp;
    private int tickCounter;
    private int numVillagers;

    /** Timestamp of tick count when villager last bred */
    private int noBreedTicks;

    /** List of player reputations with this village */
    private TreeMap playerReputation = new TreeMap();
    private List villageAgressors = new ArrayList();
    private int numIronGolems;

    public Village() {}

    public Village(final World par1World)
    {
        this.worldObj = par1World;
    }

    public void func_82691_a(final World par1World)
    {
        this.worldObj = par1World;
    }

    /**
     * Called periodically by VillageCollection
     */
    public void tick(final int par1)
    {
        this.tickCounter = par1;
        this.removeDeadAndOutOfRangeDoors();
        this.removeDeadAndOldAgressors();

        if (par1 % 20 == 0)
        {
            this.updateNumVillagers();
        }

        if (par1 % 30 == 0)
        {
            this.updateNumIronGolems();
        }

        final int j = this.numVillagers / 10;

        if (this.numIronGolems < j && this.villageDoorInfoList.size() > 20 && this.worldObj.rand.nextInt(7000) == 0)
        {
            final Vec3 vec3 = this.tryGetIronGolemSpawningLocation(MathHelper.floor_float((float)this.center.posX), MathHelper.floor_float((float)this.center.posY), MathHelper.floor_float((float)this.center.posZ), 2, 4, 2);

            if (vec3 != null)
            {
                final EntityIronGolem entityirongolem = new EntityIronGolem(this.worldObj);
                entityirongolem.setPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                this.worldObj.addEntity(entityirongolem, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE); // CraftBukkit
                ++this.numIronGolems;
            }
        }
    }

    /**
     * Tries up to 10 times to get a valid spawning location before eventually failing and returning null.
     */
    private Vec3 tryGetIronGolemSpawningLocation(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        for (int k1 = 0; k1 < 10; ++k1)
        {
            final int l1 = par1 + this.worldObj.rand.nextInt(16) - 8;
            final int i2 = par2 + this.worldObj.rand.nextInt(6) - 3;
            final int j2 = par3 + this.worldObj.rand.nextInt(16) - 8;

            if (this.isInRange(l1, i2, j2) && this.isValidIronGolemSpawningLocation(l1, i2, j2, par4, par5, par6))
            {
                return this.worldObj.getWorldVec3Pool().getVecFromPool((double)l1, (double)i2, (double)j2);
            }
        }

        return null;
    }

    private boolean isValidIronGolemSpawningLocation(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        if (!this.worldObj.doesBlockHaveSolidTopSurface(par1, par2 - 1, par3))
        {
            return false;
        }
        else
        {
            final int k1 = par1 - par4 / 2;
            final int l1 = par3 - par6 / 2;

            for (int i2 = k1; i2 < k1 + par4; ++i2)
            {
                for (int j2 = par2; j2 < par2 + par5; ++j2)
                {
                    for (int k2 = l1; k2 < l1 + par6; ++k2)
                    {
                        if (this.worldObj.isBlockNormalCube(i2, j2, k2))
                        {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private void updateNumIronGolems()
    {
        final List list = this.worldObj.getEntitiesWithinAABB(EntityIronGolem.class, AxisAlignedBB.getAABBPool().getAABB((double)(this.center.posX - this.villageRadius), (double)(this.center.posY - 4), (double)(this.center.posZ - this.villageRadius), (double)(this.center.posX + this.villageRadius), (double)(this.center.posY + 4), (double)(this.center.posZ + this.villageRadius)));
        this.numIronGolems = list.size();
    }

    private void updateNumVillagers()
    {
        final List list = this.worldObj.getEntitiesWithinAABB(EntityVillager.class, AxisAlignedBB.getAABBPool().getAABB((double)(this.center.posX - this.villageRadius), (double)(this.center.posY - 4), (double)(this.center.posZ - this.villageRadius), (double)(this.center.posX + this.villageRadius), (double)(this.center.posY + 4), (double)(this.center.posZ + this.villageRadius)));
        this.numVillagers = list.size();

        if (this.numVillagers == 0)
        {
            this.playerReputation.clear();
        }
    }

    public ChunkCoordinates getCenter()
    {
        return this.center;
    }

    public int getVillageRadius()
    {
        return this.villageRadius;
    }

    /**
     * Actually get num village door info entries, but that boils down to number of doors. Called by
     * EntityAIVillagerMate and VillageSiege
     */
    public int getNumVillageDoors()
    {
        return this.villageDoorInfoList.size();
    }

    public int getTicksSinceLastDoorAdding()
    {
        return this.tickCounter - this.lastAddDoorTimestamp;
    }

    public int getNumVillagers()
    {
        return this.numVillagers;
    }

    /**
     * Returns true, if the given coordinates are within the bounding box of the village.
     */
    public boolean isInRange(final int par1, final int par2, final int par3)
    {
        return this.center.getDistanceSquared(par1, par2, par3) < (float)(this.villageRadius * this.villageRadius);
    }

    /**
     * called only by class EntityAIMoveThroughVillage
     */
    public List getVillageDoorInfoList()
    {
        return this.villageDoorInfoList;
    }

    public VillageDoorInfo findNearestDoor(final int par1, final int par2, final int par3)
    {
        VillageDoorInfo villagedoorinfo = null;
        int l = Integer.MAX_VALUE;
        final Iterator iterator = this.villageDoorInfoList.iterator();

        while (iterator.hasNext())
        {
            final VillageDoorInfo villagedoorinfo1 = (VillageDoorInfo)iterator.next();
            final int i1 = villagedoorinfo1.getDistanceSquared(par1, par2, par3);

            if (i1 < l)
            {
                villagedoorinfo = villagedoorinfo1;
                l = i1;
            }
        }

        return villagedoorinfo;
    }

    /**
     * Find a door suitable for shelter. If there are more doors in a distance of 16 blocks, then the least restricted
     * one (i.e. the one protecting the lowest number of villagers) of them is chosen, else the nearest one regardless
     * of restriction.
     */
    public VillageDoorInfo findNearestDoorUnrestricted(final int par1, final int par2, final int par3)
    {
        VillageDoorInfo villagedoorinfo = null;
        int l = Integer.MAX_VALUE;
        final Iterator iterator = this.villageDoorInfoList.iterator();

        while (iterator.hasNext())
        {
            final VillageDoorInfo villagedoorinfo1 = (VillageDoorInfo)iterator.next();
            int i1 = villagedoorinfo1.getDistanceSquared(par1, par2, par3);

            if (i1 > 256)
            {
                i1 *= 1000;
            }
            else
            {
                i1 = villagedoorinfo1.getDoorOpeningRestrictionCounter();
            }

            if (i1 < l)
            {
                villagedoorinfo = villagedoorinfo1;
                l = i1;
            }
        }

        return villagedoorinfo;
    }

    public VillageDoorInfo getVillageDoorAt(final int par1, final int par2, final int par3)
    {
        if (this.center.getDistanceSquared(par1, par2, par3) > (float)(this.villageRadius * this.villageRadius))
        {
            return null;
        }
        else
        {
            final Iterator iterator = this.villageDoorInfoList.iterator();
            VillageDoorInfo villagedoorinfo;

            do
            {
                if (!iterator.hasNext())
                {
                    return null;
                }

                villagedoorinfo = (VillageDoorInfo)iterator.next();
            }
            while (villagedoorinfo.posX != par1 || villagedoorinfo.posZ != par3 || Math.abs(villagedoorinfo.posY - par2) > 1);

            return villagedoorinfo;
        }
    }

    public void addVillageDoorInfo(final VillageDoorInfo par1VillageDoorInfo)
    {
        this.villageDoorInfoList.add(par1VillageDoorInfo);
        this.centerHelper.posX += par1VillageDoorInfo.posX;
        this.centerHelper.posY += par1VillageDoorInfo.posY;
        this.centerHelper.posZ += par1VillageDoorInfo.posZ;
        this.updateVillageRadiusAndCenter();
        this.lastAddDoorTimestamp = par1VillageDoorInfo.lastActivityTimestamp;
    }

    /**
     * Returns true, if there is not a single village door left. Called by VillageCollection
     */
    public boolean isAnnihilated()
    {
        return this.villageDoorInfoList.isEmpty();
    }

    public void addOrRenewAgressor(final EntityLivingBase par1EntityLivingBase)
    {
        final Iterator iterator = this.villageAgressors.iterator();
        VillageAgressor villageagressor;

        do
        {
            if (!iterator.hasNext())
            {
                this.villageAgressors.add(new VillageAgressor(this, par1EntityLivingBase, this.tickCounter));
                return;
            }

            villageagressor = (VillageAgressor)iterator.next();
        }
        while (villageagressor.agressor != par1EntityLivingBase);

        villageagressor.agressionTime = this.tickCounter;
    }

    public EntityLivingBase findNearestVillageAggressor(final EntityLivingBase par1EntityLivingBase)
    {
        double d0 = Double.MAX_VALUE;
        VillageAgressor villageagressor = null;

        for (int i = 0; i < this.villageAgressors.size(); ++i)
        {
            final VillageAgressor villageagressor1 = (VillageAgressor)this.villageAgressors.get(i);
            final double d1 = villageagressor1.agressor.getDistanceSqToEntity(par1EntityLivingBase);

            if (d1 <= d0)
            {
                villageagressor = villageagressor1;
                d0 = d1;
            }
        }

        return villageagressor != null ? villageagressor.agressor : null;
    }

    public EntityPlayer func_82685_c(final EntityLivingBase par1EntityLivingBase)
    {
        double d0 = Double.MAX_VALUE;
        EntityPlayer entityplayer = null;
        final Iterator iterator = this.playerReputation.keySet().iterator();

        while (iterator.hasNext())
        {
            final String s = (String)iterator.next();

            if (this.isPlayerReputationTooLow(s))
            {
                final EntityPlayer entityplayer1 = this.worldObj.getPlayerEntityByName(s);

                if (entityplayer1 != null)
                {
                    final double d1 = entityplayer1.getDistanceSqToEntity(par1EntityLivingBase);

                    if (d1 <= d0)
                    {
                        entityplayer = entityplayer1;
                        d0 = d1;
                    }
                }
            }
        }

        return entityplayer;
    }

    private void removeDeadAndOldAgressors()
    {
        final Iterator iterator = this.villageAgressors.iterator();

        while (iterator.hasNext())
        {
            final VillageAgressor villageagressor = (VillageAgressor)iterator.next();

            if (!villageagressor.agressor.isEntityAlive() || Math.abs(this.tickCounter - villageagressor.agressionTime) > 300)
            {
                iterator.remove();
            }
        }
    }

    private void removeDeadAndOutOfRangeDoors()
    {
        boolean flag = false;
        final boolean flag1 = this.worldObj.rand.nextInt(50) == 0;
        final Iterator iterator = this.villageDoorInfoList.iterator();

        while (iterator.hasNext())
        {
            final VillageDoorInfo villagedoorinfo = (VillageDoorInfo)iterator.next();

            if (flag1)
            {
                villagedoorinfo.resetDoorOpeningRestrictionCounter();
            }

            if (!this.isBlockDoor(villagedoorinfo.posX, villagedoorinfo.posY, villagedoorinfo.posZ) || Math.abs(this.tickCounter - villagedoorinfo.lastActivityTimestamp) > 1200)
            {
                this.centerHelper.posX -= villagedoorinfo.posX;
                this.centerHelper.posY -= villagedoorinfo.posY;
                this.centerHelper.posZ -= villagedoorinfo.posZ;
                flag = true;
                villagedoorinfo.isDetachedFromVillageFlag = true;
                iterator.remove();
            }
        }

        if (flag)
        {
            this.updateVillageRadiusAndCenter();
        }
    }

    private boolean isBlockDoor(final int par1, final int par2, final int par3)
    {
        final int l = this.worldObj.getBlockId(par1, par2, par3);
        return l <= 0 ? false : l == Block.doorWood.blockID;
    }

    private void updateVillageRadiusAndCenter()
    {
        final int i = this.villageDoorInfoList.size();

        if (i == 0)
        {
            this.center.set(0, 0, 0);
            this.villageRadius = 0;
        }
        else
        {
            this.center.set(this.centerHelper.posX / i, this.centerHelper.posY / i, this.centerHelper.posZ / i);
            int j = 0;
            VillageDoorInfo villagedoorinfo;

            for (final Iterator iterator = this.villageDoorInfoList.iterator(); iterator.hasNext(); j = Math.max(villagedoorinfo.getDistanceSquared(this.center.posX, this.center.posY, this.center.posZ), j))
            {
                villagedoorinfo = (VillageDoorInfo)iterator.next();
            }

            this.villageRadius = Math.max(32, (int)Math.sqrt((double)j) + 1);
        }
    }

    /**
     * Return the village reputation for a player
     */
    public int getReputationForPlayer(final String par1Str)
    {
        final Integer integer = (Integer)this.playerReputation.get(par1Str);
        return integer != null ? integer.intValue() : 0;
    }

    /**
     * Set the village reputation for a player.
     */
    public int setReputationForPlayer(final String par1Str, final int par2)
    {
        final int j = this.getReputationForPlayer(par1Str);
        final int k = MathHelper.clamp_int(j + par2, -30, 10);
        this.playerReputation.put(par1Str, Integer.valueOf(k));
        return k;
    }

    /**
     * Return whether this player has a too low reputation with this village.
     */
    public boolean isPlayerReputationTooLow(final String par1Str)
    {
        return this.getReputationForPlayer(par1Str) <= -15;
    }

    /**
     * Read this village's data from NBT.
     */
    public void readVillageDataFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        this.numVillagers = par1NBTTagCompound.getInteger("PopSize");
        this.villageRadius = par1NBTTagCompound.getInteger("Radius");
        this.numIronGolems = par1NBTTagCompound.getInteger("Golems");
        this.lastAddDoorTimestamp = par1NBTTagCompound.getInteger("Stable");
        this.tickCounter = par1NBTTagCompound.getInteger("Tick");
        this.noBreedTicks = par1NBTTagCompound.getInteger("MTick");
        this.center.posX = par1NBTTagCompound.getInteger("CX");
        this.center.posY = par1NBTTagCompound.getInteger("CY");
        this.center.posZ = par1NBTTagCompound.getInteger("CZ");
        this.centerHelper.posX = par1NBTTagCompound.getInteger("ACX");
        this.centerHelper.posY = par1NBTTagCompound.getInteger("ACY");
        this.centerHelper.posZ = par1NBTTagCompound.getInteger("ACZ");
        final NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Doors");

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            final VillageDoorInfo villagedoorinfo = new VillageDoorInfo(nbttagcompound1.getInteger("X"), nbttagcompound1.getInteger("Y"), nbttagcompound1.getInteger("Z"), nbttagcompound1.getInteger("IDX"), nbttagcompound1.getInteger("IDZ"), nbttagcompound1.getInteger("TS"));
            this.villageDoorInfoList.add(villagedoorinfo);
        }

        final NBTTagList nbttaglist1 = par1NBTTagCompound.getTagList("Players");

        for (int j = 0; j < nbttaglist1.tagCount(); ++j)
        {
            final NBTTagCompound nbttagcompound2 = (NBTTagCompound)nbttaglist1.tagAt(j);
            this.playerReputation.put(nbttagcompound2.getString("Name"), Integer.valueOf(nbttagcompound2.getInteger("S")));
        }
    }

    /**
     * Write this village's data to NBT.
     */
    public void writeVillageDataToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("PopSize", this.numVillagers);
        par1NBTTagCompound.setInteger("Radius", this.villageRadius);
        par1NBTTagCompound.setInteger("Golems", this.numIronGolems);
        par1NBTTagCompound.setInteger("Stable", this.lastAddDoorTimestamp);
        par1NBTTagCompound.setInteger("Tick", this.tickCounter);
        par1NBTTagCompound.setInteger("MTick", this.noBreedTicks);
        par1NBTTagCompound.setInteger("CX", this.center.posX);
        par1NBTTagCompound.setInteger("CY", this.center.posY);
        par1NBTTagCompound.setInteger("CZ", this.center.posZ);
        par1NBTTagCompound.setInteger("ACX", this.centerHelper.posX);
        par1NBTTagCompound.setInteger("ACY", this.centerHelper.posY);
        par1NBTTagCompound.setInteger("ACZ", this.centerHelper.posZ);
        final NBTTagList nbttaglist = new NBTTagList("Doors");
        final Iterator iterator = this.villageDoorInfoList.iterator();

        while (iterator.hasNext())
        {
            final VillageDoorInfo villagedoorinfo = (VillageDoorInfo)iterator.next();
            final NBTTagCompound nbttagcompound1 = new NBTTagCompound("Door");
            nbttagcompound1.setInteger("X", villagedoorinfo.posX);
            nbttagcompound1.setInteger("Y", villagedoorinfo.posY);
            nbttagcompound1.setInteger("Z", villagedoorinfo.posZ);
            nbttagcompound1.setInteger("IDX", villagedoorinfo.insideDirectionX);
            nbttagcompound1.setInteger("IDZ", villagedoorinfo.insideDirectionZ);
            nbttagcompound1.setInteger("TS", villagedoorinfo.lastActivityTimestamp);
            nbttaglist.appendTag(nbttagcompound1);
        }

        par1NBTTagCompound.setTag("Doors", nbttaglist);
        final NBTTagList nbttaglist1 = new NBTTagList("Players");
        final Iterator iterator1 = this.playerReputation.keySet().iterator();

        while (iterator1.hasNext())
        {
            final String s = (String)iterator1.next();
            final NBTTagCompound nbttagcompound2 = new NBTTagCompound(s);
            nbttagcompound2.setString("Name", s);
            nbttagcompound2.setInteger("S", ((Integer)this.playerReputation.get(s)).intValue());
            nbttaglist1.appendTag(nbttagcompound2);
        }

        par1NBTTagCompound.setTag("Players", nbttaglist1);
    }

    /**
     * Prevent villager breeding for a fixed interval of time
     */
    public void endMatingSeason()
    {
        this.noBreedTicks = this.tickCounter;
    }

    /**
     * Return whether villagers mating refractory period has passed
     */
    public boolean isMatingSeason()
    {
        return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
    }

    public void func_82683_b(final int par1)
    {
        final Iterator iterator = this.playerReputation.keySet().iterator();

        while (iterator.hasNext())
        {
            final String s = (String)iterator.next();
            this.setReputationForPlayer(s, par1);
        }
    }
}
