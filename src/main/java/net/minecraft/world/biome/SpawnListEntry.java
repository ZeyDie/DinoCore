package net.minecraft.world.biome;

import net.minecraft.util.WeightedRandomItem;

public class SpawnListEntry extends WeightedRandomItem
{
    /** Holds the class of the entity to be spawned. */
    public Class entityClass;
    public int minGroupCount;
    public int maxGroupCount;

    public SpawnListEntry(final Class par1Class, final int par2, final int par3, final int par4)
    {
        super(par2);
        this.entityClass = par1Class;
        this.minGroupCount = par3;
        this.maxGroupCount = par4;
    }

    public String toString()
    {
        return this.entityClass.getSimpleName() + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
    }
}
