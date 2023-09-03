package com.zeydie;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;

public final class DefaultPaths {
    @NotNull
    public static final String VERSION = "R1.1";
    @NotNull
    public static final String LOGS_FOLDER = "logs";
    @NotNull
    public static final String SETTINGS_FOLDER = "settings";
    @NotNull
    public static final String SETTINGS_CORE_FOLDER = "core";

    public static @NotNull File getDefaultFile(@NotNull final String name) {
        return Paths.get(SETTINGS_FOLDER, name).toFile();
    }

    public static @NotNull File getCoreFile(@NotNull final String name) {
        return Paths.get(SETTINGS_FOLDER, SETTINGS_CORE_FOLDER, VERSION, name).toFile();
    }

    public static @NotNull String getLogFile(@NotNull final String name) {
        return String.format("%s/%s", LOGS_FOLDER, name);
    }
}
