package com.zeydie.netty;

import java.io.File;
import java.nio.file.Paths;

public final class DefaultPaths {
    public static final String LOGS_FOLDER = "logs";
    public static final String SETTINGS_FOLDER = "settings";
    public static final String SETTINGS_OPTIMIZATION_FOLDER = "optimization";

    public static final File getDefaultFile(final String name) {
        return Paths.get(SETTINGS_FOLDER, name).toFile();
    }

    public static final File getOptimizationFile(final String name) {
        return Paths.get(SETTINGS_FOLDER, SETTINGS_OPTIMIZATION_FOLDER, name).toFile();
    }

    public static final String getLogFile(final String name) {
        return String.format("%s/%s", LOGS_FOLDER, name);
    }
}
