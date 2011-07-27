package org.motechproject.scheduletracking.api.domain;

import java.util.Dictionary;
import java.util.Hashtable;

public class Milestone {
    private String name;
    private String referenceDate;
    private Dictionary<WindowName, MilestoneWindow> windows = new Hashtable<WindowName, MilestoneWindow>();

    public Milestone(String name, String referenceDate) {
        this.name = name;
        this.referenceDate = referenceDate;
    }

    public void addMilestoneWindow(WindowName windowBoundary, MilestoneWindow milestoneWindow) {
        windows.put(windowBoundary, milestoneWindow);
    }

    public MilestoneWindow window(WindowName windowName) {
        return windows.get(windowName);
    }
}
