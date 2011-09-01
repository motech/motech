package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.valueobjects.WallTime;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Milestone extends Referenceable {
    private String name;
    private Map<WindowName, MilestoneWindow> windows = new LinkedHashMap<WindowName, MilestoneWindow>();
    private Map<String, String> data;

    public Milestone(String name, Referenceable next, WallTime earliest, WallTime due, WallTime late, WallTime max) {
        super(next);
        this.name = name;

        windows.put(WindowName.Waiting, new MilestoneWindow(new WallTime(0, earliest.getUnit()), earliest));
        windows.put(WindowName.Upcoming, new MilestoneWindow(earliest, due));
        windows.put(WindowName.Due, new MilestoneWindow(due, late));
        windows.put(WindowName.Late, new MilestoneWindow(late, max == null ? late : max));
    }

    public Milestone(String name, WallTime earliest, WallTime due, WallTime late, WallTime max) {
        this(name, null, earliest, due, late, max);
    }

    public MilestoneWindow window(WindowName windowName) {
        return windows.get(windowName);
    }

    public WindowName applicableWindow(LocalDate enrolledDate) {
        Set<Map.Entry<WindowName, MilestoneWindow>> entries = windows.entrySet();
        for (Map.Entry<WindowName, MilestoneWindow> entry : entries) {
            MilestoneWindow milestoneWindow = entry.getValue();
            if (milestoneWindow.isApplicableTo(enrolledDate)) return entry.getKey();
        }
        return WindowName.Past;
    }

    public String name() {
        return name;
    }

    public void data(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> data() {
        return data;
    }

    public Milestone getNextMilestone() {
        return (Milestone) getNext();
    }
}
