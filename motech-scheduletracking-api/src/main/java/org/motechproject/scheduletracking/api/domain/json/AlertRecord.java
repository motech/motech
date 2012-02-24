package org.motechproject.scheduletracking.api.domain.json;

public class AlertRecord {
    private String window;
    private String offset;
    private String interval;
    private String count;

    public String offset() {
        return offset;
    }

    public String interval() {
        return interval;
    }

    public String window() {
        return window;
    }

    public String count() {
        return count;
    }
}
