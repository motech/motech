package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.valueobjects.WallTime;

import java.io.Serializable;
import java.util.*;

public class Milestone implements Serializable {
    private String name;
    private Map<String, String> data = new HashMap<String, String>();
    private List<MilestoneWindow> windows = new ArrayList<MilestoneWindow>();
    private Milestone nextMilestone;

    public Milestone(String name, Milestone nextMilestone, WallTime earliest, WallTime due, WallTime late, WallTime max) {
        this.nextMilestone = nextMilestone;
        this.name = name;

        windows.add(new MilestoneWindow(WindowName.Waiting, new WallTime(0, earliest.getUnit()), earliest));
        windows.add(new MilestoneWindow(WindowName.Upcoming, earliest, due));
        windows.add(new MilestoneWindow(WindowName.Due, due, late));
        windows.add(new MilestoneWindow(WindowName.Late, late, max));
    }

    public Milestone(String name, WallTime earliest, WallTime due, WallTime late, WallTime max) {
        this(name, null, earliest, due, late, max);
    }

    public MilestoneWindow getMilestoneWindow(WindowName windowName) {
        for (MilestoneWindow window : windows)
            if (window.getName().equals(windowName))
                return window;
        return null;
    }

    public WindowName getApplicableWindow(LocalDate enrollmentDate) {
        for (MilestoneWindow window : windows)
            if (window.isApplicableTo(enrollmentDate))
                return window.getName();
        return WindowName.Past;
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

    public Milestone getNextMilestone() {
        return nextMilestone;
    }
}
