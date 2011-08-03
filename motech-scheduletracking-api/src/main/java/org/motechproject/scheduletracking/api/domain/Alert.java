package org.motechproject.scheduletracking.api.domain;

import java.util.HashMap;
import java.util.Map;

public class Alert {
    private WindowName windowName;
    private String milestoneName;
    private Map<String, String> data = new HashMap<String, String>();

    public Alert(WindowName windowName, Milestone milestone) {
        this.windowName = windowName;
        this.milestoneName = milestone.name();
        data = milestone.data();
    }

    public WindowName windowName() {
        return windowName;
    }

    public String milestoneName() {
        return milestoneName;
    }

    public Map<String, String> data() {
        return data;
    }
}
