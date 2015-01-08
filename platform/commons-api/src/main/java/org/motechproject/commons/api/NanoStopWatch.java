package org.motechproject.commons.api;

/**
 * Simple class for measuring time.
 */
public class NanoStopWatch {

    private long start;

    /**
     * Default constructor.
     */
    public NanoStopWatch() {
    }

    /**
     * Starts the timer.
     *
     * @return this instance of class
     */
    public NanoStopWatch start() {
        start = System.nanoTime();
        return this;
    }

    /**
     * Return time elapsed since this timer was started.
     *
     * @return the time elapsed since timer started
     */
    public long duration() {
        return System.nanoTime() - start;
    }
}
