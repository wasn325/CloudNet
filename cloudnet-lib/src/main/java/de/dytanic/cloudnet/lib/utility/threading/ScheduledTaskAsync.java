package de.dytanic.cloudnet.lib.utility.threading;

import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;

/**
 * Created by Tareko on 24.05.2017.
 */
public class ScheduledTaskAsync extends ScheduledTask {

    protected Scheduler scheduler;

    public ScheduledTaskAsync(final long taskId,
                              final Runnable runnable,
                              final int delay,
                              final int repeatDelay,
                              final Scheduler scheduler) {
        super(taskId, runnable, delay, repeatDelay);
        this.scheduler = scheduler;
    }

    @Override
    protected boolean isAsync() {
        return true;
    }

    @Override
    public void run() {
        if (interrupted) {
            return;
        }

        if (delay != 0 && delayTime != 0) {
            delayTime--;
            return;
        }

        if (repeatTime > 0) {
            repeatTime--;
        } else {
            TaskScheduler.runtimeScheduler().schedule(runnable);
            if (repeatTime == -1) {
                cancel();
                return;
            }
            repeatTime = repeatDelay;
        }

    }
}
