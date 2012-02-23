package org.motechproject.scheduletracking.api.domain.json;

public class ScheduleWindowsRecord {

    private String earliest;
    private String due;
    private String late;
    private String max;

    public String earliest() {
        return earliest.toLowerCase();
    }

    public String due() {
        return due.toLowerCase();
    }

    public String late() {
        return late.toLowerCase();
    }

    public String max() {
        return max.toLowerCase();
    }
}
