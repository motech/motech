package org.motechproject.scheduletracking.api.domain;

import org.joda.time.Period;

public class Alert {
    private Period offset;
    private Period interval;
    private int count;
    private int index;
    private boolean floating;

    public Alert(Period offset, Period interval, int count, int index, boolean floating) {
        this.offset = offset;
        this.interval = interval;
        this.count = count;
        this.index = index;
        this.floating = floating;
    }

    public int getCount() {
        return count;
    }

    public Period getOffset() {
        return offset;
    }

    public Period getInterval() {
        return interval;
    }

    public int getIndex() {
        return index;
    }

    public boolean isFloating() {
        return floating;
    }
}
