package org.bukkit.command.defaults;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ReloadCommand extends BukkitCommand {
    public ReloadCommand(String name) {
        super(name);
        this.description = "Reloads the server configuration and plugins";
        this.usageMessage = "/reload";
        this.setPermission("bukkit.command.reload");
        this.setAliases(Arrays.asList("rl"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        // Cauldron - disable
        /*
        if (!testPermission(sender)) return true;

        org.spigotmc.CustomTimingsHandler.reload(); // Spigot: TODO: Why is this here?
        Bukkit.reload();
        Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Reload complete.");
        */
        sender.sendMessage(ChatColor.RED + "Reload not allowed on a Cauldron server.");
        // Cauldron end

        return true;
    }
}
