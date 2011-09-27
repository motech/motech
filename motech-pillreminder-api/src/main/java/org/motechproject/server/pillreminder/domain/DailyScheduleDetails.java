package org.motechproject.server.pillreminder.domain;

public class DailyScheduleDetails extends ScheduleDetails {

    private int pillWindowInHours;

    public DailyScheduleDetails(int repeatInterval, int pillWindowInHours) {
        super(repeatInterval);
        this.pillWindowInHours = pillWindowInHours;
    }

    public int getPillWindowInHours() {
        return pillWindowInHours;
    }

    public void setPillWindowInHours(int pillWindowInHours) {
        this.pillWindowInHours = pillWindowInHours;
    }
}
