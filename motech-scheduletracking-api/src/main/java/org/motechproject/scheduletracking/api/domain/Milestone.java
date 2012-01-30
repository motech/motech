package org.motechproject.scheduletracking.api.domain;

import org.motechproject.valueobjects.WallTime;
import sun.jvm.hotspot.types.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Milestone implements Serializable {

    private String name;
    private Map<String, String> data = new HashMap<String, String>();

    private List<MilestoneWindow> windows = new ArrayList<MilestoneWindow>();

    public Milestone(String name, WallTime earliest, WallTime due, WallTime late, WallTime max) {
        this.name = name;
        createMilestoneWindows(earliest, due, late, max);
    }

    private void createMilestoneWindows(WallTime earliest, WallTime due, WallTime late, WallTime max) {
        windows.add(new MilestoneWindow(WindowName.Waiting, new WallTime(0, earliest.getUnit()), earliest));
        windows.add(new MilestoneWindow(WindowName.Upcoming, earliest, due));
        windows.add(new MilestoneWindow(WindowName.Due, due, late));
        windows.add(new MilestoneWindow(WindowName.Late, late, max));
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
}
