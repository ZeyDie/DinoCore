package com.zeydie.settings;

import com.zeydie.DefaultPaths;
import com.zeydie.sgson.SGsonFile;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSettings {
    @NotNull
    private final SGsonFile gsonFile = new SGsonFile(DefaultPaths.getOptimizationFile(String.format("%s.json", this.getConfigName())));

    public final void reload() {
        this.setSettings(this.gsonFile.fromJsonToObject(this.getSettings()));
    }

    public final @NotNull Object loadData(@NotNull final Object settings) {
        return this.gsonFile.fromJsonToObject(settings);
    }

    public final void rewrite(@NotNull final Object settings) {
        this.gsonFile.writeJsonFile(settings);
    }

    public abstract @NotNull String getConfigName();

    public abstract @NotNull Object getSettings();

    public abstract void setSettings(@NotNull final Object object);

    @Data
    public static final class DebugSettings {
        private boolean debug = true;
        private int tickRateDebug = 20 * 60;
    }
}
