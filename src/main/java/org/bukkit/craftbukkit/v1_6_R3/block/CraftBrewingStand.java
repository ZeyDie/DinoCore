package org.bukkit.craftbukkit.v1_6_R3.block;

import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryBrewer;
import org.bukkit.inventory.BrewerInventory;

public class CraftBrewingStand extends CraftBlockState implements BrewingStand {
    private final net.minecraft.tileentity.TileEntityBrewingStand brewingStand;

    public CraftBrewingStand(final Block block) {
        super(block);

        brewingStand = (net.minecraft.tileentity.TileEntityBrewingStand) ((CraftWorld) block.getWorld()).getTileEntityAt(getX(), getY(), getZ());
    }

    public BrewerInventory getInventory() {
        return new CraftInventoryBrewer(brewingStand);
    }

    @Override
    public boolean update(final boolean force, final boolean applyPhysics) {
        final boolean result = super.update(force, applyPhysics);

        if (result) {
            brewingStand.onInventoryChanged();
        }

        return result;
    }

    public int getBrewingTime() {
        return brewingStand.brewTime;
    }

    public void setBrewingTime(final int brewTime) {
        brewingStand.brewTime = brewTime;
    }
}
