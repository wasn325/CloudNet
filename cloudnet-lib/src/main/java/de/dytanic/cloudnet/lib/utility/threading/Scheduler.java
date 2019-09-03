package de.dytanic.cloudnet.lib.utility.threading;

import de.dytanic.cloudnet.lib.NetworkUtils;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 24.05.2017.
 */
public final class Scheduler implements TaskCancelable, Runnable {

    private final int ticks;
    private final Random random = new Random();
    private final ConcurrentHashMap<Long, ScheduledTask> tasks = NetworkUtils.newConcurrentHashMap();

    public Scheduler(final int ticks) {
        this.ticks = ticks;
    }

    public Scheduler() {
        this.ticks = 10;
    }

    public int getTicks() {
        return ticks;
    }

    public Random getRandom() {
        return random;
    }

    public ScheduledTask runTaskSync(final Runnable runnable) {
        return runTaskDelaySync(runnable, 0);
    }

    public ScheduledTask runTaskDelaySync(final Runnable runnable, final int delayTicks) {
        return runTaskRepeatSync(runnable, delayTicks, -1);
    }

    public ScheduledTask runTaskRepeatSync(final Runnable runnable, final int delayTicks, final int repeatDelay) {
        final long id = random.nextLong();
        final ScheduledTask task = new ScheduledTask(id, runnable, delayTicks, repeatDelay);
        this.tasks.put(id, task);
        return task;
    }

    public ScheduledTask runTaskAsync(final Runnable runnable) {
        return runTaskDelayAsync(runnable, 0);
    }

    public ScheduledTask runTaskDelayAsync(final Runnable runnable, final int delay) {
        return runTaskRepeatAsync(runnable, delay, -1);
    }

    public ScheduledTask runTaskRepeatAsync(final Runnable runnable, final int delay, final int repeat) {
        final long id = random.nextLong();
        final ScheduledTask task = new ScheduledTaskAsync(id, runnable, delay, repeat, this);
        this.tasks.put(id, task);
        return task;
    }

    @Override
    public void cancelTask(final Long id) {
        if (tasks.containsKey(id)) {
            tasks.get(id).cancel();
        }
    }

    @Override
    public void cancelAllTasks() {
        tasks.clear();
    }

    @Override
    @Deprecated //This Method use the Thread for the Task Handling
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000 / ticks);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

            if (tasks.isEmpty()) {
                continue;
            }

            final ConcurrentHashMap<Long, ScheduledTask> tasks = this.tasks; //For a Performance optimizing

            for (final ScheduledTask task : tasks.values()) {

                if (task.isInterrupted()) {
                    this.tasks.remove(task.getTaskId());
                    continue;
                }

                task.run();
            }
        }
    }
}
