package ru.zoom4ikdan4ik.settings.interfaces;

import ru.zoom4ikdan4ik.settings.AbstractSettings;

public interface ITickRunnable {
    int getTickRate();

    AbstractSettings.DebugSettings getDebugSettings();
}
