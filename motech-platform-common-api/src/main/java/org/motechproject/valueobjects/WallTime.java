package org.motechproject.valueobjects;

import org.joda.time.Period;
import org.motechproject.util.TimeIntervalParser;

import java.io.Serializable;

public class WallTime implements Serializable {

    Period period;

    public WallTime(String userReadableForm) {
        period = new TimeIntervalParser().parse(userReadableForm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return period.equals(((WallTime) o).asPeriod());
    }

    @Override
    public int hashCode() {
        return period.hashCode();
    }

    public int inDays() {
        return period.toStandardDays().getDays();
    }

    public int getHours() {
        return period.getHours();
    }

    public int getMinutes() {
        return period.getMinutes();
    }

    public int inMinutes() {
        return period.toStandardMinutes().getMinutes();
    }

    public long inMillis() {
        return period.toStandardSeconds().getSeconds() * 1000;
    }

    public Period asPeriod() {
        return period;
    }

    public boolean isLessThanADay() {
        return period.toStandardDays().isLessThan(new Period(0, 0, 0, 1, 0, 0, 0, 0).toStandardDays());
    }
}
