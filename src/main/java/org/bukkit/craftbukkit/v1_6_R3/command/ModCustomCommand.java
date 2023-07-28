package org.bukkit.craftbukkit.v1_6_R3.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ModCustomCommand extends Command {

    public ModCustomCommand(final String name)
    {
        super(name);
    }

    public ModCustomCommand(final String name, final String description, final String usageMessage, final List<String> aliases)
    {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        // Dummy method
        return false;
    }
}