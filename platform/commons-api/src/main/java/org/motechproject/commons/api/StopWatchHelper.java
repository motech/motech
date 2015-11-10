package org.motechproject.commons.api;

import org.apache.commons.lang.time.StopWatch;

public final class StopWatchHelper {

    public static void restart(StopWatch stopWatch) {
        stopWatch.reset();
        stopWatch.start();
    }

    private StopWatchHelper() {
    }
}
