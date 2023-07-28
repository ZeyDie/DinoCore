package com.zeydie.legacy.core;

import net.minecraft.server.MinecraftServer;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public final class LogAgentFilter implements Filter {
    private static final String LOG_CONFIG_PREFIX = "cauldron.loglevels."; // Cauldron

    @Override
    public boolean isLoggable(final LogRecord record) {
        if (MinecraftServer.configuration == null) return true;
        final String logName = record.getLoggerName().replace('.', '-');
        if (!MinecraftServer.configuration.isString(LOG_CONFIG_PREFIX + logName)) {
            MinecraftServer.configuration.set(LOG_CONFIG_PREFIX + logName, "INFO");
            try {
                MinecraftServer.configuration.save(MinecraftServer.configFile);
            } catch (final java.io.IOException e) {
                e.printStackTrace();
            }
        }
        final String modLogLevel = MinecraftServer.configuration.getString(LOG_CONFIG_PREFIX + logName);
        final Level logLevel = Level.parse(modLogLevel);
        return record.getLevel().intValue() >= logLevel.intValue();
    }
}
