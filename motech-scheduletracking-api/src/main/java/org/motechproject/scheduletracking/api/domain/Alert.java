package org.motechproject.scheduletracking.api.domain;

import org.motechproject.valueobjects.WallTime;

public class Alert {

    private WallTime interval;
    private int repeatCount;

    public Alert(WallTime interval, int repeatCount) {
        this.interval = interval;
        this.repeatCount = repeatCount;
    }

    public WallTime getInterval() {
        return interval;
    }

    public int getRepeatCount() {
        return repeatCount;
    }
}
