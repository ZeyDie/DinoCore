package net.minecraftforge.event.terraingen;

import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Random;

public class InitNoiseGensEvent extends WorldEvent
{
    public final Random rand;
    public final NoiseGeneratorOctaves[] originalNoiseGens;
    public NoiseGeneratorOctaves[] newNoiseGens;
    
    public InitNoiseGensEvent(final World world, final Random rand, final NoiseGeneratorOctaves[] original)
    {
        super(world);
        this.rand = rand;
        originalNoiseGens = original;
        newNoiseGens = original.clone();
    }
}