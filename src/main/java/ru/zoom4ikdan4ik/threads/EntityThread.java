package ru.zoom4ikdan4ik.threads;

import ru.zoom4ikdan4ik.settings.optimization.MultiThreadSettings;

public final class EntityThread extends AbstractThread {
    public EntityThread(final int id) {
        super(id, MultiThreadSettings.getInstance().getMobsSettings(), true);
    }
}