package org.bukkit.craftbukkit.v1_6_R3.block;

import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;

public class CraftCommandBlock extends CraftBlockState implements CommandBlock {
    private final net.minecraft.tileentity.TileEntityCommandBlock commandBlock;
    private String command;
    private String name;

    public CraftCommandBlock(final Block block) {
        super(block);

        final CraftWorld world = (CraftWorld) block.getWorld();
        commandBlock = (net.minecraft.tileentity.TileEntityCommandBlock) world.getTileEntityAt(getX(), getY(), getZ());
        command = commandBlock.command;
        name = commandBlock.getCommandSenderName();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command != null ? command : "";
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name != null ? name : "@";
    }

    public boolean update(final boolean force, final boolean applyPhysics) {
        final boolean result = super.update(force, applyPhysics);

        if (result) {
            commandBlock.setCommand(command);
            commandBlock.setCommandSenderName(name);
        }

        return result;
    }
}
