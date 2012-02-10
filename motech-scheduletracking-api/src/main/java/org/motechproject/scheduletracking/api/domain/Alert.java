package org.motechproject.scheduletracking.api.domain;

import org.motechproject.valueobjects.WallTime;

public class Alert {

    private WallTime offset;
    private WallTime interval;
    private int repeatCount;
    private int index;

    public Alert(WallTime offset, WallTime interval, int repeatCount, int index) {
        this.offset = offset;
        this.interval = interval;
        this.repeatCount = repeatCount;
        this.index = index;
    }

    public WallTime getOffset() {
        return offset;
    }

    public WallTime getInterval() {
        return interval;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public int getIndex() {
        return index;
    }
}
