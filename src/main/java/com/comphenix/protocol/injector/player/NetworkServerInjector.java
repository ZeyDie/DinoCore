package com.comphenix.protocol.injector.player;

import com.comphenix.net.sf.cglib.proxy.Callback;
import com.comphenix.net.sf.cglib.proxy.CallbackFilter;
import com.comphenix.net.sf.cglib.proxy.Enhancer;
import com.comphenix.net.sf.cglib.proxy.Factory;
import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
import com.comphenix.net.sf.cglib.proxy.MethodProxy;
import com.comphenix.net.sf.cglib.proxy.NoOp;
import com.comphenix.protocol.concurrency.IntegerSet;
import com.comphenix.protocol.error.ErrorReporter;
import com.comphenix.protocol.error.Report;
import com.comphenix.protocol.error.ReportType;
import com.comphenix.protocol.events.NetworkMarker;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.injector.ListenerInvoker;
import com.comphenix.protocol.injector.PacketFilterManager;
import com.comphenix.protocol.injector.PacketFilterManager.PlayerInjectHooks;
import com.comphenix.protocol.reflect.FieldUtils;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.ObjectWriter;
import com.comphenix.protocol.reflect.VolatileField;
import com.comphenix.protocol.reflect.instances.DefaultInstances;
import com.comphenix.protocol.reflect.instances.ExistingGenerator;
import com.comphenix.protocol.reflect.instances.InstanceProvider;
import com.comphenix.protocol.utility.EnhancerFactory;
import com.comphenix.protocol.utility.MinecraftMethods;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.utility.MinecraftVersion;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.bukkit.entity.Player;

class NetworkServerInjector extends PlayerInjector {
    public static final ReportType REPORT_ASSUMING_DISCONNECT_FIELD = new ReportType("Unable to find 'disconnected' field. Assuming %s.");
    public static final ReportType REPORT_DISCONNECT_FIELD_MISSING = new ReportType("Cannot find disconnected field. Is ProtocolLib up to date?");
    public static final ReportType REPORT_DISCONNECT_FIELD_FAILURE = new ReportType("Unable to update disconnected field. Player quit event may be sent twice.");
    private static volatile CallbackFilter callbackFilter;
    private static volatile boolean foundSendPacket;
    private static volatile Field disconnectField;
    private InjectedServerConnection serverInjection;
    private IntegerSet sendingFilters;
    private boolean hasDisconnected;
    private final ObjectWriter writer = new ObjectWriter();

    public NetworkServerInjector(ErrorReporter reporter, Player player, ListenerInvoker invoker, IntegerSet sendingFilters, InjectedServerConnection serverInjection) throws IllegalAccessException {
        super(reporter, player, invoker);
        this.sendingFilters = sendingFilters;
        this.serverInjection = serverInjection;
    }

    protected boolean hasListener(int packetID) {
        return this.sendingFilters.contains(packetID);
    }

    public void sendServerPacket(Object packet, NetworkMarker marker, boolean filtered) throws InvocationTargetException {
        Object serverDelegate = filtered ? this.serverHandlerRef.getValue() : this.serverHandlerRef.getOldValue();
        if (serverDelegate != null) {
            try {
                if (marker != null) {
                    this.queuedMarkers.put(packet, marker);
                }

                MinecraftMethods.getSendPacketMethod().invoke(serverDelegate, packet);
            } catch (IllegalArgumentException var6) {
                throw var6;
            } catch (InvocationTargetException var7) {
                throw var7;
            } catch (IllegalAccessException var8) {
                throw new IllegalStateException("Unable to access send packet method.", var8);
            }
        } else {
            throw new IllegalStateException("Unable to load server handler. Cannot send packet.");
        }
    }

    public void injectManager() {
        if (this.serverHandlerRef == null) {
            throw new IllegalStateException("Cannot find server handler.");
        } else if (!(this.serverHandlerRef.getValue() instanceof Factory)) {
            if (!this.tryInjectManager()) {
                Class<?> serverHandlerClass = MinecraftReflection.getNetServerHandlerClass();
                if (proxyServerField != null) {
                    this.serverHandlerRef = new VolatileField(proxyServerField, this.serverHandler, true);
                    this.serverHandler = this.serverHandlerRef.getValue();
                    if (this.serverHandler == null) {
                        throw new RuntimeException("Cannot hook player: Inner proxy object is NULL.");
                    }

                    serverHandlerClass = this.serverHandler.getClass();
                    if (this.tryInjectManager()) {
                        return;
                    }
                }

                throw new RuntimeException("Cannot hook player: Unable to find a valid constructor for the " + serverHandlerClass.getName() + " object.");
            }
        }
    }

    private boolean tryInjectManager() {
        Class<?> serverClass = this.serverHandler.getClass();
        Enhancer ex = EnhancerFactory.getInstance().createEnhancer();
        Callback sendPacketCallback = new MethodInterceptor() {
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                Object packet = args[0];
                if (packet != null) {
                    packet = NetworkServerInjector.this.handlePacketSending(packet);
                    if (packet == null) {
                        return null;
                    }

                    args[0] = packet;
                }

                return proxy.invokeSuper(obj, args);
            }
        };
        Callback noOpCallback = NoOp.INSTANCE;
        if (callbackFilter == null) {
            callbackFilter = new SendMethodFilter();
        }

        ex.setSuperclass(serverClass);
        ex.setCallbacks(new Callback[]{sendPacketCallback, noOpCallback});
        ex.setCallbackFilter(callbackFilter);
        Class<?> minecraftSuperClass = this.getFirstMinecraftSuperClass(this.serverHandler.getClass());
        ExistingGenerator generator = ExistingGenerator.fromObjectFields(this.serverHandler, minecraftSuperClass);
        DefaultInstances serverInstances = null;
        Object proxyInstance = this.getProxyServerHandler();
        if (proxyInstance != null && proxyInstance != this.serverHandler) {
            serverInstances = DefaultInstances.fromArray(new InstanceProvider[]{generator, ExistingGenerator.fromObjectArray(new Object[]{proxyInstance})});
        } else {
            serverInstances = DefaultInstances.fromArray(new InstanceProvider[]{generator});
        }

        serverInstances.setNonNull(true);
        serverInstances.setMaximumRecursion(1);
        Object proxyObject = serverInstances.forEnhancer(ex).getDefault(serverClass);
        if (proxyObject != null) {
            if (!foundSendPacket) {
                throw new IllegalArgumentException("Unable to find a sendPacket method in " + serverClass);
            } else {
                this.serverInjection.replaceServerHandler(this.serverHandler, proxyObject);
                this.serverHandlerRef.setValue(proxyObject);
                return true;
            }
        } else {
            return false;
        }
    }

    private Object getProxyServerHandler() {
        if (proxyServerField != null && !proxyServerField.equals(this.serverHandlerRef.getField())) {
            try {
                return FieldUtils.readField(proxyServerField, this.serverHandler, true);
            } catch (OutOfMemoryError var2) {
                throw var2;
            } catch (ThreadDeath var3) {
                throw var3;
            } catch (Throwable var4) {
            }
        }

        return null;
    }

    private Class<?> getFirstMinecraftSuperClass(Class<?> clazz) {
        if (MinecraftReflection.isMinecraftClass(clazz)) {
            return clazz;
        } else {
            return clazz.equals(Object.class) ? clazz : this.getFirstMinecraftSuperClass(clazz.getSuperclass());
        }
    }

    protected void cleanHook() {
        if (this.serverHandlerRef != null && this.serverHandlerRef.isCurrentSet()) {
            this.writer.copyTo(this.serverHandlerRef.getValue(), this.serverHandlerRef.getOldValue(), this.serverHandler.getClass());
            this.serverHandlerRef.revertValue();

            try {
                if (this.getNetHandler() != null) {
                    try {
                        FieldUtils.writeField(netHandlerField, this.networkManager, this.serverHandlerRef.getOldValue(), true);
                    } catch (IllegalAccessException var2) {
                        var2.printStackTrace();
                    }
                }
            } catch (IllegalAccessException var3) {
                var3.printStackTrace();
            }

            if (this.hasDisconnected) {
                this.setDisconnect(this.serverHandlerRef.getValue(), true);
            }
        }

        this.serverInjection.revertServerHandler(this.serverHandler);
    }

    public void handleDisconnect() {
        this.hasDisconnected = true;
    }

    private void setDisconnect(Object handler, boolean value) {
        try {
            if (disconnectField == null) {
                disconnectField = FuzzyReflection.fromObject(handler).getFieldByName("disconnected.*");
            }

            FieldUtils.writeField(disconnectField, handler, value);
        } catch (IllegalArgumentException var4) {
            if (disconnectField == null) {
                disconnectField = FuzzyReflection.fromObject(handler).getFieldByType("disconnected", Boolean.TYPE);
                this.reporter.reportWarning(this, Report.newBuilder(REPORT_ASSUMING_DISCONNECT_FIELD).messageParam(new Object[]{disconnectField}));
                if (disconnectField != null) {
                    this.setDisconnect(handler, value);
                    return;
                }
            }

            this.reporter.reportDetailed(this, Report.newBuilder(REPORT_DISCONNECT_FIELD_MISSING).error(var4));
        } catch (IllegalAccessException var5) {
            this.reporter.reportWarning(this, Report.newBuilder(REPORT_DISCONNECT_FIELD_FAILURE).error(var5));
        }

    }

    public UnsupportedListener checkListener(MinecraftVersion version, PacketListener listener) {
        return null;
    }

    public boolean canInject(GamePhase phase) {
        return phase == GamePhase.PLAYING;
    }

    public PacketFilterManager.PlayerInjectHooks getHookType() {
        return PlayerInjectHooks.NETWORK_SERVER_OBJECT;
    }

    private static class SendMethodFilter implements CallbackFilter {
        private Method sendPacket;

        private SendMethodFilter() {
            this.sendPacket = MinecraftMethods.getSendPacketMethod();
        }

        public int accept(Method method) {
            if (this.isCallableEqual(this.sendPacket, method)) {
                NetworkServerInjector.foundSendPacket = true;
                return 0;
            } else {
                return 1;
            }
        }

        private boolean isCallableEqual(Method first, Method second) {
            return first.getName().equals(second.getName()) && first.getReturnType().equals(second.getReturnType()) && Arrays.equals(first.getParameterTypes(), second.getParameterTypes());
        }
    }
}
