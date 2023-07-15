package com.zeydie.settings;

import com.zeydie.DefaultPaths;
import com.zeydie.settings.interfaces.IGson;
import com.zeydie.sgson.SGsonFile;

public abstract class AbstractSettings {
    private final SGsonFile gsonFile = new SGsonFile(DefaultPaths.getOptimizationFile(String.format("%s.json", this.getConfigName())));

    public final void reload() {
        final IGson settings = this.getSettings();
        final IGson data = this.loadData(settings);

        this.setSettings(data);
    }

    public final IGson loadData(final IGson settings) {
        return this.gsonFile.fromJsonToObject(settings);
    }

    public final void rewrite(final IGson settings) {
        this.gsonFile.writeJsonFile(settings);
    }

    public abstract String getConfigName();

    public abstract IGson getSettings();

    public abstract void setSettings(final IGson object);

    public static final class DebugSettings {
        public boolean debug = true;
        public int tickRateDebug = 20 * 60;

        public boolean isDebug() {
            return this.debug;
        }

        public int getTickRateDebug() {
            return this.tickRateDebug;
        }
    }
}
