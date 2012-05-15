package org.motechproject.valueobjects;

import org.joda.time.Days;
import org.joda.time.Hours;
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
    };

    public int days;
    public int hours;

    WallTimeUnit(int days) {
        this.days = days;
        this.hours = days * 24;
    }

    WallTimeUnit(int days, int hours) {
        this.days = days;
        this.hours = hours;
    }

    public abstract Period toPeriod(int unit);
}
