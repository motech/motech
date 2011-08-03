package org.motechproject.valueobjects;

import org.joda.time.Days;
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
    };

    public int days;

    WallTimeUnit(int days) {
        this.days = days;
    }

    public abstract Period toPeriod(int unit);
}
