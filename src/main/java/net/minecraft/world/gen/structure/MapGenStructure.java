package net.minecraft.world.gen.structure;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

import java.util.*;

public abstract class MapGenStructure extends MapGenBase
{
    private MapGenStructureData field_143029_e;

    /**
     * Used to store a list of all structures that have been recursively generated. Used so that during recursive
     * generation, the structure generator can avoid generating structures that intersect ones that have already been
     * placed.
     */
    protected Map structureMap = new HashMap();

    public abstract String func_143025_a();

    /**
     * Recursively called by generate() (generate) and optionally by itself.
     */
    protected final void recursiveGenerate(final World par1World, final int par2, final int par3, final int par4, final int par5, final byte[] par6ArrayOfByte)
    {
        this.func_143027_a(par1World);

        if (!this.structureMap.containsKey(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par2, par3))))
        {
            this.rand.nextInt();

            try
            {
                if (this.canSpawnStructureAtCoords(par2, par3))
                {
                    final StructureStart structurestart = this.getStructureStart(par2, par3);
                    this.structureMap.put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par2, par3)), structurestart);
                    this.func_143026_a(par2, par3, structurestart);
                }
            }
            catch (final Throwable throwable)
            {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception preparing structure feature");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Feature being prepared");
                crashreportcategory.addCrashSectionCallable("Is feature chunk", new CallableIsFeatureChunk(this, par2, par3));
                crashreportcategory.addCrashSection("Chunk location", String.format("%d,%d", new Object[] {Integer.valueOf(par2), Integer.valueOf(par3)}));
                crashreportcategory.addCrashSectionCallable("Chunk pos hash", new CallableChunkPosHash(this, par2, par3));
                crashreportcategory.addCrashSectionCallable("Structure type", new CallableStructureType(this));
                throw new ReportedException(crashreport);
            }
        }
    }

    /**
     * Generates structures in specified chunk next to existing structures. Does *not* generate StructureStarts.
     */
    public boolean generateStructuresInChunk(final World par1World, final Random par2Random, final int par3, final int par4)
    {
        this.func_143027_a(par1World);
        final int k = (par3 << 4) + 8;
        final int l = (par4 << 4) + 8;
        boolean flag = false;
        final Iterator iterator = this.structureMap.values().iterator();

        while (iterator.hasNext())
        {
            final StructureStart structurestart = (StructureStart)iterator.next();

            if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().intersectsWith(k, l, k + 15, l + 15))
            {
                structurestart.generateStructure(par1World, par2Random, new StructureBoundingBox(k, l, k + 15, l + 15));
                flag = true;
                this.func_143026_a(structurestart.func_143019_e(), structurestart.func_143018_f(), structurestart);
            }
        }

        return flag;
    }

    /**
     * Returns true if the structure generator has generated a structure located at the given position tuple.
     */
    public boolean hasStructureAt(final int par1, final int par2, final int par3)
    {
        this.func_143027_a(this.worldObj);
        return this.func_143028_c(par1, par2, par3) != null;
    }

    protected StructureStart func_143028_c(final int par1, final int par2, final int par3)
    {
        final Iterator iterator = this.structureMap.values().iterator();

        while (iterator.hasNext())
        {
            final StructureStart structurestart = (StructureStart)iterator.next();

            if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().intersectsWith(par1, par3, par1, par3))
            {
                final Iterator iterator1 = structurestart.getComponents().iterator();

                while (iterator1.hasNext())
                {
                    final StructureComponent structurecomponent = (StructureComponent)iterator1.next();

                    if (structurecomponent.getBoundingBox().isVecInside(par1, par2, par3))
                    {
                        return structurestart;
                    }
                }
            }
        }

        return null;
    }

    public boolean func_142038_b(final int par1, final int par2, final int par3)
    {
        this.func_143027_a(this.worldObj);
        final Iterator iterator = this.structureMap.values().iterator();
        StructureStart structurestart;

        do
        {
            if (!iterator.hasNext())
            {
                return false;
            }

            structurestart = (StructureStart)iterator.next();
        }
        while (!structurestart.isSizeableStructure());

        return structurestart.getBoundingBox().intersectsWith(par1, par3, par1, par3);
    }

    public ChunkPosition getNearestInstance(final World par1World, final int par2, final int par3, final int par4)
    {
        this.worldObj = par1World;
        this.func_143027_a(par1World);
        this.rand.setSeed(par1World.getSeed());
        final long l = this.rand.nextLong();
        final long i1 = this.rand.nextLong();
        final long j1 = (long)(par2 >> 4) * l;
        final long k1 = (long)(par4 >> 4) * i1;
        this.rand.setSeed(j1 ^ k1 ^ par1World.getSeed());
        this.recursiveGenerate(par1World, par2 >> 4, par4 >> 4, 0, 0, (byte[])null);
        double d0 = Double.MAX_VALUE;
        ChunkPosition chunkposition = null;
        final Iterator iterator = this.structureMap.values().iterator();
        ChunkPosition chunkposition1;
        int l1;
        int i2;
        double d1;
        int j2;

        while (iterator.hasNext())
        {
            final StructureStart structurestart = (StructureStart)iterator.next();

            if (structurestart.isSizeableStructure())
            {
                final StructureComponent structurecomponent = (StructureComponent)structurestart.getComponents().get(0);
                chunkposition1 = structurecomponent.getCenter();
                i2 = chunkposition1.x - par2;
                l1 = chunkposition1.y - par3;
                j2 = chunkposition1.z - par4;
                d1 = (double)(i2 * i2 + l1 * l1 + j2 * j2);

                if (d1 < d0)
                {
                    d0 = d1;
                    chunkposition = chunkposition1;
                }
            }
        }

        if (chunkposition != null)
        {
            return chunkposition;
        }
        else
        {
            final List list = this.getCoordList();

            if (list != null)
            {
                ChunkPosition chunkposition2 = null;
                final Iterator iterator1 = list.iterator();

                while (iterator1.hasNext())
                {
                    chunkposition1 = (ChunkPosition)iterator1.next();
                    i2 = chunkposition1.x - par2;
                    l1 = chunkposition1.y - par3;
                    j2 = chunkposition1.z - par4;
                    d1 = (double)(i2 * i2 + l1 * l1 + j2 * j2);

                    if (d1 < d0)
                    {
                        d0 = d1;
                        chunkposition2 = chunkposition1;
                    }
                }

                return chunkposition2;
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Returns a list of other locations at which the structure generation has been run, or null if not relevant to this
     * structure generator.
     */
    protected List getCoordList()
    {
        return null;
    }

    private void func_143027_a(final World par1World)
    {
        if (this.field_143029_e == null)
        {
            // Spigot Start
            if (par1World.spigotConfig.saveMineshaftStructureInfo)
            {
                this.field_143029_e = (MapGenStructureData) par1World.perWorldStorage.loadData(MapGenStructureData.class, this.func_143025_a());
            }
            else
            {
                // Cauldron start - only ignore Mineshaft structure info
                if (!(this instanceof MapGenMineshaft))
                {
                    this.field_143029_e = (MapGenStructureData) par1World.perWorldStorage.loadData(MapGenStructureData.class, this.func_143025_a());
                }
                else
                {
                    this.field_143029_e = new MapGenStructureData(this.func_143025_a());
                }
                // Cauldron end
            }
            // Spigot End

            if (this.field_143029_e == null)
            {
                this.field_143029_e = new MapGenStructureData(this.func_143025_a());
                par1World.perWorldStorage.setData(this.func_143025_a(), this.field_143029_e);
            }
            else
            {
                final NBTTagCompound nbttagcompound = this.field_143029_e.func_143041_a();
                final Iterator iterator = nbttagcompound.getTags().iterator();

                while (iterator.hasNext())
                {
                    final NBTBase nbtbase = (NBTBase)iterator.next();

                    if (nbtbase.getId() == 10)
                    {
                        final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbtbase;

                        if (nbttagcompound1.hasKey("ChunkX") && nbttagcompound1.hasKey("ChunkZ"))
                        {
                            final int i = nbttagcompound1.getInteger("ChunkX");
                            final int j = nbttagcompound1.getInteger("ChunkZ");
                            final StructureStart structurestart = MapGenStructureIO.func_143035_a(nbttagcompound1, par1World);
                            this.structureMap.put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(i, j)), structurestart);
                        }
                    }
                }
            }
        }
    }

    private void func_143026_a(final int par1, final int par2, final StructureStart par3StructureStart)
    {
        this.field_143029_e.func_143043_a(par3StructureStart.func_143021_a(par1, par2), par1, par2);
        this.field_143029_e.markDirty();
    }

    protected abstract boolean canSpawnStructureAtCoords(int i, int j);

    protected abstract StructureStart getStructureStart(int i, int j);
}
