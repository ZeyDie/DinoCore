package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import com.zeydie.settings.optimization.PermissionsSettings;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpCommand extends VanillaCommand {
    public OpCommand() {
        super("op");
        this.description = "Gives the specified player operator status";
        this.usageMessage = "/op <player>";
        this.setPermission("bukkit.command.op.give");
    }

    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length != 1 || args[0].isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        //TODO ZoomCodeStart
        if (PermissionsSettings.getInstance().getSettings().isOpsOnlyFromConsole() && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "Ops command only for console!");

            return false;
        }
        //TODO ZoomCodeEnd

        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        player.setOp(true);

        Command.broadcastCommandMessage(sender, "Opped " + args[0]);
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                return ImmutableList.of();
            }

            final String lastWord = args[0];
            if (lastWord.isEmpty()) {
                return ImmutableList.of();
            }

            final Player senderPlayer = (Player) sender;

            final ArrayList<String> matchedPlayers = new ArrayList<String>();
            for (final Player player : sender.getServer().getOnlinePlayers()) {
                final String name = player.getName();
                if (!senderPlayer.canSee(player) || player.isOp()) {
                    continue;
                }
                if (StringUtil.startsWithIgnoreCase(name, lastWord)) {
                    matchedPlayers.add(name);
                }
            }

            Collections.sort(matchedPlayers, String.CASE_INSENSITIVE_ORDER);
            return matchedPlayers;
        }
        return ImmutableList.of();
    }
}
