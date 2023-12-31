package org.bukkit.craftbukkit.v1_6_R3.block;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

public class CraftDropper extends CraftBlockState implements Dropper {
    private final CraftWorld world;
    private final net.minecraft.tileentity.TileEntityDropper dropper;

    public CraftDropper(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        dropper = (net.minecraft.tileentity.TileEntityDropper) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Inventory getInventory() {
        return new CraftInventory(dropper);
    }

    public void drop() {
        final Block block = getBlock();

        if (block.getType() == Material.DROPPER) {
            final net.minecraft.block.BlockDropper drop = (net.minecraft.block.BlockDropper) net.minecraft.block.Block.dropper;

            drop.dispense(world.getHandle(), getX(), getY(), getZ());
        }
    }

    @Override
    public boolean update(final boolean force, final boolean applyPhysics) {
        final boolean result = super.update(force, applyPhysics);

        if (result) {
            dropper.onInventoryChanged();
        }

        return result;
    }
}
