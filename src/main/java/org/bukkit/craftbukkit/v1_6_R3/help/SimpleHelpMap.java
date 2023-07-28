package org.bukkit.craftbukkit.v1_6_R3.help;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.help.*;

import java.util.*;

/**
 * Standard implementation of {@link HelpMap} for CraftBukkit servers.
 */
public class SimpleHelpMap implements HelpMap {

    private HelpTopic defaultTopic;
    private final Map<String, HelpTopic> helpTopics;
    private final Map<Class, HelpTopicFactory<Command>> topicFactoryMap;
    private final CraftServer server;
    private HelpYamlReader yaml;

    @SuppressWarnings("unchecked")
    public SimpleHelpMap(final CraftServer server) {
        this.helpTopics = new TreeMap<String, HelpTopic>(HelpTopicComparator.topicNameComparatorInstance()); // Using a TreeMap for its explicit sorting on key
        this.topicFactoryMap = new HashMap<Class, HelpTopicFactory<Command>>();
        this.server = server;
        this.yaml = new HelpYamlReader(server);

        Predicate indexFilter = Predicates.not(Predicates.instanceOf(CommandAliasHelpTopic.class));
        if (!yaml.commandTopicsInMasterIndex()) {
            indexFilter = Predicates.and(indexFilter, Predicates.not(new IsCommandTopicPredicate()));
        }

        this.defaultTopic = new IndexHelpTopic("Index", null, null, Collections2.filter(helpTopics.values(), indexFilter), "Use /help [n] to get page n of help.");

        registerHelpTopicFactory(MultipleCommandAlias.class, new MultipleCommandAliasHelpTopicFactory());
    }

    public synchronized HelpTopic getHelpTopic(final String topicName) {
        if (topicName.isEmpty()) {
            return defaultTopic;
        }

        if (helpTopics.containsKey(topicName)) {
            return helpTopics.get(topicName);
        }

        return null;
    }

    public Collection<HelpTopic> getHelpTopics() {
        return helpTopics.values();
    }

    public synchronized void addTopic(final HelpTopic topic) {
        // Existing topics take priority
        if (!helpTopics.containsKey(topic.getName())) {
            helpTopics.put(topic.getName(), topic);
        }
    }

    public synchronized void clear() {
        helpTopics.clear();
    }

    public List<String> getIgnoredPlugins() {
        return yaml.getIgnoredPlugins();
    }

    /**
     * Reads the general topics from help.yml and adds them to the help index.
     */
    public synchronized void initializeGeneralTopics() {
        yaml = new HelpYamlReader(server);

        // Initialize general help topics from the help.yml file
        for (final HelpTopic topic : yaml.getGeneralTopics()) {
            addTopic(topic);
        }

        // Initialize index help topics from the help.yml file
        for (final HelpTopic topic : yaml.getIndexTopics()) {
            if (topic.getName().equals("Default")) {
                defaultTopic = topic;
            } else {
                addTopic(topic);
            }
        }
    }

    /**
     * Processes all the commands registered in the server and creates help topics for them.
     */
    public synchronized void initializeCommands() {
        // ** Load topics from highest to lowest priority order **
        final Set<String> ignoredPlugins = new HashSet<String>(yaml.getIgnoredPlugins());

        // Don't load any automatic help topics if All is ignored
        if (ignoredPlugins.contains("All")) {
            return;
        }

        // Initialize help topics from the server's command map
        outer: for (final Command command : server.getCommandMap().getCommands()) {
            if (commandInIgnoredPlugin(command, ignoredPlugins)) {
                continue;
            }

            // Register a topic
            for (final Class c : topicFactoryMap.keySet()) {
                if (c.isAssignableFrom(command.getClass())) {
                    final HelpTopic t = topicFactoryMap.get(c).createTopic(command);
                    if (t != null) addTopic(t);
                    continue outer;
                }
                if (command instanceof PluginCommand && c.isAssignableFrom(((PluginCommand)command).getExecutor().getClass())) {
                    final HelpTopic t = topicFactoryMap.get(c).createTopic(command);
                    if (t != null) addTopic(t);
                    continue outer;
                }
            }
            addTopic(new GenericCommandHelpTopic(command));
        }

        // Initialize command alias help topics
        for (final Command command : server.getCommandMap().getCommands()) {
            if (commandInIgnoredPlugin(command, ignoredPlugins)) {
                continue;
            }
            for (final String alias : command.getAliases()) {
                if (!helpTopics.containsKey("/" + alias)) {
                    addTopic(new CommandAliasHelpTopic("/" + alias, "/" + command.getLabel(), this));
                }
            }
        }

        // Initialize help topics from the server's fallback commands
        for (final VanillaCommand command : server.getCommandMap().getFallbackCommands()) {
            if (!commandInIgnoredPlugin(command, ignoredPlugins)) {
                addTopic(new GenericCommandHelpTopic(command));
            }
        }

        // Add alias sub-index
        final Collection<HelpTopic> filteredTopics = Collections2.filter(helpTopics.values(), Predicates.instanceOf(CommandAliasHelpTopic.class));
        if (!filteredTopics.isEmpty()) {
            addTopic(new IndexHelpTopic("Aliases", "Lists command aliases", null, filteredTopics));
        }

        // Initialize plugin-level sub-topics
        final Map<String, Set<HelpTopic>> pluginIndexes = new HashMap<String, Set<HelpTopic>>();
        fillPluginIndexes(pluginIndexes, server.getCommandMap().getCommands());
        fillPluginIndexes(pluginIndexes, server.getCommandMap().getFallbackCommands());

        for (final Map.Entry<String, Set<HelpTopic>> entry : pluginIndexes.entrySet()) {
            addTopic(new IndexHelpTopic(entry.getKey(), "All commands for " + entry.getKey(), null, entry.getValue(), "Below is a list of all " + entry.getKey() + " commands:"));
        }

        // Amend help topics from the help.yml file
        for (final HelpTopicAmendment amendment : yaml.getTopicAmendments()) {
            if (helpTopics.containsKey(amendment.getTopicName())) {
                helpTopics.get(amendment.getTopicName()).amendTopic(amendment.getShortText(), amendment.getFullText());
                if (amendment.getPermission() != null) {
                    helpTopics.get(amendment.getTopicName()).amendCanSee(amendment.getPermission());
                }
            }
        }
    }

    private void fillPluginIndexes(final Map<String, Set<HelpTopic>> pluginIndexes, final Collection<? extends Command> commands) {
        for (final Command command : commands) {
            final String pluginName = getCommandPluginName(command);
            if (pluginName != null) {
                final HelpTopic topic = getHelpTopic("/" + command.getLabel());
                if (topic != null) {
                    if (!pluginIndexes.containsKey(pluginName)) {
                        pluginIndexes.put(pluginName, new TreeSet<HelpTopic>(HelpTopicComparator.helpTopicComparatorInstance())); //keep things in topic order
                    }
                    pluginIndexes.get(pluginName).add(topic);
                }
            }
        }
    }

    private String getCommandPluginName(final Command command) {
        if (command instanceof BukkitCommand || command instanceof VanillaCommand) {
            return "Bukkit";
        }
        if (command instanceof PluginIdentifiableCommand) {
            return ((PluginIdentifiableCommand)command).getPlugin().getName();
        }
        return null;
    }

    private boolean commandInIgnoredPlugin(final Command command, final Set<String> ignoredPlugins) {
        if ((command instanceof BukkitCommand || command instanceof VanillaCommand) && ignoredPlugins.contains("Bukkit")) {
            return true;
        }
        if (command instanceof PluginIdentifiableCommand && ignoredPlugins.contains(((PluginIdentifiableCommand)command).getPlugin().getName())) {
            return true;
        }
        return false;
    }

    public void registerHelpTopicFactory(final Class commandClass, final HelpTopicFactory factory) {
        if (!Command.class.isAssignableFrom(commandClass) && !CommandExecutor.class.isAssignableFrom(commandClass)) {
            throw new IllegalArgumentException("commandClass must implement either Command or CommandExecutor!");
        }
        topicFactoryMap.put(commandClass, factory);
    }

    private class IsCommandTopicPredicate implements Predicate<HelpTopic> {

        public boolean apply(final HelpTopic topic) {
            return topic.getName().charAt(0) == '/';
        }
    }
}
