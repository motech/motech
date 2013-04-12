package org.motechproject.commons.api;

public class NanoStopWatch {

    private long start;

    public NanoStopWatch() {
    }

    public NanoStopWatch start() {
        start = System.nanoTime();
        return this;
    }

    public long duration() {
        return System.nanoTime() - start;
    }
}
