package org.bukkit.command;

/**
 * Represents a command that delegates to one or more other commands
 */
public class MultipleCommandAlias extends Command {
    private Command[] commands;

    public MultipleCommandAlias(final String name, final Command[] commands) {
        super(name);
        this.commands = commands;
    }

    /**
     * Gets the commands associated with the multi-command alias.
     *
     * @return commands associated with alias
     */
    public Command[] getCommands() {
        return commands;
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        boolean result = false;

        for (final Command command : commands) {
            result |= command.execute(sender, commandLabel, args);
        }

        return result;
    }
}
