package org.motechproject.commons.api;

import org.apache.commons.lang.time.StopWatch;

/**
 * A helper that allows to easily restart the Apache commons stop watch.
 */
public final class StopWatchHelper {

    /**
     * Restarts the provided {@link StopWatch} by calling {@link StopWatch#reset()} and {@link StopWatch#start()} on it.
     * @param stopWatch the stop watch to restart
     */
    public static void restart(StopWatch stopWatch) {
        stopWatch.reset();
        stopWatch.start();
    }

    private StopWatchHelper() {
    }
}
