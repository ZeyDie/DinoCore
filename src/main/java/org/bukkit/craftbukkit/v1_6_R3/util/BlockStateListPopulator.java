package org.bukkit.craftbukkit.v1_6_R3.util;

import org.bukkit.World;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BlockStateListPopulator {
    private final World world;
    private final List<BlockState> list;

    public BlockStateListPopulator(final World world) {
        this(world, new ArrayList<BlockState>());
    }

    public BlockStateListPopulator(final World world, final List<BlockState> list) {
        this.world = world;
        this.list = list;
    }

    public void setTypeId(final int x, final int y, final int z, final int type) {
        final BlockState state = world.getBlockAt(x, y, z).getState();
        state.setTypeId(type);
        list.add(state);
    }

    public void updateList() {
        for (final BlockState state : list) {
            state.update(true);
        }
    }

    public List<BlockState> getList() {
        return list;
    }

    public World getWorld() {
        return world;
    }
}
