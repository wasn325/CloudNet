package de.dytanic.cloudnet.lib.scheduler;

import de.dytanic.cloudnet.lib.utility.threading.Callback;

import java.util.concurrent.Callable;

public class TaskEntry<T> {

    private final TaskEntryFuture<T> future;
    protected volatile Callable<T> task;
    protected volatile T value;
    protected Callback<T> callback;
    protected long delayTimeOut, repeat, delay;
    protected boolean completed;

    public TaskEntry(final Callable<T> task, final Callback<T> complete, final long delay, final long repeat) {

        this.task = task;
        this.callback = complete;
        this.delay = delay;
        this.delayTimeOut = System.currentTimeMillis() + delay;
        this.repeat = repeat;
        this.future = new TaskEntryFuture<>(this, false);
    }


    protected void invoke() throws Exception {

        if (task == null) {
            return;
        }

        final T val = task.call();

        value = val;

        if (callback != null) {
            callback.call(val);
        }

        if (repeat != -1 && repeat != 0) {
            repeat--;
        }

        if (repeat != 0) {
            this.delayTimeOut = System.currentTimeMillis() + delay;
        } else {
            completed = true;

            if (future.waits) {
                synchronized (future) {
                    future.notifyAll();
                }
            }
        }
    }


    public Callback<T> getCallback() {
        return callback;
    }


    public long getDelayTimeOut() {
        return delayTimeOut;
    }


    public long getRepeat() {
        return repeat;
    }


    protected TaskEntryFuture<T> drop() {
        return future;
    }


    public boolean isCompleted() {
        return completed;
    }

}
