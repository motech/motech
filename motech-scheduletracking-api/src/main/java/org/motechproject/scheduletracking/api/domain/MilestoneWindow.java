package org.motechproject.scheduletracking.api.domain;

import org.motechproject.valueobjects.WallTime;

import java.util.ArrayList;
import java.util.List;

public class MilestoneWindow {
    private WallTime begin;
    private WallTime end;

    private List<AlertConfiguration> alertConfigurations = new ArrayList<AlertConfiguration>();

    public MilestoneWindow(WallTime begin, WallTime end) {
        this.begin = begin;
        this.end = end;
    }

    public void addAlert(AlertConfiguration alertConfiguration) {
        alertConfigurations.add(alertConfiguration);
    }
}
