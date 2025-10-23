package com.company;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadSafeLogger implements Closeable {
    private final PrintWriter writer;
    private final Object lock = new Object();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public ThreadSafeLogger(String filename) throws IOException {
        this.writer = new PrintWriter(new FileWriter(filename, true), true);
    }

    /**
     * Writes a log entry with a timestamp and thread name in a thread-safe manner.
     * Ensures that multiple threads writing at the same time do NOT interleave their messages,
     * and always acquires the lock in the same, single place (avoiding deadlocks).
     *
     * @param message The message to log
     */
    public void log(String message) {
        String threadName = Thread.currentThread().getName();
        String timestamp = sdf.format(new Date());
        synchronized (lock) {
            writer.printf("[%s][%s] %s%n", timestamp, threadName, message);
            writer.flush();
        }
    }

    @Override
    public void close() {
        synchronized (lock) {
            writer.close();
        }
    }
}
