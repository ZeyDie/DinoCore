package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StopCommand extends VanillaCommand {
    public StopCommand() {
        super("stop");
        this.description = "Stops the server with optional reason";
        this.usageMessage = "/stop [reason]";
        this.setPermission("bukkit.command.stop");
    }

    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        if (!testPermission(sender)) return true;

        Command.broadcastCommandMessage(sender, "Stopping the server..");
        Bukkit.shutdown();

        final String reason = this.createString(args, 0);
        if (StringUtils.isNotEmpty(reason)) {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(reason);
            }
        }

        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        return ImmutableList.of();
    }
}
