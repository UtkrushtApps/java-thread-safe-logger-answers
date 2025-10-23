# Solution Steps

1. Create a class ThreadSafeLogger that encapsulates a PrintWriter for writing to the log file.

2. Add a private final lock Object (not using file locks or multiple resources) to enforce mutual exclusion when writing to the file.

3. Format every log entry with a timestamp and the thread name (e.g., using java.text.SimpleDateFormat and Thread.currentThread().getName()).

4. Make the log(String message) method acquire the lock using synchronized (lock), and in the critical section, write the entire formatted log message (including timestamp, thread name, and user message) as a single atomic action, then flush the writer.

5. Implement proper closing of the file resource with close(), again using the same lock object (ensuring consistent locking order everywhere in the class to prevent deadlocks).

6. Simulate non-threadsafe behavior in a test by launching multiple threads where each writes multiple messages to the logger with random sleep intervals to make race conditions more likely.

7. After log writing, parse the file in the test to check that (1) each line is a fully formed message including timestamp and thread name, and (2) the number of lines matches the total expected, (3) there are no malformed or interleaved entries.

8. Print the file contents in the test and ensure all messages are properly formatted, with no interleaving, and that the test completes without deadlocking.

