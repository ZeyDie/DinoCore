package org.bukkit.craftbukkit.v1_6_R3.util;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class StructureGrowDelegate implements BlockChangeDelegate {
    private final CraftWorld world;
    private final List<BlockState> blocks = new ArrayList<BlockState>();

    public StructureGrowDelegate(final net.minecraft.world.World world) {
        this.world = world.getWorld();
    }

    public boolean setRawTypeId(final int x, final int y, final int z, final int type) {
        return setRawTypeIdAndData(x, y, z, type, 0);
    }

    public boolean setRawTypeIdAndData(final int x, final int y, final int z, final int type, final int data) {
        final BlockState state = world.getBlockAt(x, y, z).getState();
        state.setTypeId(type);
        state.setData(new MaterialData(type, (byte) data));
        blocks.add(state);
        return true;
    }

    public boolean setTypeId(final int x, final int y, final int z, final int typeId) {
        return setRawTypeId(x, y, z, typeId);
    }

    public boolean setTypeIdAndData(final int x, final int y, final int z, final int typeId, final int data) {
        return setRawTypeIdAndData(x, y, z, typeId, data);
    }

    public int getTypeId(final int x, final int y, final int z) {
        return world.getBlockTypeIdAt(x, y, z);
    }

    public int getHeight() {
        return world.getMaxHeight();
    }

    public List<BlockState> getBlocks() {
        return blocks;
    }

    public boolean isEmpty(final int x, final int y, final int z) {
        return world.getBlockAt(x, y, z).isEmpty();
    }
}
