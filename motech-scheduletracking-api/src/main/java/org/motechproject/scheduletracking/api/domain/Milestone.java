package org.motechproject.scheduletracking.api.domain;

import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.valueobjects.WallTime;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Milestone implements Referenceable {
    private String name;
    private Referenceable refersTo;
    private Map<WindowName, MilestoneWindow> windows = new LinkedHashMap<WindowName, MilestoneWindow>();
    private Map<String, String> data;

    public Milestone(String name, Referenceable refersTo, WallTime earliest, WallTime due, WallTime late, WallTime max) {
        this.name = name;
        this.refersTo = refersTo;

        windows.put(WindowName.Upcoming, new MilestoneWindow(earliest, due));
        windows.put(WindowName.Due, new MilestoneWindow(due, late));
        windows.put(WindowName.Late, new MilestoneWindow(late, max == null ? late : max));
    }

    public MilestoneWindow window(WindowName windowName) {
        return windows.get(windowName);
    }

    public String name() {
        return name;
    }

    public Referenceable refersTo() {
        return refersTo;
    }

    public WindowName applicableWindow(Enrollment enrollment) {
        Set<Map.Entry<WindowName, MilestoneWindow>> entries = windows.entrySet();
        for (Map.Entry<WindowName, MilestoneWindow> entry : entries) {
            MilestoneWindow milestoneWindow = entry.getValue();
            if (milestoneWindow.isApplicableTo(enrollment)) return entry.getKey();
        }
        return WindowName.Past;
    }

    public void data(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> data() {
        return data;
    }
}
