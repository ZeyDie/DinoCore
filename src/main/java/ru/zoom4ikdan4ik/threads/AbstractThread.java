package ru.zoom4ikdan4ik.threads;

import com.google.common.collect.Sets;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.server.MinecraftServer;
import ru.zoom4ikdan4ik.settings.AbstractSettings;
import ru.zoom4ikdan4ik.settings.interfaces.ITickRunnable;

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
            public final void run() {
                final long start = System.currentTimeMillis();

                synchronized (runnables) {
                    for (final Runnable runnable : runnables)
                        try {
                            runnable.run();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                }

                final long end = System.currentTimeMillis();
                final AbstractSettings.DebugSettings debugSettings = tickRate.getDebugSettings();

                if (debugSettings.isDebug())
                    if (MinecraftServer.currentTick - lastTick >= debugSettings.getTickRateDebug()) {
                        lastTick = MinecraftServer.currentTick;

                        FMLLog.info("[%s] TPS - %f; Runnables - %d; Time - %d ms.", getName(), MinecraftServer.getServer().currentTPS, runnables.size(), end - start);
                    }

                if (flush)
                    runnables.clear();
            }
        }, 0, this.tickRate.getTickRate() == 0 ? 1 : this.tickRate.getTickRate() * 50);
    }
}
