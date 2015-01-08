package org.motechproject.commons.date.model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Represents a single day of week.
 */
public enum DayOfWeek {
    Monday(1, "MON"),
    Tuesday(2, "TUE"),
    Wednesday(3, "WED"),
    Thursday(4, "THU"),
    Friday(5, "FRI"),
    Saturday(6, "SAT"),
    Sunday(7, "SUN");

    private int value;
    private String shortName;

    private DayOfWeek(int value, String shortName) {
        this.value = value;
        this.shortName = shortName;
    }

    public int getValue() {
        return value;
    }

    /**
     * Returns value for cron expression.
     *
     * @return the number ready to be used with cron expression
     */
    public int getCronValue() {
        return value % 7 + 1;
    }

    /**
     * Returns {@code DayOfWeek} for given number.
     *
     * @param dayOfWeek  the day of week as a number
     * @return the {@code DayOfWeek}
     * @throws IllegalArgumentException if given number isn't between 1 and 7
     */
    public static DayOfWeek getDayOfWeek(int dayOfWeek) {
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.getValue() == dayOfWeek) {
                return day;
            }
        }

        throw new IllegalArgumentException("Not a valid day");
    }

    /**
     * Creates {@code DayOfWeek} for given date.
     * @param date  the {@code LocalDate} to be parsed to {@code DayOfWeek}
     * @return the {@code DayOfWeek} parsed from {@code LocalDate}
     */
    public static DayOfWeek getDayOfWeek(LocalDate date) {
        return DayOfWeek.getDayOfWeek(date.dayOfWeek().get());
    }

    public String getShortName() {
        return shortName;
    }

    /**
     * Creates {@code DayOfWeek} for given property.
     * @param property  the {@code DateTime.Property} to be parsed to {@code DayOfWeek}
     * @return the {@code DayOfWeek} parsed from {@code DateTime.Property}
     */
    public static DayOfWeek getDayOfWeek(DateTime.Property property) {
        return DayOfWeek.getDayOfWeek(property.get());
    }

    /**
     * Creates {@code DayOfWeek} for given string.
     *
     * @param text  the text to be parsed to {@code DayOfWeek}
     * @return the {@code DayOfWeek} created from text, null if {@code text} isn't equal to one of weekdays short names
     */
    public static DayOfWeek parse(String text) {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (dayOfWeek.name().equalsIgnoreCase(text) || dayOfWeek.getShortName().equalsIgnoreCase(text)) {
                return dayOfWeek;
            }
        }
        return null;
    }
}
