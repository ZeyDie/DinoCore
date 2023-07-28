package org.bukkit.craftbukkit.v1_6_R3.command;

import net.minecraft.server.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TicksPerSecondCommand extends Command {

    public TicksPerSecondCommand(final String name) {
        super(name);
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission("bukkit.command.tps");
    }

    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        if (!testPermission(sender)) return true;

        //TODO ZeyCodeReplace 20 on MinecraftServer.getTPS()
        final double tps = Math.min(MinecraftServer.getTPS(),  Math.round(net.minecraft.server.MinecraftServer.currentTPS * 10) / 10.0);
        final ChatColor color;
        if (tps > 19.2D) {
            color = ChatColor.GREEN;
        } else if (tps > 17.4D) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.RED;
        }

        sender.sendMessage(ChatColor.GOLD + "[TPS] " + color + tps);

        return true;
    }
}
