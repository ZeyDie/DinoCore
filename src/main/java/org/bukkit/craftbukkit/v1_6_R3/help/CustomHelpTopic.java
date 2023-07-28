package org.bukkit.craftbukkit.v1_6_R3.help;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.help.HelpTopic;

/**
 * This is a help topic implementation for general topics registered in the help.yml file.
 */
public class CustomHelpTopic extends HelpTopic {
    private final String permissionNode;

    public CustomHelpTopic(final String name, final String shortText, final String fullText, final String permissionNode) {
        this.permissionNode = permissionNode;
        this.name = name;
        this.shortText = shortText;
        this.fullText = shortText + "\n" + fullText;
    }

    public boolean canSee(final CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }

        if (!permissionNode.isEmpty()) {
            return sender.hasPermission(permissionNode);
        } else {
            return true;
        }
    }
}
