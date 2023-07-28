package org.bukkit.craftbukkit.v1_6_R3.help;

import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.IndexHelpTopic;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class CustomIndexHelpTopic extends IndexHelpTopic {
    private List<String> futureTopics;
    private final HelpMap helpMap;

    public CustomIndexHelpTopic(final HelpMap helpMap, final String name, final String shortText, final String permission, final List<String> futureTopics, final String preamble) {
        super(name, shortText, permission, new HashSet<HelpTopic>(), preamble);
        this.helpMap = helpMap;
        this.futureTopics = futureTopics;
    }

    @Override
    public String getFullText(final CommandSender sender) {
        if (futureTopics != null) {
            final List<HelpTopic> topics = new LinkedList<HelpTopic>();
            for (final String futureTopic : futureTopics) {
                final HelpTopic topic = helpMap.getHelpTopic(futureTopic);
                if (topic != null) {
                    topics.add(topic);
                }
            }
            setTopicsCollection(topics);
            futureTopics = null;
        }

        return super.getFullText(sender);
    }
}
