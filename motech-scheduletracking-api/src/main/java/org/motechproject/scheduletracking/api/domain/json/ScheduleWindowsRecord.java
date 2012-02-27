package org.motechproject.scheduletracking.api.domain.json;

import java.util.ArrayList;
import java.util.List;

public class ScheduleWindowsRecord {

    private List<String> earliest = new ArrayList<String>();
    private List<String> due = new ArrayList<String>();
    private List<String> late = new ArrayList<String>();
    private List<String> max = new ArrayList<String>();

    public List<String> earliest() {
        return earliest;
    }

    public List<String> due() {
        return due;
    }

    public List<String> late() {
        return late;
    }

    public List<String> max() {
        return max;
    }
}
