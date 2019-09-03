/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility.threading;

/**
 * Created by Tareko on 17.09.2017.
 */
public abstract class RunnabledTask implements Runnable {

    protected ScheduledTask task;

    public void scheduleSynchronized(final Scheduler scheduler) {
        task = scheduler.runTaskSync(this);
    }

    public void scheduleSynchronized(final Scheduler scheduler, final int delay) {
        task = scheduler.runTaskDelaySync(this, delay);
    }

    public void scheduleSynchronized(final Scheduler scheduler, final int delay, final int repeat) {
        task = scheduler.runTaskRepeatSync(this, delay, repeat);
    }

    public void scheduleAsynchronized(final Scheduler scheduler) {
        task = scheduler.runTaskAsync(this);
    }

    public void scheduleAsynchronized(final Scheduler scheduler, final int delay) {
        task = scheduler.runTaskDelayAsync(this, delay);
    }

    public void scheduleAsynchronized(final Scheduler scheduler, final int delay, final int repeat) {
        task = scheduler.runTaskRepeatAsync(this, delay, repeat);
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
        }
    }
}
