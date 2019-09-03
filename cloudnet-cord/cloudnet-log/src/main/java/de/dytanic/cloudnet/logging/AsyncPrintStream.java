/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.logging;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Asynchronous print stream that takes print statements without blocking.
 */
public class AsyncPrintStream extends PrintStream {

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Thread worker = new WorkerThread(queue);

    /**
     * Constructs a new asynchronous print stream.
     *
     * @param out the output stream to write to
     *
     * @throws UnsupportedEncodingException when UTF-8 is mysteriously unavailable
     */
    public AsyncPrintStream(final OutputStream out) throws UnsupportedEncodingException {
        super(out, true, StandardCharsets.UTF_8.name());
    }

    public Thread getWorker() {
        return worker;
    }

    public BlockingQueue<Runnable> getQueue() {
        return queue;
    }

    @Override
    public void print(final boolean x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    @Override
    public void print(final char x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    @Override
    public void print(final int x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    private void print0(final int x) {
        super.print(x);
    }

    @Override
    public void print(final long x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    private void print0(final long x) {
        super.print(x);
    }

    @Override
    public void print(final float x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    @Override
    public void print(final double x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    private void print0(final double x) {
        super.print(x);
    }

    @Override
    public void print(final char[] x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    @Override
    public void print(final String x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    private void print0(final String x) {
        super.print(x);
    }

    @Override
    public void print(final Object x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    @Override
    public void println() {
        queue.offer(this::println0);
    }

    private void println0() {
        super.println();
    }

    @Override
    public void println(final boolean x) {
        queue.offer(() -> println0(x));
    }

    @Override
    public void println(final char x) {
        queue.offer(() -> println0(x));
    }

    @Override
    public void println(final int x) {
        queue.offer(() -> println0(x));
    }

    private void println0(final int x) {
        super.println(x);
    }

    @Override
    public void println(final long x) {
        queue.offer(() -> println0(x));
    }

    private void println0(final long x) {
        super.println(x);
    }

    @Override
    public void println(final float x) {
        queue.offer(() -> println0(x));
    }

    @Override
    public void println(final double x) {
        queue.offer(() -> println0(x));
    }

    private void println0(final double x) {
        super.println(x);
    }

    @Override
    public void println(final char[] x) {
        queue.offer(() -> println0(x));
    }

    @Override
    public void println(final String x) {
        queue.offer(() -> println0(x));
    }

    private void println0(final String x) {
        super.println(x);
    }

    @Override
    public void println(final Object x) {
        queue.offer(() -> println0(x));
    }

    private void println0(final Object x) {
        super.println(x);
    }

    private void println0(final char[] x) {
        super.println(x);
    }

    private void println0(final float x) {
        super.println(x);
    }

    private void println0(final char x) {
        super.println(x);
    }

    private void println0(final boolean x) {
        super.println(x);
    }

    private void print0(final Object x) {
        super.print(x);
    }

    private void print0(final char[] x) {
        super.print(x);
    }

    private void print0(final float x) {
        super.print(x);
    }

    private void print0(final char x) {
        super.print(x);
    }

    private void print0(final boolean x) {
        super.print(x);
    }

    /**
     * A worker thread for the {@link AsyncPrintStream}.
     */
    private static class WorkerThread extends Thread {

        private final BlockingQueue<Runnable> queue;

        /**
         * Constructs an worker thread that takes work from {@code queue}.
         * Automatically started until interrupted.
         *
         * @param queue the blocking queue to take work from
         */
        WorkerThread(final BlockingQueue<Runnable> queue) {
            this.queue = queue;
            setPriority(Thread.MIN_PRIORITY);
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    queue.take().run();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
