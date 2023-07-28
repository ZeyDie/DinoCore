package org.bukkit.craftbukkit.v1_6_R3.block;


import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;

public class CraftCreatureSpawner extends CraftBlockState implements CreatureSpawner {
    private final net.minecraft.tileentity.TileEntityMobSpawner spawner;

    public CraftCreatureSpawner(final Block block) {
        super(block);

        spawner = (net.minecraft.tileentity.TileEntityMobSpawner) ((CraftWorld) block.getWorld()).getTileEntityAt(getX(), getY(), getZ());
    }

    @Deprecated
    public CreatureType getCreatureType() {
        return CreatureType.fromName(spawner.getSpawnerLogic().getEntityNameToSpawn());
    }

    public EntityType getSpawnedType() {
        return EntityType.fromName(spawner.getSpawnerLogic().getEntityNameToSpawn());
    }

    @Deprecated
    public void setCreatureType(final CreatureType creatureType) {
        spawner.getSpawnerLogic().setMobID(creatureType.getName());
    }

    public void setSpawnedType(final EntityType entityType) {
        if (entityType == null || entityType.getName() == null) {
            throw new IllegalArgumentException("Can't spawn EntityType " + entityType + " from mobspawners!");
        }

        spawner.getSpawnerLogic().setMobID(entityType.getName());
    }

    @Deprecated
    public String getCreatureTypeId() {
        return spawner.getSpawnerLogic().getEntityNameToSpawn();
    }

    @Deprecated
    public void setCreatureTypeId(final String creatureName) {
        setCreatureTypeByName(creatureName);
    }

    public String getCreatureTypeName() {
        return spawner.getSpawnerLogic().getEntityNameToSpawn();
    }

    public void setCreatureTypeByName(final String creatureType) {
        // Verify input
        final EntityType type = EntityType.fromName(creatureType);
        if (type == null) {
            return;
        }
        setSpawnedType(type);
    }

    public int getDelay() {
        return spawner.getSpawnerLogic().spawnDelay;
    }

    public void setDelay(final int delay) {
        spawner.getSpawnerLogic().spawnDelay = delay;
    }

}
