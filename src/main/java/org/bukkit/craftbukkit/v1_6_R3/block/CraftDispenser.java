package org.bukkit.craftbukkit.v1_6_R3.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

public class CraftDispenser extends CraftBlockState implements Dispenser {
    private final CraftWorld world;
    private final net.minecraft.tileentity.TileEntityDispenser dispenser;

    public CraftDispenser(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        dispenser = (net.minecraft.tileentity.TileEntityDispenser) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Inventory getInventory() {
        return new CraftInventory(dispenser);
    }

    public boolean dispense() {
        final Block block = getBlock();

        if (block.getType() == Material.DISPENSER) {
            final net.minecraft.block.BlockDispenser dispense = (net.minecraft.block.BlockDispenser) net.minecraft.block.Block.dispenser;

            dispense.dispense(world.getHandle(), getX(), getY(), getZ());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean update(final boolean force, final boolean applyPhysics) {
        final boolean result = super.update(force, applyPhysics);

        if (result) {
            dispenser.onInventoryChanged();
        }

        return result;
    }
}
