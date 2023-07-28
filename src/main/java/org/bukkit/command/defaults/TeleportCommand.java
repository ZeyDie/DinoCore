package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.List;

public class TeleportCommand extends VanillaCommand {

    public TeleportCommand() {
        super("tp");
        this.description = "Teleports the given player (or yourself) to another player or coordinates";
        this.usageMessage = "/tp [player] <target> and/or <x> <y> <z>";
        this.setPermission("bukkit.command.teleport");
    }

    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 1 || args.length > 4) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        final Player player;

        if (args.length == 1 || args.length == 3) {
            if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                sender.sendMessage("Please provide a player!");
                return true;
            }
        } else {
            player = Bukkit.getPlayerExact(args[0]);
        }

        if (player == null) {
            sender.sendMessage("Player not found: " + args[0]);
            return true;
        }

        if (args.length < 3) {
            final Player target = Bukkit.getPlayerExact(args[args.length - 1]);
            if (target == null) {
                sender.sendMessage("Can't find player " + args[args.length - 1] + ". No tp.");
                return true;
            }
            player.teleport(target, TeleportCause.COMMAND);
            Command.broadcastCommandMessage(sender, "Teleported " + player.getDisplayName() + " to " + target.getDisplayName());
        } else if (player.getWorld() != null) {
            final Location playerLocation = player.getLocation();
            final double x = getCoordinate(sender, playerLocation.getX(), args[args.length - 3]);
            final double y = getCoordinate(sender, playerLocation.getY(), args[args.length - 2], 0, 0);
            final double z = getCoordinate(sender, playerLocation.getZ(), args[args.length - 1]);

            if (x == MIN_COORD_MINUS_ONE || y == MIN_COORD_MINUS_ONE || z == MIN_COORD_MINUS_ONE) {
                sender.sendMessage("Please provide a valid location!");
                return true;
            }

            playerLocation.setX(x);
            playerLocation.setY(y);
            playerLocation.setZ(z);

            player.teleport(playerLocation);
            Command.broadcastCommandMessage(sender, String.format("Teleported %s to %.2f, %.2f, %.2f", player.getDisplayName(), x, y, z));
        }
        return true;
    }

    private double getCoordinate(final CommandSender sender, final double current, final String input) {
        return getCoordinate(sender, current, input, MIN_COORD, MAX_COORD);
    }

    private double getCoordinate(final CommandSender sender, final double current, String input, final int min, final int max) {
        String input1 = input;
        final boolean relative = input1.startsWith("~");
        double result = relative ? current : 0;

        if (!relative || input1.length() > 1) {
            final boolean exact = input1.contains(".");
            if (relative) input1 = input1.substring(1);

            final double testResult = getDouble(sender, input1);
            if (testResult == MIN_COORD_MINUS_ONE) {
                return MIN_COORD_MINUS_ONE;
            }
            result += testResult;

            if (!exact && !relative) result += 0.5f;
        }
        if (min != 0 || max != 0) {
            if (result < min) {
                result = MIN_COORD_MINUS_ONE;
            }

            if (result > max) {
                result = MIN_COORD_MINUS_ONE;
            }
        }

        return result;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1 || args.length == 2) {
            return super.tabComplete(sender, alias, args);
        }
        return ImmutableList.of();
    }
}
