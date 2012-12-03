package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.Period;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MilestoneWindow implements Serializable {

    @JsonProperty
    private WindowName name;
    @JsonProperty
    private Period period;
    @JsonProperty
    private List<Alert> alerts = new ArrayList<Alert>();

    private MilestoneWindow() {
    }

    public MilestoneWindow(WindowName name, Period period) {
        this.name = name;
        this.period = period;
    }

    @JsonIgnore
    public WindowName getName() {
        return name;
    }

    @JsonIgnore
    public Period getPeriod() {
        return period;
    }

    public void addAlerts(Alert... alertsList) {
        alerts.addAll(Arrays.asList(alertsList));
    }

    @JsonIgnore
    public List<Alert> getAlerts() {
        return alerts;
    }
}
