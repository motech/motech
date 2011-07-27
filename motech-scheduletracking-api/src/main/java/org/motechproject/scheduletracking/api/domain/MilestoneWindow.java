package org.motechproject.scheduletracking.api.domain;

import org.motechproject.valueobjects.WallTime;

import java.util.ArrayList;
import java.util.List;

public class MilestoneWindow {
    private WallTime begin;
    private WallTime end;

    private List<Alert> alerts = new ArrayList<Alert>();

    public MilestoneWindow(WallTime begin, WallTime end) {
        this.begin = begin;
        this.end = end;
    }

    public void addAlert(Alert alert) {
        alerts.add(alert);
    }
}
