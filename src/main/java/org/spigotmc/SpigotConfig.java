package org.spigotmc;

import com.google.common.base.Throwables;
import com.zeydie.DefaultPaths;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_6_R3.command.TicksPerSecondCommand;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SpigotConfig {

    //TODO ZoomCodeStart
    private static final File CONFIG_FILE = DefaultPaths.getDefaultFile("spigot.yml");
    //TODO ZoomCodeEnd
    //TODO ZoomCodeClear
    //private static final File CONFIG_FILE = new File( "spigot.yml" );
    private static final String HEADER = "This is the main configuration file for Spigot.\n"
            + "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
            + "with caution, and make sure you know what each option does before configuring.\n"
            + "For a reference for any variable inside this file, check out the Spigot wiki at\n"
            + "http://www.spigotmc.org/wiki/spigot-configuration/\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to Spigot,\n"
            + "join us at the IRC or drop by our forums and leave a post.\n"
            + "\n"
            + "IRC: #spigot @ irc.esper.net ( http://webchat.esper.net/?channel=spigot )\n"
            + "Forums: http://www.spigotmc.org/forum/\n";
    /*========================================================================*/
    //TODO ZoomCodeReplace private on public
    public static YamlConfiguration config;
    static int version;
    static Map<String, Command> commands;
    /*========================================================================*/
    private static Metrics metrics;

    public static void init() {
        config = YamlConfiguration.loadConfiguration(CONFIG_FILE);
        config.options().header(HEADER);
        config.options().copyDefaults(true);

        commands = new HashMap<String, Command>();

        version = getInt("config-version", 3);
        set("config-version", 3);
        readConfig(SpigotConfig.class, null);
    }

    public static void registerCommands() {
        for (final Map.Entry<String, Command> entry : commands.entrySet()) {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Spigot", entry.getValue());
        }

        if (metrics == null) {
            try {
                metrics = new Metrics();
                metrics.start();
            } catch (final IOException ex) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not start metrics service", ex);
            }
        }
    }

    static void readConfig(final Class<?> clazz, final Object instance) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (final InvocationTargetException ex) {
                        Throwables.propagate(ex.getCause());
                    } catch (final Exception ex) {
                        Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, ex);
                    }
                }
            }
        }

        try {
            config.save(CONFIG_FILE);
        } catch (final IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    private static void set(final String path, final Object val) {
        config.set(path, val);
    }

    private static boolean getBoolean(final String path, final boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static int getInt(final String path, final int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List getList(final String path, final T def) {
        config.addDefault(path, def);
        return (List<T>) config.getList(path, config.getList(path));
    }

    private static String getString(final String path, final String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    public static boolean preventProxies;

    private static void preventProxies() {
        preventProxies = getBoolean("settings.prevent-proxies", false);
    }

    private static void tpsCommand() {
        commands.put("tps", new TicksPerSecondCommand("tps"));
    }

    public static int timeoutTime = 60;
    //TODO ZoomCodeReplace true on false
    public static boolean restartOnCrash = false;
    public static String restartScript = "./start.sh";

    private static void watchdog() {
        timeoutTime = getInt("settings.timeout-time", timeoutTime);
        restartOnCrash = getBoolean("settings.restart-on-crash", restartOnCrash);
        restartScript = getString("settings.restart-script", restartScript);
        commands.put("restart", new RestartCommand("restart"));
        WatchdogThread.doStart(timeoutTime, restartOnCrash);
    }

    public static class Listener {

        public String host;
        public int port;
        public boolean netty;
        public long connectionThrottle;

        public Listener(final String host, final int port, final boolean netty, final long connectionThrottle) {
            this.host = host;
            this.port = port;
            this.netty = netty;
            this.connectionThrottle = connectionThrottle;
        }
    }

    public static List<Listener> listeners = new ArrayList<Listener>();
    public static int nettyThreads;

    private static void listeners() {
        listeners.clear(); // We don't rebuild listeners on reload but we should clear them out!

        final Map<String, Object> def = new HashMap<String, Object>();
        def.put("host", "default");
        def.put("port", "default");
        def.put("netty", true);
        // def.put( "throttle", "default" );

        config.addDefault("listeners", Collections.singletonList(def));
        for (final Map<String, Object> info : (List<Map<String, Object>>) config.getList("listeners")) {
            String host = (String) info.get("host");
            if ("default".equals(host)) {
                host = Bukkit.getIp();
            } else {
                throw new IllegalArgumentException("Can only bind listener to default! Configure it in server.properties");
            }
            final int port;

            if (info.get("port") instanceof Integer) {
                throw new IllegalArgumentException("Can only bind port to default! Configure it in server.properties");
            } else {
                port = Bukkit.getPort();
            }
            final boolean netty = (Boolean) info.get("netty");
            // long connectionThrottle = ( info.get( "throttle" ) instanceof Number ) ? ( (Number) info.get( "throttle" ) ).longValue() : Bukkit.getConnectionThrottle();
            listeners.add(new Listener(host, port, netty, Bukkit.getConnectionThrottle()));
        }
        if (listeners.size() != 1) {
            throw new IllegalArgumentException("May only have one listener!");
        }

        //TODO ZeyCodeReplace 3 on 8
        nettyThreads = getInt("settings.netty-threads", 8);
    }

    public static List<String> bungeeAddresses = Arrays.asList(new String[]
            {
                    "127.0.0.1"
            });
    public static boolean bungee = true;

    private static void bungee() {
        bungeeAddresses = getList("settings.bungeecord-addresses", bungeeAddresses);

        //TODO ZeyCodeReplace true on false
        bungee = getBoolean("settings.bungeecord", false);
    }

    public static List<String> spamExclusions;

    private static void spamExclusions() {
        spamExclusions = getList("commands.spam-exclusions", Arrays.asList(new String[]
                {
                        "/skill"
                }));
    }

    public static boolean logCommands;

    private static void logCommands() {
        logCommands = getBoolean("commands.log", true);
    }

    public static boolean tabComplete;

    private static void tabComplete() {
        tabComplete = getBoolean("commands.tab-complete", true);
    }

    public static String whitelistMessage;
    public static String unknownCommandMessage;
    public static String serverFullMessage;
    public static String outdatedClientMessage;
    public static String outdatedServerMessage;

    private static String transform(final String s) {
        return ChatColor.translateAlternateColorCodes('&', s).replaceAll("\\n", "\n");
    }

    private static void messages() {
        whitelistMessage = transform(getString("messages.whitelist", "You are not whitelisted on this server!"));
        unknownCommandMessage = transform(getString("messages.unknown-command", "Unknown command. Type \"/help\" for help."));
        serverFullMessage = transform(getString("messages.server-full", "The server is full!"));
        outdatedClientMessage = transform(getString("messages.outdated-client", "Outdated client!"));
        outdatedServerMessage = transform(getString("messages.outdated-server", "Outdated server!"));
    }

    public static List<Pattern> logFilters;

    private static void filters() {
        final List<String> def = Arrays.asList(new String[]
                {
                        "^(.*)(/login)(.*)$"
                });
        logFilters = new ArrayList<Pattern>();

        for (final String regex : (List<String>) getList("settings.log-filters", def)) {
            try {
                logFilters.add(Pattern.compile(regex));
            } catch (final PatternSyntaxException ex) {
                Bukkit.getLogger().log(Level.WARNING, "Supplied filter " + regex + " is invalid, ignoring!", ex);
            }
        }

        Bukkit.getLogger().setFilter(new LogFilter());
    }
}
