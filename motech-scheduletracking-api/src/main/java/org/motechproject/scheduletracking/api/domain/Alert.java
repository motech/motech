package org.motechproject.scheduletracking.api.domain;

import java.util.HashMap;
import java.util.Map;

public class Alert {
    private WindowName windowName;
    private String milestoneName;
    private Map<String, String> data = new HashMap<String, String>();

    public Alert(WindowName windowName, Milestone milestone) {
        this.windowName = windowName;
        this.milestoneName = milestone.getName();
        data = milestone.getData();
    }

    public WindowName getWindowName() {
        return windowName;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public Map<String, String> getData() {
        return data;
    }
}
