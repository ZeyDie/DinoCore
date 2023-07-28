package net.minecraft.world.gen.structure;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Random;

public class StructureScatteredFeatureStart extends StructureStart
{
    public StructureScatteredFeatureStart() {}

    public StructureScatteredFeatureStart(final World par1World, final Random par2Random, final int par3, final int par4)
    {
        super(par3, par4);
        final BiomeGenBase biomegenbase = par1World.getBiomeGenForCoords(par3 * 16 + 8, par4 * 16 + 8);

        if (biomegenbase != BiomeGenBase.jungle && biomegenbase != BiomeGenBase.jungleHills)
        {
            if (biomegenbase == BiomeGenBase.swampland)
            {
                final ComponentScatteredFeatureSwampHut componentscatteredfeatureswamphut = new ComponentScatteredFeatureSwampHut(par2Random, par3 * 16, par4 * 16);
                this.components.add(componentscatteredfeatureswamphut);
            }
            else
            {
                final ComponentScatteredFeatureDesertPyramid componentscatteredfeaturedesertpyramid = new ComponentScatteredFeatureDesertPyramid(par2Random, par3 * 16, par4 * 16);
                this.components.add(componentscatteredfeaturedesertpyramid);
            }
        }
        else
        {
            final ComponentScatteredFeatureJunglePyramid componentscatteredfeaturejunglepyramid = new ComponentScatteredFeatureJunglePyramid(par2Random, par3 * 16, par4 * 16);
            this.components.add(componentscatteredfeaturejunglepyramid);
        }

        this.updateBoundingBox();
    }
}
