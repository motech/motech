package org.motechproject.commons.date.model;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

/**
 * Represents time as number of hours and minutes.
 */
public class Time implements Comparable<Time>, Serializable {
    private static final long serialVersionUID = -6049964979382913093L;
    private static final int TIME_TOKEN_MIN_LENGTH = 2;
    private static final int TIME_TOKEN_MAX_LENGTH = 3;
    private Integer hour;
    private Integer minute;

    /**
     * Constructor.
     */
    public Time() {
    }

    /**
     * Constructor.
     *
     * @param hour  the hour to be stored, not null
     * @param minute  the minute to be stored, not null
     */
    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Constructor.
     *
     * @param localTime the time to be stored, not null
     */
    public Time(LocalTime localTime) {
        this(localTime.getHourOfDay(), localTime.getMinuteOfHour());
    }

    /**
     * Constructor.
     *
     * @param timeStr  the time represented as {@code String}
     * @throws IllegalArgumentException if {@code timeStr} doesn't match "HH:MM" or "HH:MM Z" pattern
     */
    public Time(String timeStr) {
        if (isTimeContainsZone(timeStr)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("HH:mm Z");
            DateTime localTime = DateTime.parse(timeStr, dateTimeFormatter).withZone(DateTimeZone.UTC);
            this.hour = localTime.getHourOfDay();
            this.minute = localTime.getMinuteOfHour();
        } else {
            String[] tokens = timeStr.split(":");

            if (tokens.length < TIME_TOKEN_MIN_LENGTH || tokens.length > TIME_TOKEN_MAX_LENGTH) {
                throw new IllegalArgumentException("Invalid time string: " + timeStr);
            }

            try {
                this.hour = Integer.parseInt(tokens[0]);
                this.minute = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid time string: " + timeStr, e);
            }
        }
    }

    private boolean isTimeContainsZone(String timeStr) {
        return timeStr.length() == 11;
    }

    /**
     * Returns {@code String} representation of stored time.
     *
     * @return the time stored as a {@code String}
     */
    public String timeStr() {
        return String.format("%02d:%02d", hour, minute);
    }

    /**
     * Returns the hour of day for this time instance.
     * @return the hour of day
     */
    public Integer getHour() {
        return hour;
    }

    /**
     * Sets the hour of day for this time instance.
     * @param hour the hour of day
     */
    public void setHour(Integer hour) {
        this.hour = hour;
    }

    /**
     * Returns the minute of hour for this time instance.
     * @return the minute of hour
     */
    public Integer getMinute() {
        return minute;
    }

    /**
     * Sets the minute of hour for this time instance.
     * @param minute the minute of hour
     */
    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    /**
     * Creates {@code DateTime} instance with time stored in this object.
     *
     * @param dateTime  the {@code DateTime} to be used as base for the new {@code DateTime}
     * @return the {@code DateTime} with stored time
     */
    public DateTime toDateTime(DateTime dateTime) {
        return new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), hour, minute, 0, 0);
    }

    /**
     * Creates {@code DateTime} instance with time stored in this object.
     *
     * @param date the {@code LocalDate} to be used as base for the new {@code DateTime}
     * @return the {@code DateTime} with stored time
     */
    public DateTime toDateTime(LocalDate date) {
        return new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), hour, minute, 0, 0);
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Time other = (Time) obj;
        if (hour == null) {
            if (other.hour != null) {
                return false;
            }
        } else if (!hour.equals(other.hour)) {
            return false;
        }
        if (minute == null) {
            if (other.minute != null) {
                return false;
            }
        } else if (!minute.equals(other.minute)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return timeStr();
    }

    /**
     * Parses given {@code String} using the separator.
     *
     * @param time  the {@code String} to be parsed, null returns null
     * @param separator  the separator used to distinguish minute from hour, not null
     * @return the instance of {@code Time}
     * @throws IllegalArgumentException if {@code time} doesn't match "HH<separator>MM" pattern
     */
    public static Time parseTime(String time, String separator) {
        if (time == null) {
            return null;
        }
        String[] strings = StringUtils.split(time, separator);
        if (strings.length != 2) {
            throw new IllegalArgumentException();
        }
        return new Time(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
    }

    @Override
    public int compareTo(Time otherTime) {
        if (otherTime.getHour().equals(this.getHour())) {
            return this.getMinute().compareTo(otherTime.getMinute());
        }
        return this.getHour().compareTo(otherTime.getHour());
    }

    /**
     * Checks whether this is before the given time.
     *
     * @param other  the {@code Time} to be compared with this object
     * @return true if this time is before other, false otherwise
     */
    public boolean isBefore(Time other) {
        return compareTo(other) < 0;
    }

    /**
     * Checks whether this is after the given time.
     *
     * @param other  the {@code Time} to be compared with this object
     * @return true if this time is after other, false otherwise
     */
    public boolean isAfter(Time other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks whether this is before the given time.
     *
     * @param start  the {@code Time} to be compared with this object
     * @param end  the {@code Time} to be compared with this object
     * @return true if this time is between start and end, false otherwise
     */
    public boolean isBetween(Time start, Time end) {
        return isAfter(start) && isBefore(end);
    }

    /**
     * Creates instance of {@code Time} for given {@code String}.
     *
     * @param str  the {@code String} to be parsed to {@code Time}
     * @return the {@code Time} parsed from given {@code String}
     */
    public static Time valueOf(String str) {
        return new Time(str);
    }
}
