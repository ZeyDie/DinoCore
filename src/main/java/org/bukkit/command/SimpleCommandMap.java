package org.bukkit.command;

import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.command.defaults.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.regex.Pattern;

import static org.bukkit.util.Java15Compat.Arrays_copyOfRange;

public class SimpleCommandMap implements CommandMap {
    private static final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", Pattern.LITERAL);
    protected final Map<String, Command> knownCommands = new HashMap<String, Command>();
    protected final Set<String> aliases = new HashSet<String>();
    private final Server server;
    protected static final Set<VanillaCommand> fallbackCommands = new HashSet<VanillaCommand>();

    static {
        fallbackCommands.add(new ListCommand());
        fallbackCommands.add(new OpCommand());
        fallbackCommands.add(new DeopCommand());
        fallbackCommands.add(new BanIpCommand());
        fallbackCommands.add(new PardonIpCommand());
        fallbackCommands.add(new BanCommand());
        fallbackCommands.add(new PardonCommand());
        fallbackCommands.add(new KickCommand());
        fallbackCommands.add(new TeleportCommand());
        fallbackCommands.add(new GiveCommand());
        fallbackCommands.add(new TimeCommand());
        fallbackCommands.add(new SayCommand());
        fallbackCommands.add(new WhitelistCommand());
        fallbackCommands.add(new TellCommand());
        fallbackCommands.add(new MeCommand());
        fallbackCommands.add(new KillCommand());
        fallbackCommands.add(new GameModeCommand());
        fallbackCommands.add(new HelpCommand());
        fallbackCommands.add(new ExpCommand());
        fallbackCommands.add(new ToggleDownfallCommand());
        fallbackCommands.add(new BanListCommand());
        fallbackCommands.add(new DefaultGameModeCommand());
        fallbackCommands.add(new SeedCommand());
        fallbackCommands.add(new DifficultyCommand());
        fallbackCommands.add(new WeatherCommand());
        fallbackCommands.add(new SpawnpointCommand());
        fallbackCommands.add(new ClearCommand());
        fallbackCommands.add(new GameRuleCommand());
        fallbackCommands.add(new EnchantCommand());
        fallbackCommands.add(new TestForCommand());
        fallbackCommands.add(new EffectCommand());
        fallbackCommands.add(new ScoreboardCommand());
        fallbackCommands.add(new PlaySoundCommand());
        fallbackCommands.add(new SpreadPlayersCommand());
    }

    public SimpleCommandMap(final Server server) {
        this.server = server;
        setDefaultCommands(server);
    }

    private void setDefaultCommands(final Server server) {
        register("bukkit", new SaveCommand());
        register("bukkit", new SaveOnCommand());
        register("bukkit", new SaveOffCommand());
        register("bukkit", new StopCommand());
        register("bukkit", new VersionCommand("version"));
        register("bukkit", new ReloadCommand("reload"));
        register("bukkit", new PluginsCommand("plugins"));
        register("bukkit", new TimingsCommand("timings"));
    }

    /**
     * {@inheritDoc}
     */
    public void registerAll(final String fallbackPrefix, final List<Command> commands) {
        if (commands != null) {
            for (final Command c : commands) {
                register(fallbackPrefix, c);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean register(final String fallbackPrefix, final Command command) {
        return register(command.getName(), fallbackPrefix, command);
    }

    /**
     * {@inheritDoc}
     */
    public boolean register(final String label, final String fallbackPrefix, final Command command) {
        final boolean registeredPassedLabel = register(label, fallbackPrefix, command, false);

        final Iterator<String> iterator = command.getAliases().iterator();
        while (iterator.hasNext()) {
            if (!register(iterator.next(), fallbackPrefix, command, true)) {
                iterator.remove();
            }
        }

        // Register to us so further updates of the commands label and aliases are postponed until its reregistered
        command.register(this);

        return registeredPassedLabel;
    }

    /**
     * Registers a command with the given name is possible, otherwise uses fallbackPrefix to create a unique name if its not an alias
     *
     * @param label          the name of the command, without the '/'-prefix.
     * @param fallbackPrefix a prefix which is prepended to the command with a ':' one or more times to make the command unique
     * @param command        the command to register
     * @return true if command was registered with the passed in label, false otherwise.
     * If isAlias was true a return of false indicates no command was registerd
     * If isAlias was false a return of false indicates the fallbackPrefix was used one or more times to create a unique name for the command
     */
    private synchronized boolean register(final String label, final String fallbackPrefix, final Command command, final boolean isAlias) {
        String lowerLabel = label.trim().toLowerCase();

        if (isAlias && knownCommands.containsKey(lowerLabel)) {
            // Request is for an alias and it conflicts with a existing command or previous alias ignore it
            // Note: This will mean it gets removed from the commands list of active aliases
            return false;
        }

        final String lowerPrefix = fallbackPrefix.trim().toLowerCase();
        boolean registerdPassedLabel = true;

        // If the command exists but is an alias we overwrite it, otherwise we rename it based on the fallbackPrefix
        while (knownCommands.containsKey(lowerLabel) && !aliases.contains(lowerLabel)) {
            lowerLabel = lowerPrefix + ":" + lowerLabel;
            registerdPassedLabel = false;
        }

        if (isAlias) {
            aliases.add(lowerLabel);
        } else {
            // Ensure lowerLabel isn't listed as a alias anymore and update the commands registered name
            aliases.remove(lowerLabel);
            command.setLabel(lowerLabel);
        }
        knownCommands.put(lowerLabel, command);

        return registerdPassedLabel;
    }

    protected Command getFallback(final String label) {
        for (final VanillaCommand cmd : fallbackCommands) {
            if (cmd.matches(label)) {
                return cmd;
            }
        }

        return null;
    }

    public Set<VanillaCommand> getFallbackCommands() {
        return Collections.unmodifiableSet(fallbackCommands);
    }

    /**
     * {@inheritDoc}
     */
    public boolean dispatch(final CommandSender sender, final String commandLine) throws CommandException {
        final String[] args = PATTERN_ON_SPACE.split(commandLine);

        if (args.length == 0) {
            return false;
        }

        final String sentCommandLabel = args[0].toLowerCase();
        final Command target = getCommand(sentCommandLabel);

        if (target == null) {
            return false;
        }

        try {
            target.timings.startTiming(); // Spigot
            // Note: we don't return the result of target.execute as thats success / failure, we return handled (true) or not handled (false)
            target.execute(sender, sentCommandLabel, Arrays_copyOfRange(args, 1, args.length));
            target.timings.stopTiming(); // Spigot
        } catch (final CommandException ex) {
            target.timings.stopTiming(); // Spigot
            throw ex;
        } catch (final Throwable ex) {
            target.timings.stopTiming(); // Spigot
            throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, ex);
        }

        // return true as command was handled
        return true;
    }

    public synchronized void clearCommands() {
        for (final Map.Entry<String, Command> entry : knownCommands.entrySet()) {
            entry.getValue().unregister(this);
        }
        knownCommands.clear();
        aliases.clear();
        setDefaultCommands(server);
    }

    public Command getCommand(final String name) {
        Command target = knownCommands.get(name.toLowerCase());
        if (target == null) {
            target = getFallback(name);
        }
        return target;
    }

    public List<String> tabComplete(final CommandSender sender, final String cmdLine) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(cmdLine, "Command line cannot null");

        final int spaceIndex = cmdLine.indexOf(' ');

        if (spaceIndex == -1) {
            final ArrayList<String> completions = new ArrayList<String>();
            final Map<String, Command> knownCommands = this.knownCommands;

            final String prefix = (sender instanceof Player ? "/" : "");

            for (final VanillaCommand command : fallbackCommands) {
                final String name = command.getName();

                if (!command.testPermissionSilent(sender)) {
                    continue;
                }
                if (knownCommands.containsKey(name)) {
                    // Don't let a vanilla command override a command added below
                    // This has to do with the way aliases work
                    continue;
                }
                if (!StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                    continue;
                }

                completions.add(prefix + name);
            }

            for (final Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
                final Command command = commandEntry.getValue();

                if (!command.testPermissionSilent(sender)) {
                    continue;
                }

                final String name = commandEntry.getKey(); // Use the alias, not command name

                if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                    completions.add(prefix + name);
                }
            }

            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            return completions;
        }

        final String commandName = cmdLine.substring(0, spaceIndex);
        final Command target = getCommand(commandName);

        if (target == null) {
            return null;
        }

        if (!target.testPermissionSilent(sender)) {
            return null;
        }

        final String argLine = cmdLine.substring(spaceIndex + 1, cmdLine.length());
        final String[] args = PATTERN_ON_SPACE.split(argLine, -1);

        try {
            return target.tabComplete(sender, commandName, args);
        } catch (final CommandException ex) {
            throw ex;
        } catch (final Throwable ex) {
            throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex);
        }
    }

    public Collection<Command> getCommands() {
        return knownCommands.values();
    }

    public void registerServerAliases() {
        final Map<String, String[]> values = server.getCommandAliases();

        for (final String alias : values.keySet()) {
            final String[] targetNames = values.get(alias);
            final List<Command> targets = new ArrayList<Command>();
            final StringBuilder bad = new StringBuilder();

            for (final String name : targetNames) {
                final Command command = getCommand(name);

                if (command == null) {
                    if (bad.length() > 0) {
                        bad.append(", ");
                    }
                    bad.append(name);
                } else {
                    targets.add(command);
                }
            }

            // We register these as commands so they have absolute priority.

            if (!targets.isEmpty()) {
                knownCommands.put(alias.toLowerCase(), new MultipleCommandAlias(alias.toLowerCase(), targets.toArray(new Command[0])));
            } else {
                knownCommands.remove(alias.toLowerCase());
            }

            if (bad.length() > 0) {
                server.getLogger().warning("The following command(s) could not be aliased under '" + alias + "' because they do not exist: " + bad);
            }
        }
    }
}
