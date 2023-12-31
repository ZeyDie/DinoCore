package net.minecraftforge.event.terraingen;

import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.Event;

public class BiomeEvent extends Event
{
    public final BiomeGenBase biome;

    public BiomeEvent(final BiomeGenBase biome)
    {
        this.biome = biome;
    }
    
    public static class CreateDecorator extends BiomeEvent
    {
        public final BiomeDecorator originalBiomeDecorator;
        public BiomeDecorator newBiomeDecorator;
        
        public CreateDecorator(final BiomeGenBase biome, final BiomeDecorator original)
        {
            super(biome);
            originalBiomeDecorator = original;
            newBiomeDecorator = original;
        }
    }

    public static class BlockReplacement extends BiomeEvent
    {
        public final int original;
        public int replacement;

        public BlockReplacement(final BiomeGenBase biome, final int original, final int replacement)
        {
            super(biome);
            this.original = original;
            this.replacement = replacement;
        }
    }

    public static class BiomeColor extends BiomeEvent
    {
        public final int originalColor;
        public int newColor;
        
        public BiomeColor(final BiomeGenBase biome, final int original)
        {
            super(biome);
            originalColor = original;
            newColor = original;
        }
    }
    
    /**
     * This event is fired when the village generator attempts to choose a block ID
     * based on the village's biome.
     * 
     * You can set the result to DENY to prevent the default block ID selection.
     */
    @HasResult
    public static class GetVillageBlockID extends BlockReplacement
    {
        public GetVillageBlockID(final BiomeGenBase biome, final int original, final int replacement)
        {
            super(biome, original, replacement);
        }
    }
    
    /**
     * This event is fired when the village generator attempts to choose a block
     * metadata based on the village's biome.
     * 
     * You can set the result to DENY to prevent the default block metadata selection.
     */
    @HasResult
    public static class GetVillageBlockMeta extends BlockReplacement
    {
        public GetVillageBlockMeta(final BiomeGenBase biome, final int original, final int replacement)
        {
            super(biome, original, replacement);
        }
    }
    
    /**
     * This event is fired when a biome is queried for its grass color. 
     */
    public static class GetGrassColor extends BiomeColor
    {
        public GetGrassColor(final BiomeGenBase biome, final int original)
        {
            super(biome, original);
        }
    }
    
    /**
     * This event is fired when a biome is queried for its grass color. 
     */
    public static class GetFoliageColor extends BiomeColor
    {
        public GetFoliageColor(final BiomeGenBase biome, final int original)
        {
            super(biome, original);
        }
    }
    
    /**
     * This event is fired when a biome is queried for its water color. 
     */
    public static class GetWaterColor extends BiomeColor
    {
        public GetWaterColor(final BiomeGenBase biome, final int original)
        {
            super(biome, original);
        }
    }
}
