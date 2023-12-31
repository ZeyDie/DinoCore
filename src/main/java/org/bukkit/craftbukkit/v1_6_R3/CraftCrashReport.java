package org.bukkit.craftbukkit.v1_6_R3;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;


public class CraftCrashReport implements Callable {

    public Object call() throws Exception {
        final StringWriter value = new StringWriter();
        try {
            value.append("\n   Running: ").append(Bukkit.getName()).append(" version ").append(Bukkit.getVersion()).append(" (Implementing API version ").append(Bukkit.getBukkitVersion()).append(") ").append(String.valueOf(net.minecraft.server.MinecraftServer.getServer().isServerInOnlineMode()));
            value.append("\n   Plugins: {");
            for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                final PluginDescriptionFile description = plugin.getDescription();
                value.append(' ').append(description.getFullName()).append(' ').append(description.getMain()).append(' ').append(Arrays.toString(description.getAuthors().toArray())).append(',');
            }
            value.append("}\n   Warnings: ").append(Bukkit.getWarningState().name());
            value.append("\n   Threads: {");
            for (final Map.Entry<Thread, ? extends Object[]> entry : Thread.getAllStackTraces().entrySet()) {
                value.append(' ').append(entry.getKey().getState().name()).append(' ').append(entry.getKey().getName()).append(": ").append(Arrays.toString(entry.getValue())).append(',');
            }
            value.append("}\n   ").append(Bukkit.getScheduler().toString());
        } catch (final Throwable t) {
            value.append("\n   Failed to handle CraftCrashReport:\n");
            final PrintWriter writer = new PrintWriter(value);
            t.printStackTrace(writer);
            writer.flush();
        }
        return value.toString();
    }

}
