package org.motechproject.valueobjects;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.Period;

import java.io.Serializable;

public class WallTime implements Serializable {
	@JsonProperty
    private int value;
	@JsonProperty
    private WallTimeUnit unit;

	// For ektorp
	private WallTime() {
	}

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
        if (value == 0 || unit == null)
            return 0;
        return unit.days * value;
    }

    public int inHours() {
        if (value == 0 || unit == null) {
            return 0;
        }

        return unit.hours * value;
    }

    public int inMinutes() {
        if (value == 0 || unit == null) {
            return 0;
        }

        return unit.minutes * value;
    }

    public Period asPeriod() {
        if (unit == null)
            return WallTimeUnit.Day.toPeriod(0);
        return unit.toPeriod(value);
    }
}
