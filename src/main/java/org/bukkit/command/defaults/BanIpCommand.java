package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class BanIpCommand extends VanillaCommand {
    public static final Pattern ipValidity = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public BanIpCommand() {
        super("ban-ip");
        this.description = "Prevents the specified IP address from using this server";
        this.usageMessage = "/ban-ip <address|player> [reason ...]";
        this.setPermission("bukkit.command.ban.ip");
    }

    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 1)  {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        // TODO: Ban Reason support
        if (ipValidity.matcher(args[0]).matches()) {
            processIPBan(args[0], sender);
        } else {
            final Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                return false;
            }

            processIPBan(player.getAddress().getAddress().getHostAddress(), sender);
        }

        return true;
    }

    private void processIPBan(final String ip, final CommandSender sender) {
        // TODO: Kick on ban
        Bukkit.banIP(ip);

        Command.broadcastCommandMessage(sender, "Banned IP Address " + ip);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return super.tabComplete(sender, alias, args);
        }
        return ImmutableList.of();
    }
}
