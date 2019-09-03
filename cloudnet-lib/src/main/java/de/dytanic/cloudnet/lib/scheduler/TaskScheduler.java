package de.dytanic.cloudnet.lib.scheduler;

import de.dytanic.cloudnet.lib.utility.threading.Callback;

import java.time.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskScheduler {

    private static final TaskScheduler RUNTIME_SCHEDULER = new TaskScheduler(Runtime.getRuntime().availableProcessors());

    protected final ThreadGroup threadGroup = new ThreadGroup("TaskScheduler-Group-" + new Random().nextLong());

    protected final AtomicLong threadId = new AtomicLong(0);

    protected final String name = threadGroup.getName();

    protected final long sleepThreadSwitch;

    protected final boolean dynamicWorkerCount;

    protected final long threadLiveMillis;

    protected int maxThreads;

    protected Logger logger;

    protected Deque<TaskEntry<?>> taskEntries = new ConcurrentLinkedDeque<>();

    protected Collection<Worker> workers = new ConcurrentLinkedQueue<>();


    public TaskScheduler() {
        this(Runtime.getRuntime().availableProcessors());
    }


    public TaskScheduler(final int maxThreads) {
        this(maxThreads, (Logger) null);
    }


    public TaskScheduler(final int maxThreads, final Logger logger) {
        this(maxThreads, null, logger);
    }


    public TaskScheduler(final int maxThreads, final Collection<TaskEntry<?>> entries, final Logger logger) {
        this(maxThreads, entries, logger, 10);
    }


    public TaskScheduler(final int maxThreads, final Collection<TaskEntry<?>> entries, final Logger logger, final long sleepThreadSwitch) {
        this(maxThreads, entries, logger, sleepThreadSwitch, false);
    }


    public TaskScheduler(final int maxThreads,
                         final Collection<TaskEntry<?>> entries,
                         final Logger logger,
                         final long sleepThreadSwitch,
                         final boolean dynamicThreadCount) {
        this(maxThreads, entries, logger, sleepThreadSwitch, dynamicThreadCount, 10000L);
    }


    public TaskScheduler(final int maxThreads,
                         final Collection<TaskEntry<?>> entries,
                         final Logger logger,
                         final long sleepThreadSwitch,
                         final boolean dynamicThreadCount,
                         final long threadLiveMillis) {

        this.sleepThreadSwitch = sleepThreadSwitch;
        this.dynamicWorkerCount = dynamicThreadCount;
        this.threadLiveMillis = threadLiveMillis;

        this.maxThreads = maxThreads <= 0 ? Runtime.getRuntime().availableProcessors() : maxThreads;
        this.logger = logger != null ? logger : Logger.getLogger("TaskScheduler-Logger@" + threadGroup.getName());

        if (entries != null) {
            taskEntries.addAll(entries);
        }
    }


    public TaskScheduler(final long sleepThreadSwitch) {
        this(Runtime.getRuntime().availableProcessors(), sleepThreadSwitch);
    }


    public TaskScheduler(final int maxThreads, final long sleepThreadSwitch) {
        this(maxThreads, (Logger) null, sleepThreadSwitch);
    }


    public TaskScheduler(final int maxThreads, final Logger logger, final long sleepThreadSwitch) {
        this(maxThreads, null, logger, sleepThreadSwitch);
    }


    public TaskScheduler(final Logger logger) {
        this(Runtime.getRuntime().availableProcessors(), logger);
    }


    public TaskScheduler(final Logger logger, final long sleepThreadSwitch) {
        this(Runtime.getRuntime().availableProcessors(), logger, sleepThreadSwitch);
    }


    public TaskScheduler(final Collection<TaskEntry<?>> entries) {
        this(Runtime.getRuntime().availableProcessors(), entries);
    }


    public TaskScheduler(final int maxThreads, final Collection<TaskEntry<?>> entries) {
        this(maxThreads, entries, null);
    }


    public TaskScheduler(final Collection<TaskEntry<?>> entries, final long sleepThreadSwtich) {
        this(Runtime.getRuntime().availableProcessors(), entries, sleepThreadSwtich);
    }


    public TaskScheduler(final int maxThreads, final Collection<TaskEntry<?>> entries, final long sleepThreadSwitch) {
        this(maxThreads, entries, null, sleepThreadSwitch);
    }


    public TaskScheduler(final Collection<TaskEntry<?>> entries, final Logger logger) {
        this(Runtime.getRuntime().availableProcessors(), entries, logger);
    }


    public TaskScheduler(final Collection<TaskEntry<?>> entries, final Logger logger, final long sleepThreadSwtich) {
        this(Runtime.getRuntime().availableProcessors(), entries, logger, sleepThreadSwtich);
    }


    public TaskScheduler(final int maxThreads, final boolean dynamicWorkerCount) {
        this(maxThreads, null, dynamicWorkerCount);
    }

    public TaskScheduler(final int maxThreads, final Logger logger, final boolean dynamicWorkerCount) {
        this(maxThreads, null, logger, 10, dynamicWorkerCount);
    }

    public TaskScheduler(final int maxThreads,
                         final Collection<TaskEntry<?>> entries,
                         final Logger logger,
                         final boolean dynamicWorkerCount) {
        this(maxThreads, entries, logger, 10, dynamicWorkerCount);
    }



    /* ======================================================================== */

    public static TaskScheduler runtimeScheduler() {
        return RUNTIME_SCHEDULER;
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable) {
        return schedule(runnable, (Callback<Void>) null);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Callback<Void> callback) {
        return schedule(runnable, callback, 0);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Callback<Void> callback, final long delay) {
        return schedule(runnable, callback, delay, 0);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Callback<Void> callback, final long delay, final long repeats) {
        return schedule(new VoidTaskEntry(runnable, callback, delay, repeats));
    }

    public <V> TaskEntryFuture<V> schedule(final TaskEntry<V> taskEntry) {
        return offerEntry(taskEntry);
    }

    private <V> TaskEntryFuture<V> offerEntry(final TaskEntry<V> entry) {
        this.taskEntries.offer(entry);
        checkEnougthThreads();
        return entry.drop();
    }

    private void checkEnougthThreads() {
        final Worker worker = hasFreeWorker();
        if (getCurrentThreadSize() < maxThreads || (dynamicWorkerCount && maxThreads > 1 && taskEntries.size() > getCurrentThreadSize() &&
                                                    taskEntries.size() <= (getMaxThreads() * 2)) && worker == null) {
            newWorker();
        }
    }

    private Worker hasFreeWorker() {
        for (final Worker worker : workers) {
            if (worker.isFreeWorker()) {
                return worker;
            }
        }

        return null;
    }

    public int getCurrentThreadSize() {
        return this.workers.size();
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    protected void newWorker() {
        final Worker worker = new Worker();
        workers.add(worker);

        worker.start();
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Date timeout) {
        return schedule(runnable, timeout.getTime() - System.currentTimeMillis());
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final long delay) {
        return schedule(runnable, (Callback<Void>) null, delay);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final LocalDate localDate, final LocalTime localTime) {
        return schedule(runnable, null, localDate, localTime);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable,
                                          final Callback<Void> callback,
                                          final LocalDate localDate,
                                          final LocalTime localTime) {
        return schedule(runnable, callback, localDate, localTime, 0);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable,
                                          final Callback<Void> callback,
                                          final LocalDate localDate,
                                          final LocalTime localTime,
                                          final long repeats) {
        return schedule(runnable, callback, LocalDateTime.of(localDate, localTime), repeats);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable,
                                          final Callback<Void> callback,
                                          final LocalDateTime localDateTime,
                                          final long repeats) {
        return schedule(runnable, callback, localDateTime.atZone(ZoneId.systemDefault()), repeats);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable,
                                          final Callback<Void> callback,
                                          final ZonedDateTime zonedDateTime,
                                          final long repeats) {
        return schedule(runnable, callback, zonedDateTime.toInstant(), repeats);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable,
                                          final Callback<Void> callback,
                                          final Instant instant,
                                          final long repeats) {
        return schedule(runnable, callback, instant.toEpochMilli() - System.currentTimeMillis(), repeats);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final LocalDateTime localDateTime) {
        return schedule(runnable, null, localDateTime);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Callback<Void> callback, final LocalDateTime localDateTime) {
        return schedule(runnable, callback, localDateTime, 0);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final ZonedDateTime zonedDateTime) {
        return schedule(runnable, null, zonedDateTime);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Callback<Void> callback, final ZonedDateTime zonedDateTime) {
        return schedule(runnable, callback, zonedDateTime, 0);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Instant instant) {
        return schedule(runnable, null, instant);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Callback<Void> callback, final Instant instant) {
        return schedule(runnable, callback, instant, 0);
    }



    /*= --------------------------------------------------------------------------------------- =*/

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Callback<Void> callback, final Date timeout) {
        return schedule(runnable, callback, timeout.getTime() - System.currentTimeMillis());
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final long delay, final TimeUnit timeUnit) {
        return schedule(runnable, (Callback<Void>) null, timeUnit.toMillis(delay));
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable,
                                          final Callback<Void> callback,
                                          final long delay,
                                          final TimeUnit timeUnit) {
        return schedule(runnable, (Callback<Void>) null, timeUnit.toMillis(delay));
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Date timeout, final long repeats) {
        return schedule(runnable, timeout.getTime() - System.currentTimeMillis(), repeats);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final long delay, final long repeats) {
        return schedule(runnable, null, delay, repeats);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final long delay, final TimeUnit timeUnit, final long repeats) {
        return schedule(runnable, null, timeUnit.toMillis(delay), repeats);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable, final Callback<Void> callback, final Date timeout, final long repeats) {
        return schedule(runnable, callback, timeout.getTime() - System.currentTimeMillis(), repeats);
    }

    public TaskEntryFuture<Void> schedule(final Runnable runnable,
                                          final Callback<Void> callback,
                                          final long delay,
                                          final TimeUnit timeUnit,
                                          final long repeats) {
        return schedule(runnable, callback, timeUnit.toMillis(delay), repeats);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable) {
        return schedule(callable, (Callback<V>) null);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final Callback<V> callback) {
        return schedule(callable, callback, 0);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final Callback<V> callback, final long delay) {
        return schedule(callable, callback, delay, 0);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final Callback<V> callback, final long delay, final long repeats) {
        return schedule(new TaskEntry<>(callable, callback, delay, repeats));
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final LocalDate localDate, final LocalTime localTime) {
        return schedule(callable, null, localDate, localTime);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable,
                                           final Callback<V> callback,
                                           final LocalDate localDate,
                                           final LocalTime localTime) {
        return schedule(callable, callback, localDate, localTime, 0);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable,
                                           final Callback<V> callback,
                                           final LocalDate localDate,
                                           final LocalTime localTime,
                                           final long repeats) {
        return schedule(callable, callback, LocalDateTime.of(localDate, localTime), repeats);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable,
                                           final Callback<V> callback,
                                           final LocalDateTime localDateTime,
                                           final long repeats) {
        return schedule(callable, callback, localDateTime.atZone(ZoneId.systemDefault()), 0);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable,
                                           final Callback<V> callback,
                                           final ZonedDateTime zonedDateTime,
                                           final long repeats) {
        return schedule(callable, callback, zonedDateTime.toInstant(), 0);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable,
                                           final Callback<V> callback,
                                           final Instant instant,
                                           final long repeats) {
        return schedule(callable, callback, instant.toEpochMilli(), 0);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final LocalDateTime localDateTime) {
        return schedule(callable, null, localDateTime);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final Callback<V> callback, final LocalDateTime localDateTime) {
        return schedule(callable, callback, localDateTime, 0);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final ZonedDateTime zonedDateTime) {
        return schedule(callable, null, zonedDateTime);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final Callback<V> callback, final ZonedDateTime zonedDateTime) {
        return schedule(callable, callback, zonedDateTime, 0);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final Instant instant) {
        return schedule(callable, null, instant);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final Callback<V> callback, final Instant instant) {
        return schedule(callable, callback, instant, 0);
    }



    /* =============================== */

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final long delay) {
        return schedule(callable, null, delay);
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit timeUnit) {
        return schedule(callable, null, timeUnit.toMillis(delay));
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable,
                                           final Callback<V> callback,
                                           final long delay,
                                           final TimeUnit timeUnit) {
        return schedule(callable, callback, timeUnit.toMillis(delay));
    }

    public <V> TaskEntryFuture<V> schedule(final Callable<V> callable,
                                           final Callback<V> callback,
                                           final long delay,
                                           final TimeUnit timeUnit,
                                           final long repeats) {
        return schedule(callable, callback, timeUnit.toMillis(delay), repeats);
    }

    public <V> Collection<TaskEntryFuture<V>> schedule(final Collection<TaskEntry<V>> threadEntries) {

        final Collection<TaskEntryFuture<V>> TaskEntryFutures = new ArrayList<TaskEntryFuture<V>>();
        for (final TaskEntry<V> entry : threadEntries) {
            TaskEntryFutures.add(offerEntry(entry));
        }

        return TaskEntryFutures;
    }

    @SuppressWarnings("deprecation")
    public Collection<TaskEntry<?>> shutdown() {

        for (final Worker worker : workers) {
            try {
                worker.interrupt();
                worker.stop();
            } catch (final ThreadDeath th) {
                workers.remove(worker);
            }
        }

        final Collection<TaskEntry<?>> entries = new ArrayList<>(taskEntries);

        taskEntries.clear();
        workers.clear();
        threadId.set(0);

        return entries;
    }

    public TaskScheduler chargeThreadLimit(final short threads) {
        this.maxThreads += threads;
        return this;
    }

    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

    /* =================================== */

    public String getName() {
        return name;
    }

    public Deque<TaskEntry<?>> getThreadEntries() {
        return new ConcurrentLinkedDeque<>();
    }

    public Logger getLogger() {
        return logger;
    }

    /* =================================== */

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    private static final class VoidTaskEntry extends TaskEntry<Void> {

        public VoidTaskEntry(final Callable<Void> pTask, final Callback<Void> pComplete, final long pDelay, final long pRepeat) {
            super(pTask, pComplete, pDelay, pRepeat);
        }


        public VoidTaskEntry(final Runnable ptask, final Callback<Void> pComplete, final long pDelay, final long pRepeat) {
            super(new Callable<Void>() {

                @Override
                public Void call() throws Exception {

                    if (ptask != null) {
                        ptask.run();
                    }

                    return null;
                }
            }, pComplete, pDelay, pRepeat);
        }
    }

    public class Worker extends Thread {

        volatile TaskEntry<?> taskEntry;

        private long liveTimeStamp = System.currentTimeMillis();

        Worker() {
            super(threadGroup, threadGroup.getName() + '#' + threadId.addAndGet(1));
            setDaemon(true);
        }

        public boolean isFreeWorker() {
            return taskEntry == null;
        }

        @Override
        public synchronized void run() {
            while ((liveTimeStamp + threadLiveMillis) > System.currentTimeMillis()) {
                execute();
                sleepUninterruptedly(sleepThreadSwitch);
            }

            workers.remove(this);
        }

        public synchronized void execute() {
            while (!taskEntries.isEmpty() && !isInterrupted()) {
                taskEntry = taskEntries.poll();

                if (taskEntry == null || taskEntry.task == null) {
                    continue;
                }

                liveTimeStamp = System.currentTimeMillis();

                if (taskEntry.delayTimeOut != 0 && System.currentTimeMillis() < taskEntry.delayTimeOut) {
                    if (maxThreads != 1) {
                        final long difference = taskEntry.delayTimeOut - System.currentTimeMillis();

                        if (difference > sleepThreadSwitch) {
                            sleepUninterruptedly(sleepThreadSwitch - 1);
                            offerEntry(taskEntry);
                            continue;

                        } else {
                            sleepUninterruptedly(difference);
                        }
                    } else {
                        sleepUninterruptedly(sleepThreadSwitch);
                        offerEntry(taskEntry);
                        continue;
                    }
                }

                try {
                    taskEntry.invoke();
                } catch (final Exception e) {
                    logger.log(Level.SEVERE, "Error on handling Task on Thread [" + getName() + ']', e);
                }

                if (checkEntry()) {
                    taskEntry = null;
                }
            }
        }

        private synchronized void sleepUninterruptedly(final long millis) {
            try {
                Thread.sleep(millis);
            } catch (final InterruptedException ignored) {
            }
        }

        private void offerEntry(final TaskEntry<?> entry) {
            taskEntries.offer(taskEntry);
            taskEntry = null;
        }


        private boolean checkEntry() {
            if (taskEntry.repeat == -1) {
                offerEntry(taskEntry);
                return false;
            }

            if (taskEntry.repeat > 0) {
                offerEntry(taskEntry);
                return false;
            }

            return true;
        }

        public TaskEntry<?> getTaskEntry() {
            return taskEntry;
        }

    }

}
