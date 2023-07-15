package org.bukkit.craftbukkit.v1_6_R3.block;

import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

public class CraftHopper extends CraftBlockState implements Hopper {
    private final net.minecraft.tileentity.TileEntityHopper hopper;

    public CraftHopper(final Block block) {
        super(block);

        hopper = (net.minecraft.tileentity.TileEntityHopper) ((CraftWorld) block.getWorld()).getTileEntityAt(getX(), getY(), getZ());
    }

    public Inventory getInventory() {
        return new CraftInventory(hopper);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (result) {
            hopper.onInventoryChanged();
        }

        return result;
    }
}
