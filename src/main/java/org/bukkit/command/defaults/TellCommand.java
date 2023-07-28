package org.bukkit.command.defaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TellCommand extends VanillaCommand {
    public TellCommand() {
        super("tell");
        this.description = "Sends a private message to the given player";
        this.usageMessage = "/tell <player> <message>";
        this.setPermission("bukkit.command.tell");
    }

    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 2)  {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        final Player player = Bukkit.getPlayerExact(args[0]);

        // If a player is hidden from the sender pretend they are offline
        if (player == null || (sender instanceof Player && !((Player) sender).canSee(player))) {
            sender.sendMessage("There's no player by that name online.");
        } else {
            final StringBuilder message = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                if (i > 1) message.append(" ");
                message.append(args[i]);
            }

            final String result = ChatColor.GRAY + sender.getName() + " whispers " + message;

            sender.sendMessage("[" + sender.getName() + "->" + player.getName() + "] " + message);
            player.sendMessage(result);
        }

        return true;
    }

    @Override
    public boolean matches(final String input) {
        return input.equalsIgnoreCase("tell") || input.equalsIgnoreCase("w") || input.equalsIgnoreCase("msg");
    }
}
