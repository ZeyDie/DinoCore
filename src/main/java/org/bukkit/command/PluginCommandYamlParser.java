package org.bukkit.command;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PluginCommandYamlParser {

    public static List<Command> parse(final Plugin plugin) {
        final List<Command> pluginCmds = new ArrayList<Command>();

        final Map<String, Map<String, Object>> map = plugin.getDescription().getCommands();

        if (map == null) {
            return pluginCmds;
        }

        for (final Entry<String, Map<String, Object>> entry : map.entrySet()) {
            final Command newCmd = new PluginCommand(entry.getKey(), plugin);
            final Object description = entry.getValue().get("description");
            final Object usage = entry.getValue().get("usage");
            final Object aliases = entry.getValue().get("aliases");
            final Object permission = entry.getValue().get("permission");
            final Object permissionMessage = entry.getValue().get("permission-message");

            if (description != null) {
                newCmd.setDescription(description.toString());
            }

            if (usage != null) {
                newCmd.setUsage(usage.toString());
            }

            if (aliases != null) {
                final List<String> aliasList = new ArrayList<String>();

                if (aliases instanceof List) {
                    for (final Object o : (List<?>) aliases) {
                        aliasList.add(o.toString());
                    }
                } else {
                    aliasList.add(aliases.toString());
                }

                newCmd.setAliases(aliasList);
            }

            if (permission != null) {
                newCmd.setPermission(permission.toString());
            }

            if (permissionMessage != null) {
                newCmd.setPermissionMessage(permissionMessage.toString());
            }

            pluginCmds.add(newCmd);
        }
        return pluginCmds;
    }
}
