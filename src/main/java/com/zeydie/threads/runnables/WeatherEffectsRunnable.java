package com.zeydie.threads.runnables;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDummyContainer;

public final class WeatherEffectsRunnable implements Runnable {
    private final World world;

    public WeatherEffectsRunnable(final World world) {
        this.world = world;
    }

    @Override
    public void run() {
        for (int i = 0; i < this.world.weatherEffects.size(); ++i) {
            final Entity entity = (Entity) this.world.weatherEffects.get(i);
            // CraftBukkit start - Fixed an NPE
            if (entity == null) {
                continue;
            }

            // CraftBukkit end

            try {
                ++entity.ticksExisted;
                entity.onUpdate();
            } catch (final Throwable throwable) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking entity");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being ticked");

                if (entity == null) {
                    crashreportcategory.addCrashSection("Entity", "~~NULL~~");
                } else {
                    entity.addEntityCrashInfo(crashreportcategory);
                }

                if (ForgeDummyContainer.removeErroringEntities) {
                    FMLLog.severe(crashreport.getCompleteReport());
                    this.world.removeEntity(entity);
                } else {
                    throw new ReportedException(crashreport);
                }
            }

            if (entity.isDead) {
                this.world.weatherEffects.remove(i--);
            }
        }
    }
}
