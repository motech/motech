package org.motechproject.metrics.api;

import java.util.concurrent.TimeUnit;

/**
 * Represents a metric that collects the durations of timed operations.
 */
public interface Timer extends Metric, Metered, Sampling {
    /**
     * An object that represents a timing context.
     */
    interface Context {
        /**
         * Updates the timer with the difference between the current and initial time.
         */
        void stop();
    }

    /**
     * Add a recorded duration.
     *
     * @param duration the length of the duration
     * @param unit the time unit of the duration
     */
    void update(long duration, TimeUnit unit);

    /**
     * Initiate a new timing context.
     *
     * @return the context
     */
    Context time();
}
