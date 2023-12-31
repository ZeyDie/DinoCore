package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SeedCommand extends VanillaCommand {
    public SeedCommand() {
        super("seed");
        this.description = "Shows the world seed";
        this.usageMessage = "/seed";
        this.setPermission("bukkit.command.seed");
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (!testPermission(sender)) return true;
        final long seed;
        if (sender instanceof Player) {
            seed = ((Player) sender).getWorld().getSeed();
        } else {
            seed = Bukkit.getWorlds().get(0).getSeed();
        }
        sender.sendMessage("Seed: " + seed);
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
