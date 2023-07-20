package com.zeydie.settings;

import com.zeydie.DefaultPaths;
import com.zeydie.sgson.SGsonFile;
import lombok.Data;

public abstract class AbstractSettings {
    private final SGsonFile gsonFile = new SGsonFile(DefaultPaths.getOptimizationFile(String.format("%s.json", this.getConfigName())));

    public final void reload() {
        this.setSettings(this.gsonFile.fromJsonToObject(this.getSettings()));
    }

    public final Object loadData(final Object settings) {
        return this.gsonFile.fromJsonToObject(settings);
    }

    public final void rewrite(final Object settings) {
        this.gsonFile.writeJsonFile(settings);
    }

    public abstract String getConfigName();

    public abstract Object getSettings();

    public abstract void setSettings(final Object object);

    @Data
    public static final class DebugSettings {
        private boolean debug = true;
        private int tickRateDebug = 20 * 60;
    }
}
