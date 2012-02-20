package org.motechproject.appointments.api.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;
import java.util.UUID;

public class Reminder {
    public enum IntervalUnits {SECONDS, MINUTES, HOURS, DAYS, WEEKS}

    @JsonProperty
    private String id;
    @JsonProperty
    private Date startDate;
    @JsonProperty
    private Date endDate;
    @JsonProperty
    private int intervalCount;
    @JsonProperty
    private IntervalUnits units;
    @JsonProperty
    private int repeatCount;

    public Reminder() {
        this.id = UUID.randomUUID().toString();
    }

    public String id() {
        return id;
    }

    public Date startDate() {
        return startDate;
    }

    public Reminder startDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date endDate() {
        return endDate;
    }

    public Reminder endDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public int intervalCount() {
        return intervalCount;
    }

    public Reminder intervalCount(int intervalCount) {
        this.intervalCount = intervalCount;
        return this;
    }

    public IntervalUnits units() {
        return units;
    }

    public Reminder units(IntervalUnits units) {
        this.units = units;
        return this;
    }

    public int repeatCount() {
        return repeatCount;
    }

    public Reminder repeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }

    @JsonIgnore
    public long intervalSeconds() {
        long seconds = -1;

        if (IntervalUnits.SECONDS == units) {
            seconds = intervalCount;
        }

        if (IntervalUnits.MINUTES == units) {
            seconds = (intervalCount * 60);
        }

        if (IntervalUnits.HOURS == units) {
            seconds = (intervalCount * 60 * 60);
        }

        if (IntervalUnits.DAYS == units) {
            seconds = (intervalCount * 60 * 60 * 24);
        }

        if (IntervalUnits.WEEKS == units) {
            seconds = (intervalCount * 60 * 60 * 24 * 7);
        }

        return seconds;
    }
}