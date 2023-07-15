package com.zeydie.settings.interfaces;

import com.zeydie.settings.AbstractSettings;

public interface ITickRunnable {
    int getTickRate();

    AbstractSettings.DebugSettings getDebugSettings();
}
