package org.bukkit.craftbukkit.v1_6_R3.command;

import org.bukkit.command.RemoteConsoleCommandSender;

public class CraftRemoteConsoleCommandSender extends ServerCommandSender implements RemoteConsoleCommandSender {
    public CraftRemoteConsoleCommandSender() {
        super();
    }

    public void sendMessage(final String message) {
        net.minecraft.network.rcon.RConConsoleSource.consoleBuffer.sendChatToPlayer(net.minecraft.util.ChatMessageComponent.createFromText(message + "\n")); // Send a newline after each message, to preserve formatting.
    }

    public void sendMessage(final String[] messages) {
        for (final String message : messages) {
            sendMessage(message);
        }
    }

    public String getName() {
        return "Rcon";
    }

    public boolean isOp() {
        return true;
    }

    public void setOp(final boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of remote controller.");
    }
}
