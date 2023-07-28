package net.minecraft.world.gen.feature;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.gen.structure.*;

import java.util.*;
import java.util.Map.Entry;

public class MapGenScatteredFeature extends MapGenStructure
{
    private static List biomelist = Arrays.asList(new BiomeGenBase[] {BiomeGenBase.desert, BiomeGenBase.desertHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.swampland});

    /** contains possible spawns for scattered features */
    private List scatteredFeatureSpawnList;

    /** the maximum distance between scattered features */
    private int maxDistanceBetweenScatteredFeatures;

    /** the minimum distance between scattered features */
    private int minDistanceBetweenScatteredFeatures;

    public MapGenScatteredFeature()
    {
        this.scatteredFeatureSpawnList = new ArrayList();
        this.maxDistanceBetweenScatteredFeatures = 32;
        this.minDistanceBetweenScatteredFeatures = 8;
        this.scatteredFeatureSpawnList.add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
    }

    public MapGenScatteredFeature(final Map par1Map)
    {
        this();
        final Iterator iterator = par1Map.entrySet().iterator();

        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();

            if (((String)entry.getKey()).equals("distance"))
            {
                this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1);
            }
        }
    }

    public String func_143025_a()
    {
        return "Temple";
    }

    protected boolean canSpawnStructureAtCoords(int par1, int par2)
    {
        int par11 = par1;
        int par21 = par2;
        final int k = par11;
        final int l = par21;

        if (par11 < 0)
        {
            par11 -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        if (par21 < 0)
        {
            par21 -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int i1 = par11 / this.maxDistanceBetweenScatteredFeatures;
        int j1 = par21 / this.maxDistanceBetweenScatteredFeatures;
        final Random random = this.worldObj.setRandomSeed(i1, j1, 14357617);
        i1 *= this.maxDistanceBetweenScatteredFeatures;
        j1 *= this.maxDistanceBetweenScatteredFeatures;
        i1 += random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
        j1 += random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);

        if (k == i1 && l == j1)
        {
            final BiomeGenBase biomegenbase = this.worldObj.getWorldChunkManager().getBiomeGenAt(k * 16 + 8, l * 16 + 8);
            final Iterator iterator = biomelist.iterator();

            while (iterator.hasNext())
            {
                final BiomeGenBase biomegenbase1 = (BiomeGenBase)iterator.next();

                if (biomegenbase == biomegenbase1)
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(final int par1, final int par2)
    {
        return new StructureScatteredFeatureStart(this.worldObj, this.rand, par1, par2);
    }

    public boolean func_143030_a(final int par1, final int par2, final int par3)
    {
        final StructureStart structurestart = this.func_143028_c(par1, par2, par3);

        if (structurestart != null && structurestart instanceof StructureScatteredFeatureStart && !structurestart.components.isEmpty())
        {
            final StructureComponent structurecomponent = (StructureComponent)structurestart.components.getFirst();
            return structurecomponent instanceof ComponentScatteredFeatureSwampHut;
        }
        else
        {
            return false;
        }
    }

    /**
     * returns possible spawns for scattered features
     */
    public List getScatteredFeatureSpawnList()
    {
        return this.scatteredFeatureSpawnList;
    }
}
