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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        if (hour != null ? !hour.equals(reminder.hour) : reminder.hour != null) return false;
        if (minute != null ? !minute.equals(reminder.minute) : reminder.minute != null) return false;
        if (repeatInterval != null ? !repeatInterval.equals(reminder.repeatInterval) : reminder.repeatInterval != null)
            return false;
        if (repeatSize != null ? !repeatSize.equals(reminder.repeatSize) : reminder.repeatSize != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = hour != null ? hour.hashCode() : 0;
        result = 31 * result + (minute != null ? minute.hashCode() : 0);
        result = 31 * result + (repeatSize != null ? repeatSize.hashCode() : 0);
        result = 31 * result + (repeatInterval != null ? repeatInterval.hashCode() : 0);
        return result;
    }
}
