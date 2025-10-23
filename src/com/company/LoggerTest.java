package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LoggerTest {
    static final String LOG_FILE = "application.log";

    public static void main(String[] args) throws Exception {
        // Clean up old log
        File logFile = new File(LOG_FILE);
        if (logFile.exists()) {
            logFile.delete();
        }

        final int THREADS = 8;
        final int WRITES_PER_THREAD = 30;
        final CountDownLatch latch = new CountDownLatch(THREADS);
        final ThreadSafeLogger logger = new ThreadSafeLogger(LOG_FILE);
        List<Thread> threads = new ArrayList<>();

        // Simulate the logging scenario
        for (int i = 0; i < THREADS; i++) {
            final int threadNum = i;
            Thread t = new Thread(() -> {
                try {
                    for (int j = 0; j < WRITES_PER_THREAD; j++) {
                        // Add artificial variance in thread timing
                        if (j % 5 == 0) {
                            Thread.sleep((long) (Math.random() * 10));
                        }
                        logger.log(String.format("Message %02d from logger %d", j, threadNum));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }, "LoggerThread-" + i);
            threads.add(t);
        }

        threads.forEach(Thread::start);

        latch.await();
        logger.close();

        // Validate the log output
        System.out.println("\n---- Log file contents ----");
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                lineCount++;
                // Check that every message comes as a whole line
                if (!line.matches("\\[.+\\]\\[.+\\] Message \\d{2} from logger \\d+")) {
                    throw new AssertionError("Malformed log entry detected: " + line);
                }
            }
            if (lineCount != THREADS * WRITES_PER_THREAD) {
                throw new AssertionError("Missing log entries. Expected " + (THREADS * WRITES_PER_THREAD) + ", found " + lineCount);
            }
            System.out.println("\nAll log entries are properly formatted and there are no interleaved entries!");
        }
    }
}
