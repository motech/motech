package org.motechproject.scheduletracking.api.domain;

import org.motechproject.valueobjects.WallTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MilestoneWindow implements Serializable {

    private WindowName name;

    private WallTime begin;
    private WallTime end;

    private List<Alert> alerts = new ArrayList<Alert>();

    public MilestoneWindow(WindowName name, WallTime begin, WallTime end) {
        this.name = name;
        this.begin = begin;
        this.end = end;
    }

    public WindowName getName() {
        return name;
    }

    public void addAlerts(Alert... alertsList) {
        alerts.addAll(Arrays.asList(alertsList));
    }

    public List<Alert> getAlerts() {
        return alerts;
    }
}
