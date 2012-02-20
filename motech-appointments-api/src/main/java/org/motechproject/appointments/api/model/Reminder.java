package org.motechproject.appointments.api.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;
import java.util.UUID;

public class Reminder {

    @JsonProperty
    private String id;
    @JsonProperty
    private Date startDate;
    @JsonProperty
    private Date endDate;
    @JsonProperty
    private long intervalSeconds;
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

    public long intervalSeconds() {
        return intervalSeconds;
    }

    public Reminder intervalSeconds(long intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
        return this;
    }

    public int repeatCount() {
        return repeatCount;
    }

    public Reminder repeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }
}