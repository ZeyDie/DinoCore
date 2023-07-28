package org.bukkit.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;

import java.util.*;

/**
 * A simple services manager.
 */
public class SimpleServicesManager implements ServicesManager {

    /**
     * Map of providers.
     */
    private final Map<Class<?>, List<RegisteredServiceProvider<?>>> providers = new HashMap<Class<?>, List<RegisteredServiceProvider<?>>>();

    /**
     * Register a provider of a service.
     *
     * @param <T> Provider
     * @param service service class
     * @param provider provider to register
     * @param plugin plugin with the provider
     * @param priority priority of the provider
     */
    public <T> void register(final Class<T> service, final T provider, final Plugin plugin, final ServicePriority priority) {
        RegisteredServiceProvider<T> registeredProvider = null;
        synchronized (providers) {
            List<RegisteredServiceProvider<?>> registered = providers.get(service);
            if (registered == null) {
                registered = new ArrayList<RegisteredServiceProvider<?>>();
                providers.put(service, registered);
            }

            registeredProvider = new RegisteredServiceProvider<T>(service, provider, priority, plugin);

            // Insert the provider into the collection, much more efficient big O than sort
            final int position = Collections.binarySearch(registered, registeredProvider);
            if (position < 0) {
                registered.add(-(position + 1), registeredProvider);
            } else {
                registered.add(position, registeredProvider);
            }

        }
        Bukkit.getServer().getPluginManager().callEvent(new ServiceRegisterEvent(registeredProvider));
    }

    /**
     * Unregister all the providers registered by a particular plugin.
     *
     * @param plugin The plugin
     */
    public void unregisterAll(final Plugin plugin) {
        final ArrayList<ServiceUnregisterEvent> unregisteredEvents = new ArrayList<ServiceUnregisterEvent>();
        synchronized (providers) {
            final Iterator<Map.Entry<Class<?>, List<RegisteredServiceProvider<?>>>> it = providers.entrySet().iterator();

            try {
                while (it.hasNext()) {
                    final Map.Entry<Class<?>, List<RegisteredServiceProvider<?>>> entry = it.next();
                    final Iterator<RegisteredServiceProvider<?>> it2 = entry.getValue().iterator();

                    try {
                        // Removed entries that are from this plugin

                        while (it2.hasNext()) {
                            final RegisteredServiceProvider<?> registered = it2.next();

                            final Plugin oPlugin = registered.getPlugin();
                            if (oPlugin != null ? oPlugin.equals(plugin) : plugin == null) {
                                it2.remove();
                                unregisteredEvents.add(new ServiceUnregisterEvent(registered));
                            }
                        }
                    } catch (final NoSuchElementException e) { // Why does Java suck
                    }

                    // Get rid of the empty list
                    if (entry.getValue().isEmpty()) {
                        it.remove();
                    }
                }
            } catch (final NoSuchElementException e) {}
        }
        for (final ServiceUnregisterEvent event : unregisteredEvents) {
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }

    /**
     * Unregister a particular provider for a particular service.
     *
     * @param service The service interface
     * @param provider The service provider implementation
     */
    public void unregister(final Class<?> service, final Object provider) {
        final ArrayList<ServiceUnregisterEvent> unregisteredEvents = new ArrayList<ServiceUnregisterEvent>();
        synchronized (providers) {
            final Iterator<Map.Entry<Class<?>, List<RegisteredServiceProvider<?>>>> it = providers.entrySet().iterator();

            try {
                while (it.hasNext()) {
                    final Map.Entry<Class<?>, List<RegisteredServiceProvider<?>>> entry = it.next();

                    // We want a particular service
                    if (entry.getKey() != service) {
                        continue;
                    }

                    final Iterator<RegisteredServiceProvider<?>> it2 = entry.getValue().iterator();

                    try {
                        // Removed entries that are from this plugin

                        while (it2.hasNext()) {
                            final RegisteredServiceProvider<?> registered = it2.next();

                            if (registered.getProvider() == provider) {
                                it2.remove();
                                unregisteredEvents.add(new ServiceUnregisterEvent(registered));
                            }
                        }
                    } catch (final NoSuchElementException e) { // Why does Java suck
                    }

                    // Get rid of the empty list
                    if (entry.getValue().isEmpty()) {
                        it.remove();
                    }
                }
            } catch (final NoSuchElementException e) {}
        }
        for (final ServiceUnregisterEvent event : unregisteredEvents) {
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }

    /**
     * Unregister a particular provider.
     *
     * @param provider The service provider implementation
     */
    public void unregister(final Object provider) {
        final ArrayList<ServiceUnregisterEvent> unregisteredEvents = new ArrayList<ServiceUnregisterEvent>();
        synchronized (providers) {
            final Iterator<Map.Entry<Class<?>, List<RegisteredServiceProvider<?>>>> it = providers.entrySet().iterator();

            try {
                while (it.hasNext()) {
                    final Map.Entry<Class<?>, List<RegisteredServiceProvider<?>>> entry = it.next();
                    final Iterator<RegisteredServiceProvider<?>> it2 = entry.getValue().iterator();

                    try {
                        // Removed entries that are from this plugin

                        while (it2.hasNext()) {
                            final RegisteredServiceProvider<?> registered = it2.next();

                            if (registered.getProvider().equals(provider)) {
                                it2.remove();
                                unregisteredEvents.add(new ServiceUnregisterEvent(registered));
                            }
                        }
                    } catch (final NoSuchElementException e) { // Why does Java suck
                    }

                    // Get rid of the empty list
                    if (entry.getValue().isEmpty()) {
                        it.remove();
                    }
                }
            } catch (final NoSuchElementException e) {}
        }
        for (final ServiceUnregisterEvent event : unregisteredEvents) {
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }

    /**
     * Queries for a provider. This may return if no provider has been
     * registered for a service. The highest priority provider is returned.
     *
     * @param <T> The service interface
     * @param service The service interface
     * @return provider or null
     */
    public <T> T load(final Class<T> service) {
        synchronized (providers) {
            final List<RegisteredServiceProvider<?>> registered = providers.get(service);

            if (registered == null) {
                return null;
            }

            // This should not be null!
            return service.cast(registered.get(0).getProvider());
        }
    }

    /**
     * Queries for a provider registration. This may return if no provider
     * has been registered for a service.
     *
     * @param <T> The service interface
     * @param service The service interface
     * @return provider registration or null
     */
    @SuppressWarnings("unchecked")
    public <T> RegisteredServiceProvider<T> getRegistration(final Class<T> service) {
        synchronized (providers) {
            final List<RegisteredServiceProvider<?>> registered = providers.get(service);

            if (registered == null) {
                return null;
            }

            // This should not be null!
            return (RegisteredServiceProvider<T>) registered.get(0);
        }
    }

    /**
     * Get registrations of providers for a plugin.
     *
     * @param plugin The plugin
     * @return provider registration or null
     */
    public List<RegisteredServiceProvider<?>> getRegistrations(final Plugin plugin) {
        final ImmutableList.Builder<RegisteredServiceProvider<?>> ret = ImmutableList.<RegisteredServiceProvider<?>>builder();
        synchronized (providers) {
            for (final List<RegisteredServiceProvider<?>> registered : providers.values()) {
                for (final RegisteredServiceProvider<?> provider : registered) {
                    if (provider.getPlugin().equals(plugin)) {
                        ret.add(provider);
                    }
                }
            }
        }
        return ret.build();
    }

    /**
     * Get registrations of providers for a service. The returned list is
     * an unmodifiable copy.
     *
     * @param <T> The service interface
     * @param service The service interface
     * @return a copy of the list of registrations
     */
    @SuppressWarnings("unchecked")
    public <T> List<RegisteredServiceProvider<T>> getRegistrations(final Class<T> service) {
        final ImmutableList.Builder<RegisteredServiceProvider<T>> ret;
        synchronized (providers) {
            final List<RegisteredServiceProvider<?>> registered = providers.get(service);

            if (registered == null) {
                return ImmutableList.<RegisteredServiceProvider<T>>of();
            }

            ret = ImmutableList.<RegisteredServiceProvider<T>>builder();

            for (final RegisteredServiceProvider<?> provider : registered) {
                ret.add((RegisteredServiceProvider<T>) provider);
            }

        }
        return ret.build();
    }

    /**
     * Get a list of known services. A service is known if it has registered
     * providers for it.
     *
     * @return a copy of the set of known services
     */
    public Set<Class<?>> getKnownServices() {
        synchronized (providers) {
            return ImmutableSet.<Class<?>>copyOf(providers.keySet());
        }
    }

    /**
     * Returns whether a provider has been registered for a service.
     *
     * @param <T> service
     * @param service service to check
     * @return true if and only if there are registered providers
     */
    public <T> boolean isProvidedFor(final Class<T> service) {
        synchronized (providers) {
            return providers.containsKey(service);
        }
    }
}
