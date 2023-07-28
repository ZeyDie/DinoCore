package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.TimedRegisteredListener;
import org.bukkit.util.StringUtil;
import org.spigotmc.CustomTimingsHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

// Spigot start
// Spigot end

public class TimingsCommand extends BukkitCommand {
    private static final List<String> TIMINGS_SUBCOMMANDS = ImmutableList.of("report", "reset", "on", "off", "paste"); // Spigot
    public static long timingStart = 0; // Spigot

    public TimingsCommand(final String name) {
        super(name);
        this.description = "Manages Spigot Timings data to see performance of the server."; // Spigot
        this.usageMessage = "/timings <reset|report|on|off|paste>"; // Spigot
        this.setPermission("bukkit.command.timings");
    }

    // Spigot start - redesigned Timings Command
    public void executeSpigotTimings(final CommandSender sender, final String[] args) {
        if ("on".equals(args[0])) {
            ((SimplePluginManager) Bukkit.getPluginManager()).useTimings(true);
            CustomTimingsHandler.reload();
            sender.sendMessage("Enabled Timings & Reset");
            return;
        } else if ("off".equals(args[0])) {
            ((SimplePluginManager) Bukkit.getPluginManager()).useTimings(false);
            sender.sendMessage("Disabled Timings");
            return;
        }

        if (!Bukkit.getPluginManager().useTimings()) {
            sender.sendMessage("Please enable timings by typing /timings on");
            return;
        }

        final boolean paste = "paste".equals(args[0]);
        if ("reset".equals(args[0])) {
            CustomTimingsHandler.reload();
            sender.sendMessage("Timings reset");
        } else if ("merged".equals(args[0]) || "report".equals(args[0]) || paste) {
            final long sampleTime = System.nanoTime() - timingStart;
            int index = 0;
            final File timingFolder = new File("timings");
            timingFolder.mkdirs();
            File timings = new File(timingFolder, "timings.txt");
            final ByteArrayOutputStream bout = (paste) ? new ByteArrayOutputStream() : null;
            while (timings.exists()) timings = new File(timingFolder, "timings" + (++index) + ".txt");
            PrintStream fileTimings = null;
            try {
                fileTimings = (paste) ? new PrintStream(bout) : new PrintStream(timings);

                CustomTimingsHandler.printTimings(fileTimings);
                fileTimings.println("Sample time " + sampleTime + " (" + sampleTime / 1.0E9 + "s)");

                if (paste) {
                    //TODO ZoomCodeStart
                    final com.zeydie.modified.PasteThread pasteThread = new com.zeydie.modified.PasteThread(sender, bout);
                    pasteThread.start();
                    //TODO ZoomCodeEnd
                    //TODO ZoomCodeClear
                    //new PasteThread( sender, bout ).start();
                    return;
                }

                sender.sendMessage("Timings written to " + timings.getPath());
                sender.sendMessage("Paste contents of file into form at http://www.spigotmc.org/go/timings to read results.");

            } catch (final IOException e) {
            } finally {
                if (fileTimings != null) {
                    fileTimings.close();
                }
            }
        }
    }
    // Spigot end

    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 1) { // Spigot
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        if (true) {
            executeSpigotTimings(sender, args);
            return true;
        } // Spigot
        if (!sender.getServer().getPluginManager().useTimings()) {
            sender.sendMessage("Please enable timings by setting \"settings.plugin-profiling\" to true in bukkit.yml");
            return true;
        }

        final boolean separate = "separate".equals(args[0]);
        if ("reset".equals(args[0])) {
            for (final HandlerList handlerList : HandlerList.getHandlerLists()) {
                for (final RegisteredListener listener : handlerList.getRegisteredListeners()) {
                    if (listener instanceof TimedRegisteredListener) {
                        ((TimedRegisteredListener) listener).reset();
                    }
                }
            }
            sender.sendMessage("Timings reset");
        } else if ("merged".equals(args[0]) || separate) {

            int index = 0;
            int pluginIdx = 0;
            final File timingFolder = new File("timings");
            timingFolder.mkdirs();
            File timings = new File(timingFolder, "timings.txt");
            File names = null;
            while (timings.exists()) timings = new File(timingFolder, "timings" + (++index) + ".txt");
            PrintStream fileTimings = null;
            PrintStream fileNames = null;
            try {
                fileTimings = new PrintStream(timings);
                if (separate) {
                    names = new File(timingFolder, "names" + index + ".txt");
                    fileNames = new PrintStream(names);
                }
                for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    pluginIdx++;
                    long totalTime = 0;
                    if (separate) {
                        fileNames.println(pluginIdx + " " + plugin.getDescription().getFullName());
                        fileTimings.println("Plugin " + pluginIdx);
                    } else fileTimings.println(plugin.getDescription().getFullName());
                    for (final RegisteredListener listener : HandlerList.getRegisteredListeners(plugin)) {
                        if (listener instanceof TimedRegisteredListener) {
                            final TimedRegisteredListener trl = (TimedRegisteredListener) listener;
                            final long time = trl.getTotalTime();
                            final int count = trl.getCount();
                            if (count == 0) continue;
                            final long avg = time / count;
                            totalTime += time;
                            final Class<? extends Event> eventClass = trl.getEventClass();
                            if (count > 0 && eventClass != null) {
                                fileTimings.println("    " + eventClass.getSimpleName() + (trl.hasMultiple() ? " (and sub-classes)" : "") + " Time: " + time + " Count: " + count + " Avg: " + avg);
                            }
                        }
                    }
                    fileTimings.println("    Total time " + totalTime + " (" + totalTime / 1000000000 + "s)");
                }
                sender.sendMessage("Timings written to " + timings.getPath());
                if (separate) sender.sendMessage("Names written to " + names.getPath());
            } catch (final IOException e) {
            } finally {
                if (fileTimings != null) {
                    fileTimings.close();
                }
                if (fileNames != null) {
                    fileNames.close();
                }
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], TIMINGS_SUBCOMMANDS, new ArrayList<String>(TIMINGS_SUBCOMMANDS.size()));
        }
        return ImmutableList.of();
    }

    // Spigot start
    private static class PasteThread extends Thread {

        private final CommandSender sender;
        private final ByteArrayOutputStream bout;

        public PasteThread(final CommandSender sender, final ByteArrayOutputStream bout) {
            super("Timings paste thread");
            this.sender = sender;
            this.bout = bout;
        }

        @Override
        public synchronized void start() {
            if (sender instanceof RemoteConsoleCommandSender) {
                run();
            } else {
                super.start();
            }
        }

        @Override
        public void run() {
            try {
                final HttpURLConnection con = (HttpURLConnection) new URL("http://paste.ubuntu.com/").openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setInstanceFollowRedirects(false);

                final OutputStream out = con.getOutputStream();
                out.write("poster=Spigot&syntax=text&content=".getBytes(StandardCharsets.UTF_8));
                out.write(URLEncoder.encode(bout.toString("UTF-8"), "UTF-8").getBytes(StandardCharsets.UTF_8));
                out.close();
                con.getInputStream().close();

                final String location = con.getHeaderField("Location");
                final String pasteID = location.substring("http://paste.ubuntu.com/".length(), location.length() - 1);
                sender.sendMessage(ChatColor.GREEN + "View timings results can be viewed at http://www.spigotmc.org/go/timings?url=" + pasteID);
            } catch (final IOException ex) {
                sender.sendMessage(ChatColor.RED + "Error pasting timings, check your console for more information");
                Bukkit.getServer().getLogger().log(Level.WARNING, "Could not paste timings", ex);
            }
        }
    }
    // Spigot end
}
