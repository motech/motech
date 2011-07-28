package org.motechproject.scheduletracking.api.domain;

import org.motechproject.valueobjects.WallTime;

import java.util.Dictionary;
import java.util.Hashtable;

public class Milestone {
    private String name;
    private String referenceDate;
    private Dictionary<WindowName, MilestoneWindow> windows = new Hashtable<WindowName, MilestoneWindow>();

    public Milestone(String name, String referenceDate, WallTime earliest, WallTime due, WallTime late, WallTime max) {
        this.name = name;
        this.referenceDate = referenceDate;

        windows.put(WindowName.Upcoming, new MilestoneWindow(earliest, due));
        windows.put(WindowName.Due, new MilestoneWindow(due, late));
        windows.put(WindowName.Late, new MilestoneWindow(late, max));
    }

    public MilestoneWindow window(WindowName windowName) {
        return windows.get(windowName);
    }
}
