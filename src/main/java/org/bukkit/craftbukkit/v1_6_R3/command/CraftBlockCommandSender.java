package org.bukkit.craftbukkit.v1_6_R3.command;

import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;

/**
 * Represents input from a command block
 */
public class CraftBlockCommandSender extends ServerCommandSender implements BlockCommandSender {
    private final net.minecraft.tileentity.TileEntityCommandBlock commandBlock;

    public CraftBlockCommandSender(final net.minecraft.tileentity.TileEntityCommandBlock commandBlock) {
        super();
        this.commandBlock = commandBlock;
    }

    public Block getBlock() {
        return commandBlock.getWorldObj().getWorld().getBlockAt(commandBlock.xCoord, commandBlock.yCoord, commandBlock.zCoord);
    }

    public void sendMessage(final String message) {
    }

    public void sendMessage(final String[] messages) {
    }

    public String getName() {
        return commandBlock.getCommandSenderName();
    }

    public boolean isOp() {
        return true;
    }

    public void setOp(final boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of a block");
    }
}
