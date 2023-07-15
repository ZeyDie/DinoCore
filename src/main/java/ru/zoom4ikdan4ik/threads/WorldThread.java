package ru.zoom4ikdan4ik.threads;

import ru.zoom4ikdan4ik.settings.optimization.MultiThreadSettings;

public final class WorldThread extends AbstractThread {
    public WorldThread(final int id) {
        super(id, MultiThreadSettings.getInstance().getWorldSettings(), true);
    }
}
