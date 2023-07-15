package ru.zoom4ikdan4ik.settings;

import ru.zoom4ikdan4ik.DefaultPaths;
import ru.zoom4ikdan4ik.gson.GsonFile;
import ru.zoom4ikdan4ik.settings.interfaces.IGson;

public abstract class AbstractSettings {
    private final GsonFile gsonFile = new GsonFile(DefaultPaths.getOptimizationFile(String.format("%s.json", this.getConfigName())));

    public final void reload() {
        final IGson settings = this.getSettings();
        final IGson data = this.loadData(settings);

        this.setSettings(data);
    }

    public final IGson loadData(final IGson settings) {
        return (IGson) this.gsonFile.fromJsonToObject(settings);
    }

    public final void rewrite(final IGson settings) {
        this.gsonFile.writeJsonFile(settings);
    }

    public abstract String getConfigName();

    public abstract void setSettings(final IGson object);

    public abstract IGson getSettings();

    public static final class DebugSettings {
        public boolean debug = true;
        public int tickRateDebug = 20 * 60;

        public final boolean isDebug() {
            return this.debug;
        }

        public final int getTickRateDebug() {
            return this.tickRateDebug;
        }
    }
}
