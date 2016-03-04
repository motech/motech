package org.motechproject.commons.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util class, that allows to put current thread to sleep.
 */
public final class ThreadSuspender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadSuspender.class);

    /**
     * Causes the currently executing thread to sleep (temporarily cease execution) for the specified number of
     * milliseconds, subject to the precision and accuracy of system timers and schedulers.
     *
     * @param millis the length of time to sleep in milliseconds
     * @param interruptedMessage the message to put in logs, in case the waiting gets interrupted
     */
    public static void sleep(int millis, String interruptedMessage) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error(interruptedMessage);
        }
    }

    /**
     * Causes the currently executing thread to sleep (temporarily cease execution) for the specified number of
     * milliseconds, subject to the precision and accuracy of system timers and schedulers.
     *
     * @param millis the length of time to sleep in milliseconds
     */
    public static void sleep(int millis) {
        sleep(millis, "Interrupted");
    }

    private ThreadSuspender() {
    }
}
