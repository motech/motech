package org.motechproject.scheduletracking.api.domain;

import org.joda.time.Period;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MilestoneWindow implements Serializable {
    private WindowName name;
    private Period period;
    private List<Alert> alerts = new ArrayList<Alert>();

    public MilestoneWindow(WindowName name, Period period) {
        this.name = name;
        this.period = period;
    }

    public WindowName getName() {
        return name;
    }

    public Period getPeriod() {
        return period;
    }

    public void addAlerts(Alert... alertsList) {
        alerts.addAll(Arrays.asList(alertsList));
    }

    public List<Alert> getAlerts() {
        return alerts;
    }
}
