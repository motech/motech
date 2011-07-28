package org.motechproject.valueobjects;

public class WallTime {
    private int value;
    private WallTimeUnit unit;

    public WallTime() {
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

        if (value != wallTime.value) return false;
        if (unit != wallTime.unit) return false;

        return true;
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

    public void setValue(int value) {
        this.value = value;
    }

    public WallTimeUnit getUnit() {
        return unit;
    }

    public void setUnit(WallTimeUnit unit) {
        this.unit = unit;
    }

    public int inDays() {
        return this.unit.days * value;
    }
}
