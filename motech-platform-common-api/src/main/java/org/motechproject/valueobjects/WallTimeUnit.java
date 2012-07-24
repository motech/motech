package org.motechproject.valueobjects;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Weeks;

public enum WallTimeUnit {
    Week(7) {
        public Period toPeriod(int unit) {
            return Weeks.weeks(unit).toPeriod();
        }
    }, Day(1) {
        public Period toPeriod(int unit) {
            return Days.days(unit).toPeriod();
        }
    }, Hour(0, 1) {
        public Period toPeriod(int unit) {
            return Hours.hours(unit).toPeriod();
        }
    }, Minute(0, 0, 1) {
        public Period toPeriod(int unit) {
            return Minutes.minutes(unit).toPeriod();
        }
    };

    private int days;
    private int hours;
    private int minutes;

    WallTimeUnit(int days) {
        this.days = days;
        this.hours = days * 24;
        this.minutes = hours * 60;
    }

    WallTimeUnit(int days, int hours) {
        this.days = days;
        this.hours = hours;
        this.minutes = hours * 60;
    }

    WallTimeUnit(int days, int hours, int minutes) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    public abstract Period toPeriod(int unit);
}
