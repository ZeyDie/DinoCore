package net.minecraftforge.cauldron.block;

import net.minecraft.inventory.IInventory;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CraftCustomContainer extends CraftBlockState implements InventoryHolder {
    private final CraftWorld world;
    private final net.minecraft.inventory.IInventory container;

    public CraftCustomContainer(final Block block) {
        super(block);
        world = (CraftWorld) block.getWorld();
        container = (IInventory)world.getTileEntityAt(getX(), getY(), getZ());
    }

    @Override
    public Inventory getInventory() {
        final CraftInventory inventory = new CraftInventory(container);
        return inventory;
    }
}
