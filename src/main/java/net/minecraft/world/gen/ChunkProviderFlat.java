package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.MapGenScatteredFeature;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;

import java.util.*;

public class ChunkProviderFlat implements IChunkProvider
{
    private World worldObj;
    private Random random;
    private final byte[] cachedBlockIDs = new byte[256];
    private final byte[] cachedBlockMetadata = new byte[256];
    private final FlatGeneratorInfo flatWorldGenInfo;
    private final List structureGenerators = new ArrayList();
    private final boolean hasDecoration;
    private final boolean hasDungeons;
    private WorldGenLakes waterLakeGenerator;
    private WorldGenLakes lavaLakeGenerator;

    public ChunkProviderFlat(final World par1World, final long par2, final boolean par4, final String par5Str)
    {
        this.worldObj = par1World;
        this.random = new Random(par2);
        this.flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(par5Str);

        if (par4)
        {
            final Map map = this.flatWorldGenInfo.getWorldFeatures();

            if (map.containsKey("village"))
            {
                final Map map1 = (Map)map.get("village");

                if (!map1.containsKey("size"))
                {
                    map1.put("size", "1");
                }

                this.structureGenerators.add(new MapGenVillage(map1));
            }

            if (map.containsKey("biome_1"))
            {
                this.structureGenerators.add(new MapGenScatteredFeature((Map)map.get("biome_1")));
            }

            if (map.containsKey("mineshaft"))
            {
                this.structureGenerators.add(new MapGenMineshaft((Map)map.get("mineshaft")));
            }

            if (map.containsKey("stronghold"))
            {
                this.structureGenerators.add(new MapGenStronghold((Map)map.get("stronghold")));
            }
        }

        this.hasDecoration = this.flatWorldGenInfo.getWorldFeatures().containsKey("decoration");

        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lake"))
        {
            this.waterLakeGenerator = new WorldGenLakes(Block.waterStill.blockID);
        }

        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake"))
        {
            this.lavaLakeGenerator = new WorldGenLakes(Block.lavaStill.blockID);
        }

        this.hasDungeons = this.flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");
        final Iterator iterator = this.flatWorldGenInfo.getFlatLayers().iterator();

        while (iterator.hasNext())
        {
            final FlatLayerInfo flatlayerinfo = (FlatLayerInfo)iterator.next();

            for (int j = flatlayerinfo.getMinY(); j < flatlayerinfo.getMinY() + flatlayerinfo.getLayerCount(); ++j)
            {
                this.cachedBlockIDs[j] = (byte)(flatlayerinfo.getFillBlock() & 255);
                this.cachedBlockMetadata[j] = (byte)flatlayerinfo.getFillBlockMeta();
            }
        }
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(final int par1, final int par2)
    {
        return this.provideChunk(par1, par2);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(final int par1, final int par2)
    {
        final Chunk chunk = new Chunk(this.worldObj, par1, par2);

        for (int k = 0; k < this.cachedBlockIDs.length; ++k)
        {
            final int l = k >> 4;
            ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[l];

            if (extendedblockstorage == null)
            {
                extendedblockstorage = new ExtendedBlockStorage(k, !this.worldObj.provider.hasNoSky);
                chunk.getBlockStorageArray()[l] = extendedblockstorage;
            }

            for (int i1 = 0; i1 < 16; ++i1)
            {
                for (int j1 = 0; j1 < 16; ++j1)
                {
                    extendedblockstorage.setExtBlockID(i1, k & 15, j1, this.cachedBlockIDs[k] & 255);
                    extendedblockstorage.setExtBlockMetadata(i1, k & 15, j1, this.cachedBlockMetadata[k]);
                }
            }
        }

        chunk.generateSkylightMap();
        final BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, par1 * 16, par2 * 16, 16, 16);
        final byte[] abyte = chunk.getBiomeArray();

        for (int k1 = 0; k1 < abyte.length; ++k1)
        {
            abyte[k1] = (byte)abiomegenbase[k1].biomeID;
        }

        final Iterator iterator = this.structureGenerators.iterator();

        while (iterator.hasNext())
        {
            final MapGenStructure mapgenstructure = (MapGenStructure)iterator.next();
            mapgenstructure.generate(this, this.worldObj, par1, par2, (byte[])null);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(final int par1, final int par2)
    {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(final IChunkProvider par1IChunkProvider, final int par2, final int par3)
    {
        final int k = par2 * 16;
        final int l = par3 * 16;
        final BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(k + 16, l + 16);
        boolean flag = false;
        this.random.setSeed(this.worldObj.getSeed());
        final long i1 = this.random.nextLong() / 2L * 2L + 1L;
        final long j1 = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long)par2 * i1 + (long)par3 * j1 ^ this.worldObj.getSeed());
        final Iterator iterator = this.structureGenerators.iterator();

        while (iterator.hasNext())
        {
            final MapGenStructure mapgenstructure = (MapGenStructure)iterator.next();
            final boolean flag1 = mapgenstructure.generateStructuresInChunk(this.worldObj, this.random, par2, par3);

            if (mapgenstructure instanceof MapGenVillage)
            {
                flag |= flag1;
            }
        }

        int k1;
        int l1;
        int i2;

        if (this.waterLakeGenerator != null && !flag && this.random.nextInt(4) == 0)
        {
            l1 = k + this.random.nextInt(16) + 8;
            k1 = this.random.nextInt(128);
            i2 = l + this.random.nextInt(16) + 8;
            this.waterLakeGenerator.generate(this.worldObj, this.random, l1, k1, i2);
        }

        if (this.lavaLakeGenerator != null && !flag && this.random.nextInt(8) == 0)
        {
            l1 = k + this.random.nextInt(16) + 8;
            k1 = this.random.nextInt(this.random.nextInt(120) + 8);
            i2 = l + this.random.nextInt(16) + 8;

            if (k1 < 63 || this.random.nextInt(10) == 0)
            {
                this.lavaLakeGenerator.generate(this.worldObj, this.random, l1, k1, i2);
            }
        }

        if (this.hasDungeons)
        {
            for (l1 = 0; l1 < 8; ++l1)
            {
                k1 = k + this.random.nextInt(16) + 8;
                i2 = this.random.nextInt(128);
                final int j2 = l + this.random.nextInt(16) + 8;
                (new WorldGenDungeons()).generate(this.worldObj, this.random, k1, i2, j2);
            }
        }

        if (this.hasDecoration)
        {
            biomegenbase.decorate(this.worldObj, this.random, k, l);
        }
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(final boolean par1, final IProgressUpdate par2IProgressUpdate)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData() {}

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "FlatLevelSource";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures(final EnumCreatureType par1EnumCreatureType, final int par2, final int par3, final int par4)
    {
        final BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(par2, par4);
        return biomegenbase == null ? null : biomegenbase.getSpawnableList(par1EnumCreatureType);
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition findClosestStructure(final World par1World, final String par2Str, final int par3, final int par4, final int par5)
    {
        if ("Stronghold".equals(par2Str))
        {
            final Iterator iterator = this.structureGenerators.iterator();

            while (iterator.hasNext())
            {
                final MapGenStructure mapgenstructure = (MapGenStructure)iterator.next();

                if (mapgenstructure instanceof MapGenStronghold)
                {
                    return mapgenstructure.getNearestInstance(par1World, par3, par4, par5);
                }
            }
        }

        return null;
    }

    public int getLoadedChunkCount()
    {
        return 0;
    }

    public void recreateStructures(final int par1, final int par2)
    {
        final Iterator iterator = this.structureGenerators.iterator();

        while (iterator.hasNext())
        {
            final MapGenStructure mapgenstructure = (MapGenStructure)iterator.next();
            mapgenstructure.generate(this, this.worldObj, par1, par2, (byte[])null);
        }
    }
}
