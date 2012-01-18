package org.motechproject.valueobjects;

import org.joda.time.Period;

public class WallTime {
    private final int value;
    private final WallTimeUnit unit;

    public WallTime(int value, WallTimeUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WallTime wallTime = (WallTime) o;

        return value == wallTime.value && unit == wallTime.unit;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }

    public int getValue() {
        return value;
    }

    public WallTimeUnit getUnit() {
        return unit;
    }

    public int inDays() {
        return unit.days * value;
    }

    public Period asPeriod() {
        return unit.toPeriod(value);
    }
}
