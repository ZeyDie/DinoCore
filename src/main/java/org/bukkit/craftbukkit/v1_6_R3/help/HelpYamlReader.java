package org.bukkit.craftbukkit.v1_6_R3.help;

import com.zeydie.DefaultPaths;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.help.HelpTopic;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * HelpYamlReader is responsible for processing the contents of the help.yml file.
 */
public class HelpYamlReader {

    private YamlConfiguration helpYaml;
    private final char ALT_COLOR_CODE = '&';
    private final Server server;

    public HelpYamlReader(final Server server) {
        this.server = server;

        //TODO ZoomCodeStart
        final File helpYamlFile = DefaultPaths.getDefaultFile("help.yml");
        //TODO ZoomCodeEnd
        //TODO ZoomCodeClear
        //File helpYamlFile = new File("help.yml");
        final YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(getClass().getClassLoader().getResourceAsStream("configurations/help.yml"));

        try {
            helpYaml = YamlConfiguration.loadConfiguration(helpYamlFile);
            helpYaml.options().copyDefaults(true);
            helpYaml.setDefaults(defaultConfig);

            try {
                if (!helpYamlFile.exists()) {
                    helpYaml.save(helpYamlFile);
                }
            } catch (final IOException ex) {
                server.getLogger().log(Level.SEVERE, "Could not save " + helpYamlFile, ex);
            }
        } catch (final Exception ex) {
            server.getLogger().severe("Failed to load help.yml. Verify the yaml indentation is correct. Reverting to default help.yml.");
            helpYaml = defaultConfig;
        }
    }

    /**
     * Extracts a list of all general help topics from help.yml
     *
     * @return A list of general topics.
     */
    public List<HelpTopic> getGeneralTopics() {
        final List<HelpTopic> topics = new LinkedList<HelpTopic>();
        final ConfigurationSection generalTopics = helpYaml.getConfigurationSection("general-topics");
        if (generalTopics != null) {
            for (final String topicName : generalTopics.getKeys(false)) {
                final ConfigurationSection section = generalTopics.getConfigurationSection(topicName);
                final String shortText = ChatColor.translateAlternateColorCodes(ALT_COLOR_CODE, section.getString("shortText", ""));
                final String fullText = ChatColor.translateAlternateColorCodes(ALT_COLOR_CODE, section.getString("fullText", ""));
                final String permission = section.getString("permission", "");
                topics.add(new CustomHelpTopic(topicName, shortText, fullText, permission));
            }
        }
        return topics;
    }

    /**
     * Extracts a list of all index topics from help.yml
     *
     * @return A list of index topics.
     */
    public List<HelpTopic> getIndexTopics() {
        final List<HelpTopic> topics = new LinkedList<HelpTopic>();
        final ConfigurationSection indexTopics = helpYaml.getConfigurationSection("index-topics");
        if (indexTopics != null) {
            for (final String topicName : indexTopics.getKeys(false)) {
                final ConfigurationSection section = indexTopics.getConfigurationSection(topicName);
                final String shortText = ChatColor.translateAlternateColorCodes(ALT_COLOR_CODE, section.getString("shortText", ""));
                final String preamble = ChatColor.translateAlternateColorCodes(ALT_COLOR_CODE, section.getString("preamble", ""));
                final String permission = ChatColor.translateAlternateColorCodes(ALT_COLOR_CODE, section.getString("permission", ""));
                final List<String> commands = section.getStringList("commands");
                topics.add(new CustomIndexHelpTopic(server.getHelpMap(), topicName, shortText, permission, commands, preamble));
            }
        }
        return topics;
    }

    /**
     * Extracts a list of topic amendments from help.yml
     *
     * @return A list of amendments.
     */
    public List<HelpTopicAmendment> getTopicAmendments() {
        final List<HelpTopicAmendment> amendments = new LinkedList<HelpTopicAmendment>();
        final ConfigurationSection commandTopics = helpYaml.getConfigurationSection("amended-topics");
        if (commandTopics != null) {
            for (final String topicName : commandTopics.getKeys(false)) {
                final ConfigurationSection section = commandTopics.getConfigurationSection(topicName);
                final String description = ChatColor.translateAlternateColorCodes(ALT_COLOR_CODE, section.getString("shortText", ""));
                final String usage = ChatColor.translateAlternateColorCodes(ALT_COLOR_CODE, section.getString("fullText", ""));
                final String permission = section.getString("permission", "");
                amendments.add(new HelpTopicAmendment(topicName, description, usage, permission));
            }
        }
        return amendments;
    }

    public List<String> getIgnoredPlugins() {
        return helpYaml.getStringList("ignore-plugins");
    }

    public boolean commandTopicsInMasterIndex() {
        return helpYaml.getBoolean("command-topics-in-master-index", true);
    }
}
