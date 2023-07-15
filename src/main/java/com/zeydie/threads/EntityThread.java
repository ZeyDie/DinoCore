package com.zeydie.threads;

import com.zeydie.settings.optimization.MultiThreadSettings;

public final class EntityThread extends AbstractThread {
    public EntityThread(final int id) {
        super(id, MultiThreadSettings.getInstance().getMobsSettings(), true);
    }
}