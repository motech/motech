package org.motechproject.valueobjects;

public enum WallTimeUnit {
    Week(7), Day(1);

    public int days;

    WallTimeUnit(int days) {
        this.days = days;
    }
}
