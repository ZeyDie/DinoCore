package org.bukkit.plugin.java;

// Cauldron start

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import me.Kepa2012.fastbukkit.model.IProxiedEvent;
import me.Kepa2012.fastbukkit.utils.ProxyCreator;
import net.md_5.specialsource.InheritanceMap;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.transformer.MavenShade;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.Warning;
import org.bukkit.Warning.WarningState;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.*;
import org.spigotmc.CustomTimingsHandler;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;

// Cauldron end

/**
 * Represents a Java plugin loader, allowing plugins in the form of .jar
 */
public class JavaPluginLoader implements PluginLoader {
    final Server server;
    final boolean extended = this.getClass() != JavaPluginLoader.class;
    boolean warn;

    private final Pattern[] fileFilters0 = {Pattern.compile("\\.jar$"),};
    /**
     * @deprecated Internal field that wasn't intended to be exposed
     */
    @Deprecated
    protected final Pattern[] fileFilters = fileFilters0;

    private final Map<String, Class<?>> classes0 = new HashMap<String, Class<?>>();
    /**
     * @deprecated Internal field that wasn't intended to be exposed
     */
    @Deprecated
    protected final Map<String, Class<?>> classes = classes0;

    private final Map<String, PluginClassLoader> loaders0 = new LinkedHashMap<String, PluginClassLoader>();
    /**
     * @deprecated Internal field that wasn't intended to be exposed
     */
    @Deprecated
    protected final Map<String, PluginClassLoader> loaders = loaders0;
    public static final CustomTimingsHandler pluginParentTimer = new CustomTimingsHandler("** Plugins"); // Spigot

    /**
     * This class was not meant to be extended
     */
    @Deprecated
    public JavaPluginLoader(final Server instance) {
        Validate.notNull(instance, "Server cannot be null");
        server = instance;
        warn = instance.getWarningState() != WarningState.OFF;
        if (extended && warn) {
            warn = false;
            instance.getLogger().log(Level.WARNING, "JavaPluginLoader not intended to be extended by " + getClass() + ", and may be final in a future version of Bukkit");
        }
    }

    public Plugin loadPlugin(final File file) throws InvalidPluginException {
        Validate.notNull(file, "File cannot be null");

        if (!file.exists()) {
            throw new InvalidPluginException(new FileNotFoundException(file.getPath() + " does not exist"));
        }

        final PluginDescriptionFile description;
        try {
            description = getPluginDescription(file);
        } catch (final InvalidDescriptionException ex) {
            throw new InvalidPluginException(ex);
        }

        final File dataFolder = new File(file.getParentFile(), description.getName());
        final File oldDataFolder = extended ? getDataFolder(file) : getDataFolder0(file); // Don't warn on deprecation, but maintain overridability

        // Found old data folder
        if (dataFolder.equals(oldDataFolder)) {
            // They are equal -- nothing needs to be done!
        } else if (dataFolder.isDirectory() && oldDataFolder.isDirectory()) {
            server.getLogger().log(Level.INFO, String.format(
                    "While loading %s (%s) found old-data folder: %s next to the new one: %s",
                    description.getName(),
                    file,
                    oldDataFolder,
                    dataFolder
            ));
        } else if (oldDataFolder.isDirectory() && !dataFolder.exists()) {
            if (!oldDataFolder.renameTo(dataFolder)) {
                throw new InvalidPluginException("Unable to rename old data folder: '" + oldDataFolder + "' to: '" + dataFolder + "'");
            }
            server.getLogger().log(Level.INFO, String.format(
                    "While loading %s (%s) renamed data folder: '%s' to '%s'",
                    description.getName(),
                    file,
                    oldDataFolder,
                    dataFolder
            ));
        }

        if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidPluginException(String.format(
                    "Projected datafolder: '%s' for %s (%s) exists and is not a directory",
                    dataFolder,
                    description.getName(),
                    file
            ));
        }

        List<String> depend = description.getDepend();
        if (depend == null) {
            depend = ImmutableList.<String>of();
        }

        for (final String pluginName : depend) {
            if (loaders0 == null) {
                throw new UnknownDependencyException(pluginName);
            }
            final PluginClassLoader current = loaders0.get(pluginName);

            if (current == null) {
                throw new UnknownDependencyException(pluginName);
            }
        }

        PluginClassLoader loader = null;
        JavaPlugin result = null;

        try {
            final URL[] urls = new URL[1];

            urls[0] = file.toURI().toURL();

            if (description.getClassLoaderOf() != null) {
                loader = loaders0.get(description.getClassLoaderOf());
                loader.addURL(urls[0]);
            } else {
                loader = new PluginClassLoader(this, urls, getClass().getClassLoader(), description); // Cauldron - pass description
            }

            final Class<?> jarClass = Class.forName(description.getMain(), true, loader);
            final Class<? extends JavaPlugin> plugin = jarClass.asSubclass(JavaPlugin.class);

            final Constructor<? extends JavaPlugin> constructor = plugin.getConstructor();

            result = constructor.newInstance();

            result.initialize(this, server, description, dataFolder, file, loader);
        } catch (final InvocationTargetException ex) {
            throw new InvalidPluginException(ex.getCause());
        } catch (final Throwable ex) {
            throw new InvalidPluginException(ex);
        }

        loaders0.put(description.getName(), loader);

        return result;
    }

    /**
     * @deprecated Relic method from PluginLoader that didn't get purged
     */
    @Deprecated
    public Plugin loadPlugin(final File file, final boolean ignoreSoftDependencies) throws InvalidPluginException {
        if (warn) {
            server.getLogger().log(Level.WARNING, "Method \"public Plugin loadPlugin(File, boolean)\" is Deprecated, and may be removed in a future version of Bukkit", new AuthorNagException(""));
            warn = false;
        }
        return loadPlugin(file);
    }

    /**
     * @deprecated Internal method that wasn't intended to be exposed
     */
    @Deprecated
    protected File getDataFolder(final File file) {
        if (warn) {
            server.getLogger().log(Level.WARNING, "Method \"protected File getDataFolder(File)\" is Deprecated, and may be removed in a future version of Bukkit", new AuthorNagException(""));
            warn = false;
        }
        return getDataFolder0(file);
    }

    private File getDataFolder0(final File file) {
        File dataFolder = null;

        final String filename = file.getName();
        final int index = file.getName().lastIndexOf(".");

        if (index != -1) {
            final String name = filename.substring(0, index);

            dataFolder = new File(file.getParentFile(), name);
        } else {
            // This is if there is no extension, which should not happen
            // Using _ to prevent name collision

            dataFolder = new File(file.getParentFile(), filename + "_");
        }

        return dataFolder;
    }

    public PluginDescriptionFile getPluginDescription(final File file) throws InvalidDescriptionException {
        Validate.notNull(file, "File cannot be null");

        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            final JarEntry entry = jar.getJarEntry("plugin.yml");

            if (entry == null) {
                throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain plugin.yml"));
            }

            stream = jar.getInputStream(entry);

            return new PluginDescriptionFile(stream);

        } catch (final IOException ex) {
            throw new InvalidDescriptionException(ex);
        } catch (final YAMLException ex) {
            throw new InvalidDescriptionException(ex);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (final IOException e) {
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (final IOException e) {
                }
            }
        }
    }

    public Pattern[] getPluginFileFilters() {
        return fileFilters0.clone();
    }

    /**
     * @deprecated Internal method that wasn't intended to be exposed
     */
    @Deprecated
    public Class<?> getClassByName(final String name) {
        if (warn) {
            server.getLogger().log(Level.WARNING, "Method \"public Class<?> getClassByName(String)\" is Deprecated, and may be removed in a future version of Bukkit", new AuthorNagException(""));
            warn = false;
        }
        return getClassByName0(name);
    }

    Class<?> getClassByName0(final String name) {
        Class<?> cachedClass = classes0.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (final String current : loaders0.keySet()) {
                final PluginClassLoader loader = loaders0.get(current);

                try {
                    cachedClass = loader.extended ? loader.findClass(name, false) : loader.findClass0(name, false); // Don't warn on deprecation, but maintain overridability
                } catch (final ClassNotFoundException cnfe) {
                }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }

    /**
     * @deprecated Internal method that wasn't intended to be exposed
     */
    @Deprecated
    public void setClass(final String name, final Class<?> clazz) {
        if (warn) {
            server.getLogger().log(Level.WARNING, "Method \"public void setClass(String, Class<?>)\" is Deprecated, and may be removed in a future version of Bukkit", new AuthorNagException(""));
            warn = false;
        }
        setClass0(name, clazz);
    }

    void setClass0(final String name, final Class<?> clazz) {
        if (!classes0.containsKey(name)) {
            classes0.put(name, clazz);

            if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
                final Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
                ConfigurationSerialization.registerClass(serializable);
            }
        }
    }

    /**
     * @deprecated Internal method that wasn't intended to be exposed
     */
    @Deprecated
    public void removeClass(final String name) {
        if (warn) {
            server.getLogger().log(Level.WARNING, "Method \"public void removeClass(String)\" is Deprecated, and may be removed in a future version of Bukkit", new AuthorNagException(""));
            warn = false;
        }
        removeClass0(name);
    }

    private void removeClass0(final String name) {
        final Class<?> clazz = classes0.remove(name);

        try {
            if ((clazz != null) && (ConfigurationSerializable.class.isAssignableFrom(clazz))) {
                final Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
                ConfigurationSerialization.unregisterClass(serializable);
            }
        } catch (final NullPointerException ex) {
            // Boggle!
            // (Native methods throwing NPEs is not fun when you can't stop it before-hand)
        }
    }

    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(final Listener listener, final Plugin plugin) {
        Validate.notNull(plugin, "Plugin can not be null");
        Validate.notNull(listener, "Listener can not be null");

        final boolean useTimings = server.getPluginManager().useTimings();
        final Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<Class<? extends Event>, Set<RegisteredListener>>();
        final Set<Method> methods;
        try {
            final Method[] publicMethods = listener.getClass().getMethods();
            methods = new HashSet<Method>(publicMethods.length, Float.MAX_VALUE);
            methods.addAll(Arrays.asList(publicMethods));
            methods.addAll(Arrays.asList(listener.getClass().getDeclaredMethods()));
        } catch (final NoClassDefFoundError e) {
            plugin.getLogger().severe("Plugin " + plugin.getDescription().getFullName() + " has failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.");
            return ret;
        }

        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                plugin.getLogger().severe(plugin.getDescription().getFullName() + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
                continue;
            }
            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            Set<RegisteredListener> eventSet = ret.get(eventClass);
            if (eventSet == null) {
                eventSet = new HashSet<RegisteredListener>();
                ret.put(eventClass, eventSet);
            }

            for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                // This loop checks for extending deprecated events
                if (clazz.getAnnotation(Deprecated.class) != null) {
                    final Warning warning = clazz.getAnnotation(Warning.class);
                    final WarningState warningState = server.getWarningState();
                    if (!warningState.printFor(warning)) {
                        break;
                    }
                    plugin.getLogger().log(
                            Level.WARNING,
                            String.format(
                                    "\"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated." +
                                            " \"%s\"; please notify the authors %s.",
                                    plugin.getDescription().getFullName(),
                                    clazz.getName(),
                                    method.toGenericString(),
                                    (warning != null && !warning.reason().isEmpty()) ? warning.reason() : "Server performance will be affected",
                                    Arrays.toString(plugin.getDescription().getAuthors().toArray())),
                            warningState == WarningState.ON ? new AuthorNagException(null) : null);
                    break;
                }
            }

            final CustomTimingsHandler timings = new CustomTimingsHandler("Plugin: " + plugin.getDescription().getFullName() + " Event: " + listener.getClass().getName() + "::" + method.getName() + "(" + eventClass.getSimpleName() + ")", pluginParentTimer); // Spigot

            //TODO Kepa2012Start
            final IProxiedEvent fastEvent = ProxyCreator.createEventProxy(method);
            //TODO Kepa2012End

            final EventExecutor executor = new EventExecutor() {
                public void execute(final Listener listener, final Event event) throws EventException {
                    try {
                        if (!eventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }
                        // Spigot start
                        final boolean isAsync = event.isAsynchronous();
                        if (!isAsync) timings.startTiming();

                        //TODO Kepa2012Start
                        if (fastEvent != null)
                            fastEvent.callEvent(listener, event);
                        else
                            //TODO Kepa2012End

                            method.invoke(listener, event);
                        if (!isAsync) timings.stopTiming();
                        // Spigot end
                    } catch (final InvocationTargetException ex) {
                        throw new EventException(ex.getCause());
                    } catch (final Throwable t) {
                        throw new EventException(t);
                    }
                }
            };
            if (false) { // Spigot - RL handles useTimings check now
                eventSet.add(new TimedRegisteredListener(listener, executor, eh.priority(), plugin, eh.ignoreCancelled()));
            } else {
                eventSet.add(new RegisteredListener(listener, executor, eh.priority(), plugin, eh.ignoreCancelled()));
            }
        }
        return ret;
    }

    public void enablePlugin(final Plugin plugin) {
        Validate.isTrue(plugin instanceof JavaPlugin, "Plugin is not associated with this PluginLoader");

        if (!plugin.isEnabled()) {
            plugin.getLogger().info("Enabling " + plugin.getDescription().getFullName());

            final JavaPlugin jPlugin = (JavaPlugin) plugin;

            final String pluginName = jPlugin.getDescription().getName();

            if (!loaders0.containsKey(pluginName)) {
                loaders0.put(pluginName, (PluginClassLoader) jPlugin.getClassLoader());
            }

            try {
                jPlugin.setEnabled(true);
            } catch (final Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            // Perhaps abort here, rather than continue going, but as it stands,
            // an abort is not possible the way it's currently written
            server.getPluginManager().callEvent(new PluginEnableEvent(plugin));
        }
    }

    public void disablePlugin(final Plugin plugin) {
        Validate.isTrue(plugin instanceof JavaPlugin, "Plugin is not associated with this PluginLoader");

        if (plugin.isEnabled()) {
            final String message = String.format("Disabling %s", plugin.getDescription().getFullName());
            plugin.getLogger().info(message);

            server.getPluginManager().callEvent(new PluginDisableEvent(plugin));

            final JavaPlugin jPlugin = (JavaPlugin) plugin;
            final ClassLoader cloader = jPlugin.getClassLoader();

            try {
                jPlugin.setEnabled(false);
            } catch (final Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            loaders0.remove(jPlugin.getDescription().getName());

            if (cloader instanceof PluginClassLoader) {
                final PluginClassLoader loader = (PluginClassLoader) cloader;
                final Set<String> names = loader.extended ? loader.getClasses() : loader.getClasses0(); // Don't warn on deprecation, but maintain overridability

                for (final String name : names) {
                    if (extended) {
                        removeClass(name);
                    } else {
                        removeClass0(name);
                    }
                }
            }
        }
    }

    // Cauldron start
    private InheritanceMap globalInheritanceMap = null;

    /**
     * Get the inheritance map for remapping all plugins
     */
    public InheritanceMap getGlobalInheritanceMap() {
        if (globalInheritanceMap == null) {
            final Map<String, String> relocationsCurrent = new HashMap<String, String>();
            relocationsCurrent.put("net.minecraft.server", "net.minecraft.server." + PluginClassLoader.getNativeVersion());
            final JarMapping currentMappings = new JarMapping();

            try {
                currentMappings.loadMappings(
                        new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("mappings/" + PluginClassLoader.getNativeVersion() + "/cb2numpkg.srg"))),
                        new MavenShade(relocationsCurrent),
                        null, false);
            } catch (final IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }

            final BiMap<String, String> inverseClassMap = HashBiMap.create(currentMappings.classes).inverse();
            globalInheritanceMap = new InheritanceMap();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("mappings/" + PluginClassLoader.getNativeVersion() + "/nms.inheritmap")));

            try {
                globalInheritanceMap.load(reader, inverseClassMap);
            } catch (final IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
            System.out.println("Loaded inheritance map of " + globalInheritanceMap.size() + " classes");
        }

        return globalInheritanceMap;
    }
    // Cauldron end
}
