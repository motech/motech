package org.motechproject.scheduletracking.api.domain;

import org.motechproject.valueobjects.WallTime;

public class Alert {

    private WallTime startOffset;
    private WallTime interval;
    private int repeatCount;

    public Alert(WallTime startOffset, WallTime interval, int repeatCount) {
        this.startOffset = startOffset;
        this.interval = interval;
        this.repeatCount = repeatCount;
    }
}
