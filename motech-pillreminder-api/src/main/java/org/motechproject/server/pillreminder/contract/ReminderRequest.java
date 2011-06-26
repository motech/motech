package org.motechproject.server.pillreminder.contract;

public class ReminderRequest {
    private Integer hour;
    private Integer minute;
    private Integer repeatSize;
    private Integer repeatInterval;

    public ReminderRequest(Integer hour, Integer minute, Integer repeatSize, Integer repeatInterval) {
        this.hour = hour;
        this.minute = minute;
        this.repeatSize = repeatSize;
        this.repeatInterval = repeatInterval;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public Integer getRepeatSize() {
        return repeatSize;
    }

    public Integer getRepeatInterval() {
        return repeatInterval;
    }
}
