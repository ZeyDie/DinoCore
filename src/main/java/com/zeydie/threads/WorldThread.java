package com.zeydie.threads;

import com.zeydie.settings.optimization.MultiThreadSettings;

public final class WorldThread extends AbstractThread {
    public WorldThread(final int id) {
        super(id, MultiThreadSettings.getInstance().getWorldSettings(), true);
    }
}
