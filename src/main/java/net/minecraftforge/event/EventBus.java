package net.minecraftforge.event;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus
{
    private static int maxID = 0;
    
    private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = new ConcurrentHashMap<Object, ArrayList<IEventListener>>();
    private final int busID = maxID++;

    public EventBus()
    {
        ListenerList.resize(busID + 1);
    }
    
    public void register(final Object target)
    {
        if (listeners.containsKey(target))
        {
            return;
        }

        final Set<? extends Class<?>> supers = TypeToken.of(target.getClass()).getTypes().rawTypes();
        for (final Method method : target.getClass().getMethods())
        {
            for (final Class<?> cls : supers)
            {
                try
                {
                    final Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    if (real.isAnnotationPresent(ForgeSubscribe.class))
                    {
                        final Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length != 1)
                        {
                            throw new IllegalArgumentException(
                                "Method " + method + " has @ForgeSubscribe annotation, but requires " + parameterTypes.length +
                                " arguments.  Event handler methods must require a single argument."
                            );
                        }
                        
                        final Class<?> eventType = parameterTypes[0];
                        
                        if (!Event.class.isAssignableFrom(eventType))
                        {
                            throw new IllegalArgumentException("Method " + method + " has @ForgeSubscribe annotation, but takes a argument that is not a Event " + eventType); 
                        }
                                                
                        register(eventType, target, method);
                        break;
                    }
                }
                catch (final NoSuchMethodException e)
                {
                    ;
                }
            }
        }
    }

    private void register(final Class<?> eventType, final Object target, final Method method)
    {
        try
        {
            final Constructor<?> ctr = eventType.getConstructor();
            ctr.setAccessible(true);
            final Event event = (Event)ctr.newInstance();
            final ASMEventHandler listener = new ASMEventHandler(target, method);
            event.getListenerList().register(busID, listener.getPriority(), listener);

            ArrayList<IEventListener> others = listeners.get(target); 
            if (others == null)
            {
                others = new ArrayList<IEventListener>();
                listeners.put(target, others);
            }
            others.add(listener);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    public void unregister(final Object object)
    {
        final ArrayList<IEventListener> list = listeners.remove(object);
        for (final IEventListener listener : list)
        {
            ListenerList.unregiterAll(busID, listener);
        }
    }
    
    public boolean post(final Event event)
    {
        final IEventListener[] listeners = event.getListenerList().getListeners(busID);
        for (final IEventListener listener : listeners)
        {
            listener.invoke(event);
        }
        return (event.isCancelable() ? event.isCanceled() : false);
    }
}
