package net.minecraft.village;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VillageCollection extends WorldSavedData
{
    private World worldObj;

    /**
     * This is a black hole. You can add data to this list through a public interface, but you can't query that
     * information in any way and it's not used internally either.
     */
    private final List villagerPositionsList = new ArrayList();
    private final List newDoors = new ArrayList();
    private final List villageList = new ArrayList();
    private int tickCounter;

    public VillageCollection(final String par1Str)
    {
        super(par1Str);
    }

    public VillageCollection(final World par1World)
    {
        super("villages");
        this.worldObj = par1World;
        this.markDirty();
    }

    public void func_82566_a(final World par1World)
    {
        this.worldObj = par1World;
        final Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            final Village village = (Village)iterator.next();
            village.func_82691_a(par1World);
        }
    }

    /**
     * This is a black hole. You can add data to this list through a public interface, but you can't query that
     * information in any way and it's not used internally either.
     */
    public void addVillagerPosition(final int par1, final int par2, final int par3)
    {
        if (this.villagerPositionsList.size() <= 64)
        {
            if (!this.isVillagerPositionPresent(par1, par2, par3))
            {
                this.villagerPositionsList.add(new ChunkCoordinates(par1, par2, par3));
            }
        }
    }

    /**
     * Runs a single tick for the village collection
     */
    public void tick()
    {
        ++this.tickCounter;
        final Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            final Village village = (Village)iterator.next();
            village.tick(this.tickCounter);
        }

        this.removeAnnihilatedVillages();
        this.dropOldestVillagerPosition();
        this.addNewDoorsToVillageOrCreateVillage();

        if (this.tickCounter % 400 == 0)
        {
            this.markDirty();
        }
    }

    private void removeAnnihilatedVillages()
    {
        final Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            final Village village = (Village)iterator.next();

            if (village.isAnnihilated())
            {
                iterator.remove();
                this.markDirty();
            }
        }
    }

    /**
     * Get a list of villages.
     */
    public List getVillageList()
    {
        return this.villageList;
    }

    /**
     * Finds the nearest village, but only the given coordinates are withing it's bounding box plus the given the
     * distance.
     */
    public Village findNearestVillage(final int par1, final int par2, final int par3, final int par4)
    {
        Village village = null;
        float f = Float.MAX_VALUE;
        final Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            final Village village1 = (Village)iterator.next();
            final float f1 = village1.getCenter().getDistanceSquared(par1, par2, par3);

            if (f1 < f)
            {
                final float f2 = (float)(par4 + village1.getVillageRadius());

                if (f1 <= f2 * f2)
                {
                    village = village1;
                    f = f1;
                }
            }
        }

        return village;
    }

    private void dropOldestVillagerPosition()
    {
        if (!this.villagerPositionsList.isEmpty())
        {
            this.addUnassignedWoodenDoorsAroundToNewDoorsList((ChunkCoordinates)this.villagerPositionsList.remove(0));
        }
    }

    private void addNewDoorsToVillageOrCreateVillage()
    {
        int i = 0;

        while (i < this.newDoors.size())
        {
            final VillageDoorInfo villagedoorinfo = (VillageDoorInfo)this.newDoors.get(i);
            boolean flag = false;
            final Iterator iterator = this.villageList.iterator();

            while (true)
            {
                if (iterator.hasNext())
                {
                    final Village village = (Village)iterator.next();
                    final int j = (int)village.getCenter().getDistanceSquared(villagedoorinfo.posX, villagedoorinfo.posY, villagedoorinfo.posZ);
                    final float k = 32.0f + village.getVillageRadius();

                    if (j > k * k)
                    {
                        continue;
                    }

                    village.addVillageDoorInfo(villagedoorinfo);
                    flag = true;
                }

                if (!flag)
                {
                    final Village village1 = new Village(this.worldObj);
                    village1.addVillageDoorInfo(villagedoorinfo);
                    this.villageList.add(village1);
                    this.markDirty();
                }

                ++i;
                break;
            }
        }

        this.newDoors.clear();
    }

    private void addUnassignedWoodenDoorsAroundToNewDoorsList(final ChunkCoordinates par1ChunkCoordinates)
    {
        final byte b0 = 16;
        final byte b1 = 4;
        final byte b2 = 16;

        for (int i = par1ChunkCoordinates.posX - b0; i < par1ChunkCoordinates.posX + b0; ++i)
        {
            for (int j = par1ChunkCoordinates.posY - b1; j < par1ChunkCoordinates.posY + b1; ++j)
            {
                for (int k = par1ChunkCoordinates.posZ - b2; k < par1ChunkCoordinates.posZ + b2; ++k)
                {
                    if (this.isWoodenDoorAt(i, j, k))
                    {
                        final VillageDoorInfo villagedoorinfo = this.getVillageDoorAt(i, j, k);

                        if (villagedoorinfo == null)
                        {
                            this.addDoorToNewListIfAppropriate(i, j, k);
                        }
                        else
                        {
                            villagedoorinfo.lastActivityTimestamp = this.tickCounter;
                        }
                    }
                }
            }
        }
    }

    private VillageDoorInfo getVillageDoorAt(final int par1, final int par2, final int par3)
    {
        Iterator iterator = this.newDoors.iterator();
        VillageDoorInfo villagedoorinfo;

        do
        {
            if (!iterator.hasNext())
            {
                iterator = this.villageList.iterator();
                VillageDoorInfo villagedoorinfo1;

                do
                {
                    if (!iterator.hasNext())
                    {
                        return null;
                    }

                    final Village village = (Village)iterator.next();
                    villagedoorinfo1 = village.getVillageDoorAt(par1, par2, par3);
                }
                while (villagedoorinfo1 == null);

                return villagedoorinfo1;
            }

            villagedoorinfo = (VillageDoorInfo)iterator.next();
        }
        while (villagedoorinfo.posX != par1 || villagedoorinfo.posZ != par3 || Math.abs(villagedoorinfo.posY - par2) > 1);

        return villagedoorinfo;
    }

    private void addDoorToNewListIfAppropriate(final int par1, final int par2, final int par3)
    {
        final int l = ((BlockDoor)Block.doorWood).getDoorOrientation(this.worldObj, par1, par2, par3);
        int i1;
        int j1;

        if (l != 0 && l != 2)
        {
            i1 = 0;

            for (j1 = -5; j1 < 0; ++j1)
            {
                if (this.worldObj.canBlockSeeTheSky(par1, par2, par3 + j1))
                {
                    --i1;
                }
            }

            for (j1 = 1; j1 <= 5; ++j1)
            {
                if (this.worldObj.canBlockSeeTheSky(par1, par2, par3 + j1))
                {
                    ++i1;
                }
            }

            if (i1 != 0)
            {
                this.newDoors.add(new VillageDoorInfo(par1, par2, par3, 0, i1 > 0 ? -2 : 2, this.tickCounter));
            }
        }
        else
        {
            i1 = 0;

            for (j1 = -5; j1 < 0; ++j1)
            {
                if (this.worldObj.canBlockSeeTheSky(par1 + j1, par2, par3))
                {
                    --i1;
                }
            }

            for (j1 = 1; j1 <= 5; ++j1)
            {
                if (this.worldObj.canBlockSeeTheSky(par1 + j1, par2, par3))
                {
                    ++i1;
                }
            }

            if (i1 != 0)
            {
                this.newDoors.add(new VillageDoorInfo(par1, par2, par3, i1 > 0 ? -2 : 2, 0, this.tickCounter));
            }
        }
    }

    private boolean isVillagerPositionPresent(final int par1, final int par2, final int par3)
    {
        final Iterator iterator = this.villagerPositionsList.iterator();
        ChunkCoordinates chunkcoordinates;

        do
        {
            if (!iterator.hasNext())
            {
                return false;
            }

            chunkcoordinates = (ChunkCoordinates)iterator.next();
        }
        while (chunkcoordinates.posX != par1 || chunkcoordinates.posY != par2 || chunkcoordinates.posZ != par3);

        return true;
    }

    private boolean isWoodenDoorAt(final int par1, final int par2, final int par3)
    {
        final int l = this.worldObj.getBlockId(par1, par2, par3);
        return l == Block.doorWood.blockID;
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        this.tickCounter = par1NBTTagCompound.getInteger("Tick");
        final NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Villages");

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            final Village village = new Village();
            village.readVillageDataFromNBT(nbttagcompound1);
            this.villageList.add(village);
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("Tick", this.tickCounter);
        final NBTTagList nbttaglist = new NBTTagList("Villages");
        final Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            final Village village = (Village)iterator.next();
            final NBTTagCompound nbttagcompound1 = new NBTTagCompound("Village");
            village.writeVillageDataToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
        }

        par1NBTTagCompound.setTag("Villages", nbttaglist);
    }
}
