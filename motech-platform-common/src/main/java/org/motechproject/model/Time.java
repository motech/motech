package org.motechproject.model;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;

public class Time {

    private Integer hour;

    private Integer minute;

    public Time() {
    }

    public Time(Integer hour, Integer minute) {
        this.hour = hour;
        this.minute = minute;
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

    @JsonIgnore
    public DateTime getDateTime(DateTime today) {
        return new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), hour, minute, 0, 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hour == null) ? 0 : hour.hashCode());
        result = prime * result + ((minute == null) ? 0 : minute.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Time other = (Time) obj;
        if (hour == null) {
            if (other.hour != null)
                return false;
        } else if (!hour.equals(other.hour))
            return false;
        if (minute == null) {
            if (other.minute != null)
                return false;
        } else if (!minute.equals(other.minute))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Time [hour=" + hour + ", minute=" + minute + "]";
    }

    public static Time parseTime(String time, String separator) {
        String[] strings = StringUtils.split(time, separator);
        if (strings.length != 2) throw new IllegalArgumentException();
        return new Time(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
    }
}
