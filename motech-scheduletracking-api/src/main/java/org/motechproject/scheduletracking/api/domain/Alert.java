package org.motechproject.scheduletracking.api.domain;

import org.motechproject.valueobjects.WallTime;

public class Alert {

    private WallTime offset;
    private WallTime interval;
    private int count;
    private int index;

    public Alert(WallTime offset, WallTime interval, int count, int index) {
        this.offset = offset;
        this.interval = interval;
        this.count = count;
        this.index = index;
    }

    public WallTime getOffset() {
        return offset;
    }

    public WallTime getInterval() {
        return interval;
    }

    public int getCount() {
        return count;
    }

    public int getIndex() {
        return index;
    }
}
