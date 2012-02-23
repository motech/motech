package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.motechproject.util.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Milestone implements Serializable {

    private String name;
    private Map<String, String> data = new HashMap<String, String>();
    private List<MilestoneWindow> windows = new ArrayList<MilestoneWindow>();

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

    public List<MilestoneWindow> getMilestoneWindows() {
        return windows;
    }

    public MilestoneWindow getMilestoneWindow(WindowName windowName) {
        for (MilestoneWindow window : windows)
            if (window.getName().equals(windowName))
                return window;
        return null;
    }

    public String getName() {
        return name;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void addAlert(WindowName windowName, Alert... alertList) {
        getMilestoneWindow(windowName).addAlerts(alertList);
    }

    public List<Alert> getAlerts() {
        List<Alert> alerts = new ArrayList<Alert>();
        for (MilestoneWindow window : windows)
            alerts.addAll(window.getAlerts());
        return alerts;
    }

    public Period getMaximumDuration() {
        return getWindowEnd(WindowName.max);
    }

    public Period getWindowStart(WindowName name) {
        MutablePeriod period = new MutablePeriod();
        for (MilestoneWindow window : windows) {
            if (window.getName().equals(name)) {
                break;
            }
            period.add(window.getPeriod());
        }
        return period.toPeriod();
    }

    public Period getWindowEnd(WindowName name) {
        MutablePeriod period = new MutablePeriod();
        for (MilestoneWindow window : windows) {
            period.add(window.getPeriod());
            if (window.getName().equals(name))
                break;
        }
        return period.toPeriod();
    }

    public boolean windowElapsed(WindowName windowName, LocalDate milestoneStartDate) {
        LocalDate today = DateUtil.today();
        Period endOffset = getWindowEnd(windowName);
        return !today.isBefore(milestoneStartDate.plus(endOffset));
    }
}
