package org.bukkit.command.defaults;

import org.bukkit.command.Command;

import java.util.List;

public abstract class BukkitCommand extends Command {
    protected BukkitCommand(final String name) {
        super(name);
    }

    protected BukkitCommand(final String name, final String description, final String usageMessage, final List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }
}
