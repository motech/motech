package org.motechproject.server.pillreminder.domain;

import java.util.UUID;

public class Reminder {
    private String id;
    private Integer hour;
    private Integer minute;
    private Integer repeatSize;
    private Integer repeatInterval;

    public Reminder() {

    }

    public Reminder(Integer hour, Integer minute, Integer repeatSize, Integer repeatInterval) {
        this.id = UUID.randomUUID().toString();
        this.hour = hour;
        this.minute = minute;
        this.repeatSize = repeatSize;
        this.repeatInterval = repeatInterval;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getRepeatSize() {
        return repeatSize;
    }

    public void setRepeatSize(Integer repeatSize) {
        this.repeatSize = repeatSize;
    }

    public Integer getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(Integer repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

}
