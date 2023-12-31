package net.minecraft.world.gen.structure;

import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.biome.SpawnListEntry;

import java.util.ArrayList;
import java.util.List;

public class MapGenNetherBridge extends MapGenStructure
{
    private List spawnList = new ArrayList();

    public MapGenNetherBridge()
    {
        this.spawnList.add(new SpawnListEntry(EntityBlaze.class, 10, 2, 3));
        this.spawnList.add(new SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
        this.spawnList.add(new SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
        this.spawnList.add(new SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
    }

    public String func_143025_a()
    {
        return "Fortress";
    }

    public List getSpawnList()
    {
        return this.spawnList;
    }

    protected boolean canSpawnStructureAtCoords(final int par1, final int par2)
    {
        final int k = par1 >> 4;
        final int l = par2 >> 4;
        this.rand.setSeed((long)(k ^ l << 4) ^ this.worldObj.getSeed());
        this.rand.nextInt();
        return this.rand.nextInt(3) != 0 ? false : (par1 != (k << 4) + 4 + this.rand.nextInt(8) ? false : par2 == (l << 4) + 4 + this.rand.nextInt(8));
    }

    protected StructureStart getStructureStart(final int par1, final int par2)
    {
        return new StructureNetherBridgeStart(this.worldObj, this.rand, par1, par2);
    }
}
