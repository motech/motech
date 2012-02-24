package org.motechproject.scheduletracking.api.domain.json;

public class ScheduleWindowsRecord {
    private String earliest;
    private String due;
    private String late;
    private String max;

    public String earliest() {
        return earliest;
    }

    public String due() {
        return due;
    }

    public String late() {
        return late;
    }

    public String max() {
        return max;
    }
}
