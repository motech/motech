package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.now;

public class Milestone implements Serializable {
    private static final long serialVersionUID = 2041937390099820322L;
    @JsonProperty
    private String name;
    @JsonProperty
    private Map<String, String> data = new HashMap<>();
    @JsonProperty
    private List<MilestoneWindow> windows = new ArrayList<>();

    private Milestone() {
    }

    public Milestone(String name, Period earliest, Period due, Period late, Period max) {
        this.name = name;
        createMilestoneWindows(earliest, due, late, max);
    }

    private void createMilestoneWindows(Period earliest, Period due, Period late, Period max) {
        windows.add(new MilestoneWindow(WindowName.earliest, earliest));
        windows.add(new MilestoneWindow(WindowName.due, due));
        windows.add(new MilestoneWindow(WindowName.late, late));
        windows.add(new MilestoneWindow(WindowName.max, max));
    }

    @JsonIgnore
    public List<MilestoneWindow> getMilestoneWindows() {
        return windows;
    }

    @JsonIgnore
    public MilestoneWindow getMilestoneWindow(WindowName windowName) {
        for (MilestoneWindow window : windows) {
            if (window.getName().equals(windowName)) {
                return window;
            }
        }
        return null;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @JsonIgnore
    public Map<String, String> getData() {
        return data;
    }

    public void addAlert(WindowName windowName, Alert... alertList) {
        getMilestoneWindow(windowName).addAlerts(alertList);
    }

    @JsonIgnore
    public List<Alert> getAlerts() {
        List<Alert> alerts = new ArrayList<Alert>();
        for (MilestoneWindow window : windows) {
            alerts.addAll(window.getAlerts());
        }
        return alerts;
    }

    @JsonIgnore
    public Period getMaximumDuration() {
        return getWindowEnd(WindowName.max);
    }

    @JsonIgnore
    public Period getWindowStart(WindowName windowName) {
        MutablePeriod period = new MutablePeriod();
        for (MilestoneWindow window : windows) {
            if (window.getName().equals(windowName)) {
                break;
            }
            period.add(window.getPeriod());
        }
        return period.toPeriod();
    }

    @JsonIgnore
    public Period getWindowEnd(WindowName windowName) {
        MutablePeriod period = new MutablePeriod();
        for (MilestoneWindow window : windows) {
            period.add(window.getPeriod());
            if (window.getName().equals(windowName)) {
                break;
            }
        }
        return period.toPeriod();
    }

    @JsonIgnore
    public Period getWindowDuration(WindowName windowName) {
        return getWindowEnd(windowName).minus(getWindowStart(windowName));
    }

    public boolean windowElapsed(WindowName windowName, DateTime milestoneStartDateTime) {
        Period endOffset = getWindowEnd(windowName);
        return !now().isBefore(milestoneStartDateTime.plus(endOffset));
    }
}
