package net.minecraftforge.event.terraingen;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

import java.util.Random;

public class DeferredBiomeDecorator extends BiomeDecorator {
    private BiomeDecorator wrapped;

    public DeferredBiomeDecorator(final BiomeGenBase biomeGenBase, final BiomeDecorator wrappedOriginal)
    {
        super(biomeGenBase);
        this.wrapped = wrappedOriginal;
    }

    @Override
    public void decorate(final World par1World, final Random par2Random, final int par3, final int par4)
    {
        fireCreateEventAndReplace();
        // On first call to decorate, we fire and substitute ourselves, if we haven't already done so
        biome.theBiomeDecorator.decorate(par1World, par2Random, par3, par4);
    }
    public void fireCreateEventAndReplace()
    {
        // Copy any configuration from us to the real instance.
        wrapped.bigMushroomsPerChunk = bigMushroomsPerChunk;
        wrapped.cactiPerChunk = cactiPerChunk;
        wrapped.clayPerChunk = clayPerChunk;
        wrapped.deadBushPerChunk = deadBushPerChunk;
        wrapped.flowersPerChunk = flowersPerChunk;
        wrapped.generateLakes = generateLakes;
        wrapped.grassPerChunk = grassPerChunk;
        wrapped.mushroomsPerChunk = mushroomsPerChunk;
        wrapped.reedsPerChunk = reedsPerChunk;
        wrapped.sandPerChunk = sandPerChunk;
        wrapped.sandPerChunk2 = sandPerChunk2;
        wrapped.treesPerChunk = treesPerChunk;
        wrapped.waterlilyPerChunk = waterlilyPerChunk;
        
        final BiomeEvent.CreateDecorator event = new BiomeEvent.CreateDecorator(biome, wrapped);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        biome.theBiomeDecorator = event.newBiomeDecorator;
    }
}
