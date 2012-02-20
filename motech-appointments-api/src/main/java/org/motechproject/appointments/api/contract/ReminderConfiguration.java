package org.motechproject.appointments.api.contract;

public class ReminderConfiguration {
    public enum IntervalUnit {SECONDS, MINUTES, HOURS, DAYS, WEEKS}

    private int remindFrom;

    private int remindTill;

    private int intervalCount;

    private IntervalUnit intervalUnit;

    private int repeatCount;

    public int getRemindFrom() {
        return remindFrom;
    }

    public ReminderConfiguration setRemindFrom(int remindFrom) {
        this.remindFrom = remindFrom;
        return this;
    }

    public int getRemindTill() {
        return remindTill;
    }

    public ReminderConfiguration setRemindTill(int remindTill) {
        this.remindTill = remindTill;
        return this;
    }

    public int getIntervalCount() {
        return intervalCount;
    }

    public ReminderConfiguration setIntervalCount(int intervalCount) {
        this.intervalCount = intervalCount;
        return this;
    }

    public IntervalUnit getIntervalUnit() {
        return intervalUnit;
    }

    public ReminderConfiguration setIntervalUnit(IntervalUnit intervalUnit) {
        this.intervalUnit = intervalUnit;
        return this;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public ReminderConfiguration setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }
}
