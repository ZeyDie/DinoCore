package com.zeydie.threads.runnables;

import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAITasks;

import java.util.ArrayList;
import java.util.Iterator;

//TODO Refactor
public final class AIRunnable implements Runnable {
    private final EntityAITasks entityAITasks;

    public AIRunnable(final EntityAITasks entityAITasks) {
        this.entityAITasks = entityAITasks;
    }

    @Override
    public void run() {
        ArrayList arraylist = new ArrayList();
        Iterator iterator;
        EntityAITaskEntry entityaitaskentry;

        synchronized (this.entityAITasks.executingTaskEntries) {
            if (this.entityAITasks.tickCount++ % entityAITasks.tickRate == 0) {
                iterator = this.entityAITasks.taskEntries.iterator();

                while (iterator.hasNext()) {
                    entityaitaskentry = (EntityAITaskEntry) iterator.next();
                    boolean flag = this.entityAITasks.executingTaskEntries.contains(entityaitaskentry);

                    if (flag) {
                        if (this.entityAITasks.canUse(entityaitaskentry) && this.entityAITasks.canContinue(entityaitaskentry)) {
                            continue;
                        }

                        entityaitaskentry.action.resetTask();
                        this.entityAITasks.executingTaskEntries.remove(entityaitaskentry);
                    }

                    if (this.entityAITasks.canUse(entityaitaskentry) && entityaitaskentry.action.shouldExecute()) {
                        arraylist.add(entityaitaskentry);
                        this.entityAITasks.executingTaskEntries.add(entityaitaskentry);
                    }
                }
            } else {
                iterator = this.entityAITasks.executingTaskEntries.iterator();

                while (iterator.hasNext()) {
                    entityaitaskentry = (EntityAITaskEntry) iterator.next();

                    if (!entityaitaskentry.action.continueExecuting()) {
                        entityaitaskentry.action.resetTask();
                        iterator.remove();
                    }
                }
            }

            this.entityAITasks.theProfiler.startSection("goalStart");
            iterator = arraylist.iterator();

            while (iterator.hasNext()) {
                entityaitaskentry = (EntityAITaskEntry) iterator.next();
                this.entityAITasks.theProfiler.startSection(entityaitaskentry.action.getClass().getSimpleName());
                entityaitaskentry.action.startExecuting();
                this.entityAITasks.theProfiler.endSection();
            }

            this.entityAITasks.theProfiler.endSection();
            this.entityAITasks.theProfiler.startSection("goalTick");
            iterator = this.entityAITasks.executingTaskEntries.iterator();

            while (iterator.hasNext()) {
                entityaitaskentry = (EntityAITaskEntry) iterator.next();
                entityaitaskentry.action.updateTask();
            }
        }

        this.entityAITasks.theProfiler.endSection();
    }
}
