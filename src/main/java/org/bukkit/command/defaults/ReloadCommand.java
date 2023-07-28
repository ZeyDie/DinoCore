package org.bukkit.command.defaults;

import com.zeydie.settings.optimization.CoreSettings;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;

public class ReloadCommand extends BukkitCommand {
    public ReloadCommand(final String name) {
        super(name);
        this.description = "Reloads the server configuration and plugins";
        this.usageMessage = "/reload";
        this.setPermission("bukkit.command.reload");
        this.setAliases(Collections.singletonList("rl"));
    }

    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        //TODO ZeyCodeStart
        if (CoreSettings.getInstance().getSettings().isReloadCommand()) {
            if (!testPermission(sender)) return true;

            org.spigotmc.CustomTimingsHandler.reload(); // Spigot: TODO: Why is this here?
            Bukkit.reload();
            MinecraftServer.getServer().init();
            Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Reload complete.");
        } else
            //TODO ZeyCodeEnd

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
