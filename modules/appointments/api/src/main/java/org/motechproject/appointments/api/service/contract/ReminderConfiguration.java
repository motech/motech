package org.motechproject.appointments.api.service.contract;

/**
 * \ingroup Appointments
 *
 * Specification for reminders, allows different time units and parameters such as start date and repeat count
 */
public class ReminderConfiguration {
    /**
     * specifies the unit for interval. Can specify units - seconds, minutes, hours, days, weeks
     */
    public enum IntervalUnit {
        SECONDS, MINUTES, HOURS, DAYS, WEEKS
    }

    private int remindFrom;

    private int intervalCount;

    private IntervalUnit intervalUnit;

    private int repeatCount;

    public int getRemindFrom() {
        if (intervalUnit == null) {
            return remindFrom;
        }

        if (this.intervalUnit.equals(IntervalUnit.MINUTES)) {
            return remindFrom * 60;
        } else if (this.intervalUnit.equals(IntervalUnit.HOURS)) {
            return remindFrom * 60 * 60;
        } else if (this.intervalUnit.equals(IntervalUnit.DAYS)) {
            return remindFrom * 60 * 60 * 24;
        } else if (this.intervalUnit.equals(IntervalUnit.WEEKS)) {
            return remindFrom * 60 * 60 * 24 * 7;
        }
        return remindFrom;
    }

    /**
     * Reminder start date : number of days before due date
     *
     * @param remindFrom
     * @return
     */
    public ReminderConfiguration setRemindFrom(int remindFrom) {
        this.remindFrom = remindFrom;
        return this;
    }

    public int getIntervalCount() {
        return intervalCount;
    }

    /**
     * Duration between two reminders, used along with {@link org.motechproject.appointments.api.service.contract.ReminderConfiguration#getIntervalCount() intervalCount}
     *
     * @param intervalCount
     * @return
     */
    public ReminderConfiguration setIntervalCount(int intervalCount) {
        this.intervalCount = intervalCount;
        return this;
    }

    public IntervalUnit getIntervalUnit() {
        return intervalUnit;
    }

    /**
     * Sets the interval unit for intervalCount
     *
     * @param intervalUnit specify unit {@link IntervalUnit}
     * @return
     * @see IntervalUnit
     */
    public ReminderConfiguration setIntervalUnit(IntervalUnit intervalUnit) {
        this.intervalUnit = intervalUnit;
        return this;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    /**
     * Sets the repeat count for reminder
     *
     * @param repeatCount number of times the reminder should be triggered
     * @return
     */
    public ReminderConfiguration setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }
}
