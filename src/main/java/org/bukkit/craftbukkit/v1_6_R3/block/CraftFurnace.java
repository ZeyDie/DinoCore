package org.bukkit.craftbukkit.v1_6_R3.block;

import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryFurnace;
import org.bukkit.inventory.FurnaceInventory;

public class CraftFurnace extends CraftBlockState implements Furnace {
    private final net.minecraft.tileentity.TileEntityFurnace furnace;

    public CraftFurnace(final Block block) {
        super(block);

        furnace = (net.minecraft.tileentity.TileEntityFurnace) ((CraftWorld) block.getWorld()).getTileEntityAt(getX(), getY(), getZ());
    }

    public FurnaceInventory getInventory() {
        return new CraftInventoryFurnace(furnace);
    }

    @Override
    public boolean update(final boolean force, final boolean applyPhysics) {
        final boolean result = super.update(force, applyPhysics);

        if (result) {
            furnace.onInventoryChanged();
        }

        return result;
    }

    public short getBurnTime() {
        return (short) furnace.furnaceBurnTime;
    }

    public void setBurnTime(final short burnTime) {
        furnace.furnaceBurnTime = burnTime;
    }

    public short getCookTime() {
        return (short) furnace.furnaceCookTime;
    }

    public void setCookTime(final short cookTime) {
        furnace.furnaceCookTime = cookTime;
    }
}
