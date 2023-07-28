package com.zeydie.threads;

import com.google.common.collect.Sets;
import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.ITickRunnable;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.server.MinecraftServer;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractThread extends Thread {
    public final Timer timer = new Timer();
    private final Set<Runnable> runnables = Sets.newCopyOnWriteArraySet();
    private final ITickRunnable tickRate;
    private final boolean flush;
    private long lastTick = MinecraftServer.currentTick;

    public AbstractThread(final int id, final ITickRunnable tickRate, final boolean flush) {
        this.tickRate = tickRate;
        this.flush = flush;

        this.setName(String.format("%s #%d", this.getClass().getSimpleName(), id));
    }

    public final int getRunnables() {
        return this.runnables.size();
    }

    public void addRunnable(final Runnable runnable) {
        this.runnables.add(runnable);
    }

    public void removeRunnable(final Runnable runnable) {
        this.runnables.remove(runnable);
    }

    @Override
    public void run() {
        this.timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                final int currentTick = MinecraftServer.currentTick;

                synchronized (runnables) {
                    for (final Runnable runnable : runnables)
                        try {
                            runnable.run();
                        } catch (final Exception exception) {
                            exception.printStackTrace();
                        }
                }

                final AbstractSettings.DebugSettings debugSettings = tickRate.getDebugSettings();

                if (debugSettings.isDebug())
                    if (currentTick - lastTick >= debugSettings.getTickRateDebug()) {
                        FMLLog.info("[%s] TPS - %f; Runnables - %d; Time - %d ms.", getName(), MinecraftServer.currentTPS, runnables.size(), currentTick - MinecraftServer.currentTick);

                        lastTick = currentTick;
                    }

                if (flush)
                    runnables.clear();
            }
        }, 0, this.tickRate.getTickRate() == 0 ? 1 : this.tickRate.getTickRate() * 50L);
    }
}
