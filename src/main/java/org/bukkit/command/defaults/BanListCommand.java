package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BanListCommand extends VanillaCommand {
    private static final List<String> BANLIST_TYPES = ImmutableList.of("ips", "players");

    public BanListCommand() {
        super("banlist");
        this.description = "View all players banned from this server";
        this.usageMessage = "/banlist [ips|players]";
        this.setPermission("bukkit.command.ban.list");
    }

    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        if (!testPermission(sender)) return true;

        // TODO: ips support
        final StringBuilder message = new StringBuilder();
        final OfflinePlayer[] banlist = Bukkit.getServer().getBannedPlayers().toArray(new OfflinePlayer[0]);

        for (int x = 0; x < banlist.length; x++) {
            if (x != 0) {
                if (x == banlist.length - 1) {
                    message.append(" and ");
                } else {
                    message.append(", ");
                }
            }
            message.append(banlist[x].getName());
        }

        sender.sendMessage("There are " + banlist.length + " total banned players:");
        sender.sendMessage(message.toString());
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], BANLIST_TYPES, new ArrayList<String>(BANLIST_TYPES.size()));
        }
        return ImmutableList.of();
    }
}
