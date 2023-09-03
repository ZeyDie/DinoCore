package com.comphenix.protocol.utility;

import com.comphenix.net.sf.cglib.asm.ClassReader;
import com.comphenix.net.sf.cglib.asm.MethodVisitor;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.error.ErrorReporter;
import com.comphenix.protocol.error.Report;
import com.comphenix.protocol.error.ReportType;
import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.injector.packet.PacketRegistry;
import com.comphenix.protocol.reflect.ClassAnalyser;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.MethodAccessor;
import com.comphenix.protocol.reflect.compiler.EmptyClassVisitor;
import com.comphenix.protocol.reflect.compiler.EmptyMethodVisitor;
import com.comphenix.protocol.reflect.fuzzy.*;
import com.comphenix.protocol.utility.RemappedClassSource.RemapperUnavaibleException.Reason;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtType;
import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftReflection {
    public static final ReportType REPORT_CANNOT_FIND_MCPC_REMAPPER = new ReportType("Cannot find MCPC remapper.");
    public static final ReportType REPORT_CANNOT_LOAD_CPC_REMAPPER = new ReportType("Unable to load MCPC remapper.");
    public static final ReportType REPORT_NON_CRAFTBUKKIT_LIBRARY_PACKAGE = new ReportType("Cannot find standard Minecraft library location. Assuming MCPC.");
    /**
     * @deprecated
     */
    @Deprecated
    public static final String MINECRAFT_OBJECT = "net\\.minecraft(\\.\\w+)+";
    private static String DYNAMIC_PACKAGE_MATCHER = null;
    private static final String FORGE_ENTITY_PACKAGE = "net.minecraft.entity";
    private static String MINECRAFT_PREFIX_PACKAGE = "net.minecraft.server";
    //TODO ZeyCodeReplace net.minecraft.util on empty
    private static String MINECRAFT_LIBRARY_PACKAGE = "";
    private static final Pattern PACKAGE_VERSION_MATCHER = Pattern.compile(".*\\.(v\\d+_\\d+_\\w*\\d+)");
    private static String MINECRAFT_FULL_PACKAGE = null;
    private static String CRAFTBUKKIT_PACKAGE = null;
    static CachedPackage minecraftPackage;
    static CachedPackage craftbukkitPackage;
    static CachedPackage libraryPackage;
    private static Constructor<?> craftNMSConstructor;
    private static Constructor<?> craftBukkitConstructor;
    private static AbstractFuzzyMatcher<Class<?>> fuzzyMatcher;
    private static Method craftNMSMethod;
    private static Method craftBukkitNMS;
    private static Method craftBukkitOBC;
    private static boolean craftItemStackFailed;
    private static String packageVersion;
    private static Class<?> itemStackArrayClass;
    private static Cache<Class<?>, MethodAccessor> getBukkitEntityCache = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, MethodAccessor>() {
        public MethodAccessor load(Class<?> paramK) throws Exception {
            return Accessors.getMethodAccessor(paramK, "getBukkitEntity", new Class[0]);
        }
    });
    private static ClassSource classSource;
    private static boolean initializing;
    private static Boolean cachedNetty;

    private MinecraftReflection() {
    }

    public static String getMinecraftObjectRegex() {
        if (DYNAMIC_PACKAGE_MATCHER == null) {
            getMinecraftPackage();
        }

        return DYNAMIC_PACKAGE_MATCHER;
    }

    public static AbstractFuzzyMatcher<Class<?>> getMinecraftObjectMatcher() {
        if (fuzzyMatcher == null) {
            fuzzyMatcher = FuzzyMatchers.matchRegex(getMinecraftObjectRegex(), 50);
        }

        return fuzzyMatcher;
    }

    public static String getMinecraftPackage() {
        if (MINECRAFT_FULL_PACKAGE != null) {
            return MINECRAFT_FULL_PACKAGE;
        } else if (initializing) {
            throw new IllegalStateException("Already initializing minecraft package!");
        } else {
            initializing = true;
            Server craftServer = Bukkit.getServer();
            if (craftServer == null) {
                initializing = false;
                throw new IllegalStateException("Could not find Bukkit. Is it running?");
            } else {
                String matcher;
                try {
                    Class<?> craftClass = craftServer.getClass();
                    CRAFTBUKKIT_PACKAGE = getPackage(craftClass.getCanonicalName());
                    Matcher packageMatcher = PACKAGE_VERSION_MATCHER.matcher(CRAFTBUKKIT_PACKAGE);
                    if (packageMatcher.matches()) {
                        packageVersion = packageMatcher.group(1);
                    }

                    handleLibigot();
                    handleLibraryPackage();
                    Class<?> craftEntity = getCraftEntityClass();
                    Method getHandle = craftEntity.getMethod("getHandle");
                    MINECRAFT_FULL_PACKAGE = getPackage(getHandle.getReturnType().getCanonicalName());
                    if (!MINECRAFT_FULL_PACKAGE.startsWith(MINECRAFT_PREFIX_PACKAGE)) {
                        if (MINECRAFT_FULL_PACKAGE.equals("net.minecraft.entity")) {
                            MINECRAFT_FULL_PACKAGE = CachedPackage.combine(MINECRAFT_PREFIX_PACKAGE, packageVersion);
                        } else {
                            MINECRAFT_PREFIX_PACKAGE = MINECRAFT_FULL_PACKAGE;
                        }

                        matcher = (MINECRAFT_PREFIX_PACKAGE.length() > 0 ? Pattern.quote(MINECRAFT_PREFIX_PACKAGE + ".") : "") + "\\w+";
                        setDynamicPackageMatcher("(" + matcher + ")|(" + "net\\.minecraft(\\.\\w+)+" + ")");
                    } else {
                        setDynamicPackageMatcher("net\\.minecraft(\\.\\w+)+");
                    }

                    matcher = MINECRAFT_FULL_PACKAGE;
                } catch (SecurityException var10) {
                    throw new RuntimeException("Security violation. Cannot get handle method.", var10);
                } catch (NoSuchMethodException var11) {
                    throw new IllegalStateException("Cannot find getHandle() method on server. Is this a modified CraftBukkit version?", var11);
                } finally {
                    initializing = false;
                }

                return matcher;
            }
        }
    }

    private static String getMinecraftLibraryPackage() {
        getMinecraftPackage();
        return MINECRAFT_LIBRARY_PACKAGE;
    }

    private static void handleLibraryPackage() {
        try {
            //TODO ZeyCodeClear
            //MINECRAFT_LIBRARY_PACKAGE = "net.minecraft.util";
            getClassSource().loadClass(CachedPackage.combine(MINECRAFT_LIBRARY_PACKAGE, "com.google.gson.Gson"));
        } catch (Exception var1) {
            MINECRAFT_LIBRARY_PACKAGE = "";
            ProtocolLibrary.getErrorReporter().reportWarning(MinecraftReflection.class, Report.newBuilder(REPORT_NON_CRAFTBUKKIT_LIBRARY_PACKAGE));
        }

    }

    public static String getPackageVersion() {
        getMinecraftPackage();
        return packageVersion;
    }

    private static void setDynamicPackageMatcher(String regex) {
        DYNAMIC_PACKAGE_MATCHER = regex;
        fuzzyMatcher = null;
    }

    private static void handleLibigot() {
        try {
            getCraftEntityClass();
        } catch (RuntimeException var1) {
            craftbukkitPackage = null;
            CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit";
            getCraftEntityClass();
        }

    }

    public static void setMinecraftPackage(String minecraftPackage, String craftBukkitPackage) {
        MINECRAFT_FULL_PACKAGE = minecraftPackage;
        CRAFTBUKKIT_PACKAGE = craftBukkitPackage;
        if (getMinecraftServerClass() == null) {
            throw new IllegalArgumentException("Cannot find MinecraftServer for package " + minecraftPackage);
        } else {
            setDynamicPackageMatcher("net\\.minecraft(\\.\\w+)+");
        }
    }

    public static String getCraftBukkitPackage() {
        if (CRAFTBUKKIT_PACKAGE == null) {
            getMinecraftPackage();
        }

        return CRAFTBUKKIT_PACKAGE;
    }

    private static String getPackage(String fullName) {
        int index = fullName.lastIndexOf(".");
        return index > 0 ? fullName.substring(0, index) : "";
    }

    public static Object getBukkitEntity(Object nmsObject) {
        if (nmsObject == null) {
            return null;
        } else {
            try {
                return getBukkitEntityCache.getIfPresent(nmsObject.getClass()).invoke(nmsObject, new Object[0]);
            } catch (Exception var2) {
                throw new IllegalArgumentException("Cannot get Bukkit entity from " + nmsObject, var2);
            }
        }
    }

    public static boolean isMinecraftObject(@NotNull Object obj) {
        return obj == null ? false : obj.getClass().getName().startsWith(MINECRAFT_PREFIX_PACKAGE);
    }

    public static boolean isMinecraftClass(@NotNull Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be NULL.");
        } else {
            return getMinecraftObjectMatcher().isMatch(clazz, (Object) null);
        }
    }

    public static boolean isMinecraftObject(@NotNull Object obj, String className) {
        if (obj == null) {
            return false;
        } else {
            String javaName = obj.getClass().getName();
            return javaName.startsWith(MINECRAFT_PREFIX_PACKAGE) && javaName.endsWith(className);
        }
    }

    public static boolean isChunkPosition(Object obj) {
        return obj != null && getChunkPositionClass().isAssignableFrom(obj.getClass());
    }

    public static boolean isChunkCoordIntPair(Object obj) {
        return obj != null && getChunkCoordIntPair().isAssignableFrom(obj.getClass());
    }

    public static boolean isChunkCoordinates(Object obj) {
        return obj != null && getChunkCoordinatesClass().isAssignableFrom(obj.getClass());
    }

    public static boolean isPacketClass(Object obj) {
        return obj != null && getPacketClass().isAssignableFrom(obj.getClass());
    }

    public static boolean isLoginHandler(Object obj) {
        return obj != null && getNetLoginHandlerClass().isAssignableFrom(obj.getClass());
    }

    public static boolean isServerHandler(Object obj) {
        return obj != null && getNetServerHandlerClass().isAssignableFrom(obj.getClass());
    }

    public static boolean isMinecraftEntity(Object obj) {
        return obj != null && getEntityClass().isAssignableFrom(obj.getClass());
    }

    public static boolean isItemStack(Object value) {
        return value != null && getItemStackClass().isAssignableFrom(value.getClass());
    }

    public static boolean isCraftPlayer(Object value) {
        return value != null && getCraftPlayerClass().isAssignableFrom(value.getClass());
    }

    public static boolean isMinecraftPlayer(Object obj) {
        return obj != null && getEntityPlayerClass().isAssignableFrom(obj.getClass());
    }

    public static boolean isWatchableObject(Object obj) {
        return obj != null && getWatchableObjectClass().isAssignableFrom(obj.getClass());
    }

    public static boolean isDataWatcher(Object obj) {
        return obj != null && getDataWatcherClass().isAssignableFrom(obj.getClass());
    }

    public static boolean isIntHashMap(Object obj) {
        return obj != null && getIntHashMapClass().isAssignableFrom(obj.getClass());
    }

    public static boolean isCraftItemStack(Object obj) {
        return obj != null && getCraftItemStackClass().isAssignableFrom(obj.getClass());
    }

    public static Class<?> getEntityPlayerClass() {
        try {
            return getMinecraftClass("EntityPlayer");
        } catch (RuntimeException var3) {
            try {
                Method detect = FuzzyReflection.fromClass(getCraftBukkitClass("CraftServer")).getMethodByName("detectListNameConflict");
                return detect.getParameterTypes()[0];
            } catch (IllegalArgumentException var2) {
                return fallbackMethodReturn("EntityPlayer", "entity.CraftPlayer", "getHandle");
            }
        }
    }

    public static Class<?> getGameProfileClass() {
        if (!isUsingNetty()) {
            throw new IllegalStateException("GameProfile does not exist in version 1.6.4 and earlier.");
        } else {
            return GameProfile.class;
        }
    }

    public static Class<?> getEntityClass() {
        try {
            return getMinecraftClass("Entity");
        } catch (RuntimeException var1) {
            return fallbackMethodReturn("Entity", "entity.CraftEntity", "getHandle");
        }
    }

    public static Class<?> getCraftChatMessage() {
        return getCraftBukkitClass("util.CraftChatMessage");
    }

    public static Class<?> getWorldServerClass() {
        try {
            return getMinecraftClass("WorldServer");
        } catch (RuntimeException var1) {
            return fallbackMethodReturn("WorldServer", "CraftWorld", "getHandle");
        }
    }

    private static Class<?> fallbackMethodReturn(String nmsClass, String craftClass, String methodName) {
        Class<?> result = FuzzyReflection.fromClass(getCraftBukkitClass(craftClass)).getMethodByName(methodName).getReturnType();
        return setMinecraftClass(nmsClass, result);
    }

    public static Class<?> getPacketClass() {
        try {
            return getMinecraftClass("Packet");
        } catch (RuntimeException var4) {
            FuzzyClassContract paketContract = null;
            if (isUsingNetty()) {
                paketContract = FuzzyClassContract.newBuilder().method(FuzzyMethodContract.newBuilder().parameterDerivedOf(ByteBuf.class).returnTypeVoid()).method(FuzzyMethodContract.newBuilder().parameterDerivedOf(ByteBuf.class, 0).parameterExactType(byte[].class, 1).returnTypeVoid()).build();
            } else {
                paketContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeDerivedOf(Map.class).requireModifier(8)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(Set.class).requireModifier(8)).method(FuzzyMethodContract.newBuilder().parameterSuperOf(DataInputStream.class).returnTypeVoid()).build();
            }

            Method selected = FuzzyReflection.fromClass(getNetServerHandlerClass()).getMethod(FuzzyMethodContract.newBuilder().parameterMatches(paketContract, 0).parameterCount(1).build());
            Class<?> clazz = getTopmostClass(selected.getParameterTypes()[0]);
            return setMinecraftClass("Packet", clazz);
        }
    }

    public static Class<?> getEnumProtocolClass() {
        try {
            return getMinecraftClass("EnumProtocol");
        } catch (RuntimeException var2) {
            Method protocolMethod = FuzzyReflection.fromClass(getNetworkManagerClass()).getMethod(FuzzyMethodContract.newBuilder().parameterCount(1).parameterDerivedOf(Enum.class, 0).build());
            return setMinecraftClass("EnumProtocol", protocolMethod.getParameterTypes()[0]);
        }
    }

    public static Class<?> getIChatBaseComponentClass() {
        try {
            return getMinecraftClass("IChatBaseComponent");
        } catch (RuntimeException var1) {
            return setMinecraftClass("IChatBaseComponent", Accessors.getMethodAccessor(getCraftChatMessage(), "fromString", new Class[]{String.class}).getMethod().getReturnType().getComponentType());
        }
    }

    public static Class<?> getChatComponentTextClass() {
        try {
            return getMinecraftClass("ChatComponentText");
        } catch (RuntimeException var7) {
            try {
                Method getScoreboardDisplayName = FuzzyReflection.fromClass(getEntityClass()).getMethodByParameters("getScoreboardDisplayName", getIChatBaseComponentClass(), new Class[0]);
                Class<?> baseClass = getIChatBaseComponentClass();
                Iterator i$ = ClassAnalyser.getDefault().getMethodCalls(getScoreboardDisplayName).iterator();

                while (i$.hasNext()) {
                    ClassAnalyser.AsmMethod method = (ClassAnalyser.AsmMethod) i$.next();
                    Class<?> owner = method.getOwnerClass();
                    if (isMinecraftClass(owner) && baseClass.isAssignableFrom(owner)) {
                        return setMinecraftClass("ChatComponentText", owner);
                    }
                }
            } catch (Exception var6) {
                throw new IllegalStateException("Cannot find ChatComponentText class.", var7);
            }

            throw new IllegalStateException("Cannot find ChatComponentText class.");
        }
    }

    public static Class<?> getChatSerializerClass() {
        try {
            return getMinecraftClass("ChatSerializer");
        } catch (RuntimeException var7) {
            try {
                List<ClassAnalyser.AsmMethod> methodCalls = ClassAnalyser.getDefault().getMethodCalls(com.comphenix.protocol.PacketType.Play.Server.CHAT.getPacketClass(), MinecraftMethods.getPacketReadByteBufMethod());
                Class<?> packetSerializer = getPacketDataSerializerClass();
                Iterator i$ = methodCalls.iterator();

                while (i$.hasNext()) {
                    ClassAnalyser.AsmMethod method = (ClassAnalyser.AsmMethod) i$.next();
                    Class<?> owner = method.getOwnerClass();
                    if (isMinecraftClass(owner) && !owner.equals(packetSerializer)) {
                        return setMinecraftClass("ChatSerializer", owner);
                    }
                }
            } catch (Exception var6) {
                throw new IllegalStateException("Cannot find ChatSerializer class.", var7);
            }

            throw new IllegalStateException("Cannot find ChatSerializer class.");
        }
    }

    public static Class<?> getServerPingClass() {
        if (!isUsingNetty()) {
            throw new IllegalStateException("ServerPing is only supported in 1.7.2.");
        } else {
            try {
                return getMinecraftClass("ServerPing");
            } catch (RuntimeException var3) {
                Class<?> statusServerInfo = com.comphenix.protocol.PacketType.Status.Server.OUT_SERVER_INFO.getPacketClass();
                AbstractFuzzyMatcher<Class<?>> serverPingContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeExact(String.class).build()).field(FuzzyFieldContract.newBuilder().typeDerivedOf(getIChatBaseComponentClass()).build()).build().and(getMinecraftObjectMatcher());
                return setMinecraftClass("ServerPing", FuzzyReflection.fromClass(statusServerInfo, true).getField(FuzzyFieldContract.matchType(serverPingContract)).getType());
            }
        }
    }

    public static Class<?> getServerPingServerDataClass() {
        if (!isUsingNetty()) {
            throw new IllegalStateException("ServerPingServerData is only supported in 1.7.2.");
        } else {
            try {
                return getMinecraftClass("ServerPingServerData");
            } catch (RuntimeException var3) {
                Class<?> serverPing = getServerPingClass();
                AbstractFuzzyMatcher<Class<?>> serverDataContract = FuzzyClassContract.newBuilder().constructor(FuzzyMethodContract.newBuilder().parameterExactArray(new Class[]{String.class, Integer.TYPE})).build().and(getMinecraftObjectMatcher());
                return setMinecraftClass("ServerPingServerData", getTypeFromField(serverPing, serverDataContract));
            }
        }
    }

    public static Class<?> getServerPingPlayerSampleClass() {
        if (!isUsingNetty()) {
            throw new IllegalStateException("ServerPingPlayerSample is only supported in 1.7.2.");
        } else {
            try {
                return getMinecraftClass("ServerPingPlayerSample");
            } catch (RuntimeException var3) {
                Class<?> serverPing = getServerPingClass();
                AbstractFuzzyMatcher<Class<?>> serverPlayerContract = FuzzyClassContract.newBuilder().constructor(FuzzyMethodContract.newBuilder().parameterExactArray(new Class[]{Integer.TYPE, Integer.TYPE})).field(FuzzyFieldContract.newBuilder().typeExact(GameProfile[].class)).build().and(getMinecraftObjectMatcher());
                return setMinecraftClass("ServerPingPlayerSample", getTypeFromField(serverPing, serverPlayerContract));
            }
        }
    }

    private static Class<?> getTypeFromField(Class<?> clazz, AbstractFuzzyMatcher<Class<?>> fieldTypeMatcher) {
        FuzzyFieldContract fieldMatcher = FuzzyFieldContract.matchType(fieldTypeMatcher);
        return FuzzyReflection.fromClass(clazz, true).getField(fieldMatcher).getType();
    }

    public static boolean isUsingNetty() {
        if (cachedNetty == null) {
            try {
                cachedNetty = getEnumProtocolClass() != null;
            } catch (RuntimeException var1) {
                cachedNetty = false;
            }
        }

        return cachedNetty;
    }

    private static Class<?> getTopmostClass(Class<?> clazz) {
        while (true) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == Object.class || superClass == null) {
                return clazz;
            }

            clazz = superClass;
        }
    }

    public static Class<?> getMinecraftServerClass() {
        try {
            return getMinecraftClass("MinecraftServer");
        } catch (RuntimeException var1) {
            useFallbackServer();
            return getMinecraftClass("MinecraftServer");
        }
    }

    public static Class<?> getStatisticClass() {
        return getMinecraftClass("Statistic");
    }

    public static Class<?> getStatisticListClass() {
        return getMinecraftClass("StatisticList");
    }

    private static void useFallbackServer() {
        Constructor<?> selected = FuzzyReflection.fromClass(getCraftBukkitClass("CraftServer")).getConstructor(FuzzyMethodContract.newBuilder().parameterMatches(getMinecraftObjectMatcher(), 0).parameterCount(2).build());
        Class<?>[] params = selected.getParameterTypes();
        setMinecraftClass("MinecraftServer", params[0]);
        setMinecraftClass("ServerConfigurationManager", params[1]);
    }

    public static Class<?> getPlayerListClass() {
        try {
            return getMinecraftClass("ServerConfigurationManager", "PlayerList");
        } catch (RuntimeException var1) {
            useFallbackServer();
            return getMinecraftClass("ServerConfigurationManager");
        }
    }

    public static Class<?> getNetLoginHandlerClass() {
        try {
            return getMinecraftClass("NetLoginHandler", "PendingConnection");
        } catch (RuntimeException var2) {
            Method selected = FuzzyReflection.fromClass(getPlayerListClass()).getMethod(FuzzyMethodContract.newBuilder().parameterMatches(FuzzyMatchers.matchExact(getEntityPlayerClass()).inverted(), 0).parameterExactType(String.class, 1).parameterExactType(String.class, 2).build());
            return setMinecraftClass("NetLoginHandler", selected.getParameterTypes()[0]);
        }
    }

    public static Class<?> getNetServerHandlerClass() {
        try {
            return getMinecraftClass("NetServerHandler", "PlayerConnection");
        } catch (RuntimeException var6) {
            try {
                return setMinecraftClass("NetServerHandler", FuzzyReflection.fromClass(getEntityPlayerClass()).getFieldByType("playerConnection", getNetHandlerClass()).getType());
            } catch (RuntimeException var5) {
                Class<?> playerClass = getEntityPlayerClass();
                FuzzyClassContract playerConnection = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeExact(playerClass).build()).constructor(FuzzyMethodContract.newBuilder().parameterCount(3).parameterSuperOf(getMinecraftServerClass(), 0).parameterSuperOf(getEntityPlayerClass(), 2).build()).method(FuzzyMethodContract.newBuilder().parameterCount(1).parameterExactType(String.class).build()).build();
                Class<?> fieldType = FuzzyReflection.fromClass(getEntityPlayerClass(), true).getField(FuzzyFieldContract.newBuilder().typeMatches(playerConnection).build()).getType();
                return setMinecraftClass("NetServerHandler", fieldType);
            }
        }
    }

    public static Class<?> getNetworkManagerClass() {
        try {
            return getMinecraftClass("INetworkManager", "NetworkManager");
        } catch (RuntimeException var2) {
            Constructor<?> selected = FuzzyReflection.fromClass(getNetServerHandlerClass()).getConstructor(FuzzyMethodContract.newBuilder().parameterSuperOf(getMinecraftServerClass(), 0).parameterSuperOf(getEntityPlayerClass(), 2).build());
            return setMinecraftClass("INetworkManager", selected.getParameterTypes()[1]);
        }
    }

    public static Class<?> getNetHandlerClass() {
        try {
            return getMinecraftClass("NetHandler", "Connection");
        } catch (RuntimeException var1) {
            return setMinecraftClass("NetHandler", getNetLoginHandlerClass().getSuperclass());
        }
    }

    public static Class<?> getItemStackClass() {
        try {
            return getMinecraftClass("ItemStack");
        } catch (RuntimeException var1) {
            return setMinecraftClass("ItemStack", FuzzyReflection.fromClass(getCraftItemStackClass(), true).getFieldByName("handle").getType());
        }
    }

    public static Class<?> getBlockClass() {
        try {
            return getMinecraftClass("Block");
        } catch (RuntimeException var9) {
            FuzzyReflection reflect = FuzzyReflection.fromClass(getItemStackClass());
            Set<Class<?>> candidates = new HashSet();
            Iterator i$ = reflect.getConstructors().iterator();

            while (i$.hasNext()) {
                Constructor<?> constructor = (Constructor) i$.next();
                Class[] arr$ = constructor.getParameterTypes();
                int len$ = arr$.length;

                for (int i = 0; i < len$; ++i) {
                    Class<?> clazz = arr$[i];
                    if (isMinecraftClass(clazz)) {
                        candidates.add(clazz);
                    }
                }
            }

            Method selected = reflect.getMethod(FuzzyMethodContract.newBuilder().parameterMatches(FuzzyMatchers.matchAnyOf(candidates)).returnTypeExact(Float.TYPE).build());
            return setMinecraftClass("Block", selected.getParameterTypes()[0]);
        }
    }

    public static Class<?> getWorldTypeClass() {
        try {
            return getMinecraftClass("WorldType");
        } catch (RuntimeException var2) {
            Method selected = FuzzyReflection.fromClass(getMinecraftServerClass(), true).getMethod(FuzzyMethodContract.newBuilder().parameterExactType(String.class, 0).parameterExactType(String.class, 1).parameterMatches(getMinecraftObjectMatcher()).parameterExactType(String.class, 4).parameterCount(5).build());
            return setMinecraftClass("WorldType", selected.getParameterTypes()[3]);
        }
    }

    public static Class<?> getDataWatcherClass() {
        try {
            return getMinecraftClass("DataWatcher");
        } catch (RuntimeException var3) {
            FuzzyClassContract dataWatcherContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().requireModifier(8).typeDerivedOf(Map.class)).field(FuzzyFieldContract.newBuilder().banModifier(8).typeDerivedOf(Map.class)).method(FuzzyMethodContract.newBuilder().parameterExactType(Integer.TYPE).parameterExactType(Object.class).returnTypeVoid()).build();
            FuzzyFieldContract fieldContract = FuzzyFieldContract.newBuilder().typeMatches(dataWatcherContract).build();
            return setMinecraftClass("DataWatcher", FuzzyReflection.fromClass(getEntityClass(), true).getField(fieldContract).getType());
        }
    }

    public static Class<?> getChunkPositionClass() {
        try {
            return getMinecraftClass("ChunkPosition");
        } catch (RuntimeException var3) {
            Class<?> normalChunkGenerator = getCraftBukkitClass("generator.NormalChunkGenerator");
            FuzzyMethodContract selected = FuzzyMethodContract.newBuilder().banModifier(8).parameterMatches(getMinecraftObjectMatcher(), 0).parameterExactType(String.class, 1).parameterExactType(Integer.TYPE, 2).parameterExactType(Integer.TYPE, 3).parameterExactType(Integer.TYPE, 4).build();
            return setMinecraftClass("ChunkPosition", FuzzyReflection.fromClass(normalChunkGenerator).getMethod(selected).getReturnType());
        }
    }

    public static Class<?> getChunkCoordinatesClass() {
        try {
            return getMinecraftClass("ChunkCoordinates");
        } catch (RuntimeException var1) {
            return setMinecraftClass("ChunkCoordinates", WrappedDataWatcher.getTypeClass(6));
        }
    }

    public static Class<?> getChunkCoordIntPair() {
        if (!isUsingNetty()) {
            throw new IllegalArgumentException("Not supported on 1.6.4 and older.");
        } else {
            try {
                return getMinecraftClass("ChunkCoordIntPair");
            } catch (RuntimeException var4) {
                Class<?> packet = PacketRegistry.getPacketClassFromType(com.comphenix.protocol.PacketType.Play.Server.MULTI_BLOCK_CHANGE);
                AbstractFuzzyMatcher<Class<?>> chunkCoordIntContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeDerivedOf(Integer.TYPE)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(Integer.TYPE)).method(FuzzyMethodContract.newBuilder().parameterExactArray(new Class[]{Integer.TYPE}).returnDerivedOf(getChunkPositionClass())).build().and(getMinecraftObjectMatcher());
                Field field = FuzzyReflection.fromClass(packet, true).getField(FuzzyFieldContract.matchType(chunkCoordIntContract));
                return setMinecraftClass("ChunkCoordIntPair", field.getType());
            }
        }
    }

    public static Class<?> getWatchableObjectClass() {
        try {
            return getMinecraftClass("WatchableObject");
        } catch (RuntimeException var2) {
            Method selected = FuzzyReflection.fromClass(getDataWatcherClass(), true).getMethod(FuzzyMethodContract.newBuilder().requireModifier(8).parameterDerivedOf(DataOutput.class, 0).parameterMatches(getMinecraftObjectMatcher(), 1).build());
            return setMinecraftClass("WatchableObject", selected.getParameterTypes()[1]);
        }
    }

    public static Class<?> getServerConnectionClass() {
        try {
            return getMinecraftClass("ServerConnection");
        } catch (RuntimeException var3) {
            FuzzyClassContract serverConnectionContract = FuzzyClassContract.newBuilder().constructor(FuzzyMethodContract.newBuilder().parameterExactType(getMinecraftServerClass()).parameterCount(1)).method(FuzzyMethodContract.newBuilder().parameterExactType(getNetServerHandlerClass())).build();
            Method selected = FuzzyReflection.fromClass(getMinecraftServerClass()).getMethod(FuzzyMethodContract.newBuilder().requireModifier(1024).returnTypeMatches(serverConnectionContract).build());
            return setMinecraftClass("ServerConnection", selected.getReturnType());
        }
    }

    public static Class<?> getNBTBaseClass() {
        try {
            return getMinecraftClass("NBTBase");
        } catch (RuntimeException var4) {
            Class<?> nbtBase = null;
            FuzzyClassContract tagCompoundContract;
            Method selected;
            if (isUsingNetty()) {
                tagCompoundContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeDerivedOf(Map.class)).method(FuzzyMethodContract.newBuilder().parameterDerivedOf(DataOutput.class).parameterCount(1)).build();
                selected = FuzzyReflection.fromClass(getPacketDataSerializerClass()).getMethod(FuzzyMethodContract.newBuilder().banModifier(8).parameterCount(1).parameterMatches(tagCompoundContract).returnTypeVoid().build());
                nbtBase = selected.getParameterTypes()[0].getSuperclass();
            } else {
                tagCompoundContract = FuzzyClassContract.newBuilder().constructor(FuzzyMethodContract.newBuilder().parameterExactType(String.class).parameterCount(1)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(Map.class)).build();
                selected = FuzzyReflection.fromClass(getPacketClass()).getMethod(FuzzyMethodContract.newBuilder().requireModifier(8).parameterSuperOf(DataInputStream.class).parameterCount(1).returnTypeMatches(tagCompoundContract).build());
                nbtBase = selected.getReturnType().getSuperclass();
            }

            if (nbtBase != null && !nbtBase.equals(Object.class)) {
                return setMinecraftClass("NBTBase", nbtBase);
            } else {
                throw new IllegalStateException("Unable to find NBT base class: " + nbtBase);
            }
        }
    }

    public static Class<?> getNBTReadLimiterClass() {
        return getMinecraftClass("NBTReadLimiter");
    }

    public static Class<?> getNBTCompoundClass() {
        try {
            return getMinecraftClass("NBTTagCompound");
        } catch (RuntimeException var1) {
            return setMinecraftClass("NBTTagCompound", NbtFactory.ofWrapper(NbtType.TAG_COMPOUND, "Test").getHandle().getClass());
        }
    }

    public static Class<?> getEntityTrackerClass() {
        try {
            return getMinecraftClass("EntityTracker");
        } catch (RuntimeException var3) {
            FuzzyClassContract entityTrackerContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeDerivedOf(Set.class)).method(FuzzyMethodContract.newBuilder().parameterSuperOf(getEntityClass()).parameterCount(1).returnTypeVoid()).method(FuzzyMethodContract.newBuilder().parameterSuperOf(getEntityClass(), 0).parameterSuperOf(Integer.TYPE, 1).parameterSuperOf(Integer.TYPE, 2).parameterCount(3).returnTypeVoid()).build();
            Field selected = FuzzyReflection.fromClass(getWorldServerClass(), true).getField(FuzzyFieldContract.newBuilder().typeMatches(entityTrackerContract).build());
            return setMinecraftClass("EntityTracker", selected.getType());
        }
    }

    public static Class<?> getNetworkListenThreadClass() {
        try {
            return getMinecraftClass("NetworkListenThread");
        } catch (RuntimeException var3) {
            FuzzyClassContract networkListenContract = FuzzyClassContract.newBuilder().field(FuzzyFieldContract.newBuilder().typeDerivedOf(ServerSocket.class)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(Thread.class)).field(FuzzyFieldContract.newBuilder().typeDerivedOf(List.class)).method(FuzzyMethodContract.newBuilder().parameterExactType(getNetServerHandlerClass())).build();
            Field selected = FuzzyReflection.fromClass(getMinecraftServerClass(), true).getField(FuzzyFieldContract.newBuilder().typeMatches(networkListenContract).build());
            return setMinecraftClass("NetworkListenThread", selected.getType());
        }
    }

    public static Class<?> getAttributeSnapshotClass() {
        try {
            return getMinecraftClass("AttributeSnapshot");
        } catch (RuntimeException var5) {
            Class<?> packetUpdateAttributes = PacketRegistry.getPacketClassFromType(com.comphenix.protocol.PacketType.Play.Server.UPDATE_ATTRIBUTES, true);
            final String packetSignature = packetUpdateAttributes.getCanonicalName().replace('.', '/');

            try {
                ClassReader reader = new ClassReader(packetUpdateAttributes.getCanonicalName());
                reader.accept(new EmptyClassVisitor() {
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                        return desc.startsWith("(Ljava/io/DataInput") ? new EmptyMethodVisitor() {
                            public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                                if (opcode == 183 && MinecraftReflection.isConstructor(name)) {
                                    String className = owner.replace('/', '.');
                                    if (desc.startsWith("(L" + packetSignature)) {
                                        MinecraftReflection.setMinecraftClass("AttributeSnapshot", MinecraftReflection.getClass(className));
                                    } else if (desc.startsWith("(Ljava/util/UUID;Ljava/lang/String")) {
                                        MinecraftReflection.setMinecraftClass("AttributeModifier", MinecraftReflection.getClass(className));
                                    }
                                }

                            }
                        } : null;
                    }
                }, 0);
            } catch (IOException var4) {
                throw new RuntimeException("Unable to read the content of Packet44UpdateAttributes.", var4);
            }

            return getMinecraftClass("AttributeSnapshot");
        }
    }

    public static Class<?> getIntHashMapClass() {
        try {
            return getMinecraftClass("IntHashMap");
        } catch (RuntimeException var4) {
            Class<?> parent = getEntityTrackerClass();
            FuzzyClassContract intHashContract = FuzzyClassContract.newBuilder().method(FuzzyMethodContract.newBuilder().parameterCount(2).parameterExactType(Integer.TYPE, 0).parameterExactType(Object.class, 1).requirePublic()).method(FuzzyMethodContract.newBuilder().parameterCount(1).parameterExactType(Integer.TYPE).returnTypeExact(Object.class).requirePublic()).field(FuzzyFieldContract.newBuilder().typeMatches(FuzzyMatchers.matchArray(FuzzyMatchers.matchAll()))).build();
            AbstractFuzzyMatcher<Field> intHashField = FuzzyFieldContract.newBuilder().typeMatches(getMinecraftObjectMatcher().and(intHashContract)).build();
            return setMinecraftClass("IntHashMap", FuzzyReflection.fromClass(parent).getField(intHashField).getType());
        }
    }

    public static Class<?> getAttributeModifierClass() {
        try {
            return getMinecraftClass("AttributeModifier");
        } catch (RuntimeException var1) {
            getAttributeSnapshotClass();
            return getMinecraftClass("AttributeModifier");
        }
    }

    public static Class<?> getMobEffectClass() {
        try {
            return getMinecraftClass("MobEffect");
        } catch (RuntimeException var3) {
            Class<?> packet = PacketRegistry.getPacketClassFromType(com.comphenix.protocol.PacketType.Play.Server.ENTITY_EFFECT);
            Constructor<?> constructor = FuzzyReflection.fromClass(packet).getConstructor(FuzzyMethodContract.newBuilder().parameterCount(2).parameterExactType(Integer.TYPE, 0).parameterMatches(getMinecraftObjectMatcher(), 1).build());
            return setMinecraftClass("MobEffect", constructor.getParameterTypes()[1]);
        }
    }

    public static Class<?> getPacketDataSerializerClass() {
        try {
            return getMinecraftClass("PacketDataSerializer");
        } catch (RuntimeException var3) {
            Class<?> packet = getPacketClass();
            Method method = FuzzyReflection.fromClass(packet).getMethod(FuzzyMethodContract.newBuilder().parameterCount(1).parameterDerivedOf(ByteBuf.class).returnTypeVoid().build());
            return setMinecraftClass("PacketDataSerializer", method.getParameterTypes()[0]);
        }
    }

    public static Class<?> getNbtCompressedStreamToolsClass() {
        try {
            return getMinecraftClass("NBTCompressedStreamTools");
        } catch (RuntimeException var7) {
            Class<?> packetSerializer = getPacketDataSerializerClass();
            Method writeNbt = FuzzyReflection.fromClass(packetSerializer).getMethodByParameters("writeNbt", new Class[]{getNBTCompoundClass()});

            try {
                Iterator i$ = ClassAnalyser.getDefault().getMethodCalls(writeNbt).iterator();

                while (i$.hasNext()) {
                    ClassAnalyser.AsmMethod method = (ClassAnalyser.AsmMethod) i$.next();
                    Class<?> owner = method.getOwnerClass();
                    if (!packetSerializer.equals(owner) && isMinecraftClass(owner)) {
                        return setMinecraftClass("NBTCompressedStreamTools", owner);
                    }
                }
            } catch (Exception var6) {
                throw new RuntimeException("Unable to analyse class.", var6);
            }

            throw new IllegalArgumentException("Unable to find NBTCompressedStreamTools.");
        }
    }

    public static ByteBuf getPacketDataSerializer(ByteBuf buffer) {
        Class<?> packetSerializer = getPacketDataSerializerClass();

        try {
            return (ByteBuf) packetSerializer.getConstructor(ByteBuf.class).newInstance(buffer);
        } catch (Exception var3) {
            throw new RuntimeException("Cannot construct packet serializer.", var3);
        }
    }

    public static Class<?> getMinecraftGsonClass() {
        try {
            return getMinecraftLibraryClass("com.google.gson.Gson");
        } catch (RuntimeException var2) {
            Class<?> match = FuzzyReflection.fromClass(com.comphenix.protocol.PacketType.Status.Server.OUT_SERVER_INFO.getPacketClass()).getFieldByType(".*\\.google\\.gson\\.Gson").getType();
            return setMinecraftLibraryClass("com.google.gson.Gson", match);
        }
    }

    private static boolean isConstructor(String name) {
        return "<init>".equals(name);
    }

    public static Class<?> getItemStackArrayClass() {
        if (itemStackArrayClass == null) {
            itemStackArrayClass = getArrayClass(getItemStackClass());
        }

        return itemStackArrayClass;
    }

    public static Class<?> getArrayClass(Class<?> componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }

    public static Class<?> getCraftItemStackClass() {
        return getCraftBukkitClass("inventory.CraftItemStack");
    }

    public static Class<?> getCraftPlayerClass() {
        return getCraftBukkitClass("entity.CraftPlayer");
    }

    public static Class<?> getCraftEntityClass() {
        return getCraftBukkitClass("entity.CraftEntity");
    }

    public static Class<?> getCraftMessageClass() {
        return getCraftBukkitClass("util.CraftChatMessage");
    }

    public static ItemStack getBukkitItemStack(ItemStack bukkitItemStack) {
        if (craftBukkitNMS != null) {
            return getBukkitItemByMethod(bukkitItemStack);
        } else {
            if (craftBukkitConstructor == null) {
                try {
                    craftBukkitConstructor = getCraftItemStackClass().getConstructor(ItemStack.class);
                } catch (Exception var3) {
                    if (!craftItemStackFailed) {
                        return getBukkitItemByMethod(bukkitItemStack);
                    }

                    throw new RuntimeException("Cannot find CraftItemStack(org.bukkit.inventory.ItemStack).", var3);
                }
            }

            try {
                return (ItemStack) craftBukkitConstructor.newInstance(bukkitItemStack);
            } catch (Exception var2) {
                throw new RuntimeException("Cannot construct CraftItemStack.", var2);
            }
        }
    }

    private static ItemStack getBukkitItemByMethod(ItemStack bukkitItemStack) {
        if (craftBukkitNMS == null) {
            try {
                craftBukkitNMS = getCraftItemStackClass().getMethod("asNMSCopy", ItemStack.class);
                craftBukkitOBC = getCraftItemStackClass().getMethod("asCraftMirror", getItemStackClass());
            } catch (Exception var3) {
                craftItemStackFailed = true;
                throw new RuntimeException("Cannot find CraftItemStack.asCraftCopy(org.bukkit.inventory.ItemStack).", var3);
            }
        }

        try {
            Object nmsItemStack = craftBukkitNMS.invoke((Object) null, bukkitItemStack);
            return (ItemStack) craftBukkitOBC.invoke((Object) null, nmsItemStack);
        } catch (Exception var2) {
            throw new RuntimeException("Cannot construct CraftItemStack.", var2);
        }
    }

    public static ItemStack getBukkitItemStack(Object minecraftItemStack) {
        if (craftNMSMethod != null) {
            return getBukkitItemByMethod(minecraftItemStack);
        } else {
            if (craftNMSConstructor == null) {
                try {
                    craftNMSConstructor = getCraftItemStackClass().getConstructor(minecraftItemStack.getClass());
                } catch (Exception var3) {
                    if (!craftItemStackFailed) {
                        return getBukkitItemByMethod(minecraftItemStack);
                    }

                    throw new RuntimeException("Cannot find CraftItemStack(net.mineraft.server.ItemStack).", var3);
                }
            }

            try {
                return (ItemStack) craftNMSConstructor.newInstance(minecraftItemStack);
            } catch (Exception var2) {
                throw new RuntimeException("Cannot construct CraftItemStack.", var2);
            }
        }
    }

    private static ItemStack getBukkitItemByMethod(Object minecraftItemStack) {
        if (craftNMSMethod == null) {
            try {
                craftNMSMethod = getCraftItemStackClass().getMethod("asCraftMirror", minecraftItemStack.getClass());
            } catch (Exception var3) {
                craftItemStackFailed = true;
                throw new RuntimeException("Cannot find CraftItemStack.asCraftMirror(net.mineraft.server.ItemStack).", var3);
            }
        }

        try {
            return (ItemStack) craftNMSMethod.invoke((Object) null, minecraftItemStack);
        } catch (Exception var2) {
            throw new RuntimeException("Cannot construct CraftItemStack.", var2);
        }
    }

    public static Object getMinecraftItemStack(ItemStack stack) {
        if (!isCraftItemStack(stack)) {
            stack = getBukkitItemStack(stack);
        }

        BukkitUnwrapper unwrapper = new BukkitUnwrapper();
        return unwrapper.unwrapItem(stack);
    }

    private static Class getClass(String className) {
        try {
            return MinecraftReflection.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException var2) {
            throw new RuntimeException("Cannot find class " + className, var2);
        }
    }

    public static Class getCraftBukkitClass(String className) {
        if (craftbukkitPackage == null) {
            craftbukkitPackage = new CachedPackage(getCraftBukkitPackage(), getClassSource());
        }

        return craftbukkitPackage.getPackageClass(className);
    }

    public static Class<?> getMinecraftClass(String className) {
        if (minecraftPackage == null) {
            minecraftPackage = new CachedPackage(getMinecraftPackage(), getClassSource());
        }

        return minecraftPackage.getPackageClass(className);
    }

    public static Class<?> getMinecraftLibraryClass(String className) {
        if (libraryPackage == null) {
            libraryPackage = new CachedPackage(getMinecraftLibraryPackage(), getClassSource());
        }

        return libraryPackage.getPackageClass(className);
    }

    private static Class<?> setMinecraftLibraryClass(String className, Class<?> clazz) {
        if (libraryPackage == null) {
            libraryPackage = new CachedPackage(getMinecraftLibraryPackage(), getClassSource());
        }

        libraryPackage.setPackageClass(className, clazz);
        return clazz;
    }

    private static Class<?> setMinecraftClass(String className, Class<?> clazz) {
        if (minecraftPackage == null) {
            minecraftPackage = new CachedPackage(getMinecraftPackage(), getClassSource());
        }

        minecraftPackage.setPackageClass(className, clazz);
        return clazz;
    }

    private static ClassSource getClassSource() {
        ErrorReporter reporter = ProtocolLibrary.getErrorReporter();
        if (classSource == null) {
            try {
                return classSource = (new RemappedClassSource()).initialize();
            } catch (RemappedClassSource.RemapperUnavaibleException var2) {
                if (var2.getReason() != Reason.MCPC_NOT_PRESENT) {
                    reporter.reportWarning(MinecraftReflection.class, Report.newBuilder(REPORT_CANNOT_FIND_MCPC_REMAPPER));
                }
            } catch (Exception var3) {
                reporter.reportWarning(MinecraftReflection.class, Report.newBuilder(REPORT_CANNOT_LOAD_CPC_REMAPPER));
            }

            classSource = ClassSource.fromClassLoader();
        }

        return classSource;
    }

    public static Class<?> getMinecraftClass(String className, String... aliases) {
        try {
            return getMinecraftClass(className);
        } catch (RuntimeException var10) {
            Class<?> success = null;
            String[] arr$ = aliases;
            int len$ = aliases.length;
            int i$ = 0;

            while (i$ < len$) {
                String alias = arr$[i$];

                try {
                    success = getMinecraftClass(alias);
                    break;
                } catch (RuntimeException var9) {
                    ++i$;
                }
            }

            if (success != null) {
                minecraftPackage.setPackageClass(className, success);
                return success;
            } else {
                throw new RuntimeException(String.format("Unable to find %s (%s)", className, Joiner.on(", ").join(aliases)));
            }
        }
    }

    public static String getNetworkManagerName() {
        return getNetworkManagerClass().getSimpleName();
    }

    public static String getNetLoginHandlerName() {
        return getNetLoginHandlerClass().getSimpleName();
    }
}
