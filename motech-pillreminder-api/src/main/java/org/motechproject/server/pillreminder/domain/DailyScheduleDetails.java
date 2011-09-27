package org.motechproject.server.pillreminder.domain;

public class DailyScheduleDetails extends ScheduleDetails {
    private int pillWindowInHours;

    protected DailyScheduleDetails() {
        super();
    }

    public DailyScheduleDetails(int repeatIntervalInMinutes, int pillWindowInHours) {
        super(repeatIntervalInMinutes);
        this.pillWindowInHours = pillWindowInHours;
    }

    public int getPillWindowInHours() {
        return pillWindowInHours;
    }

    public void setPillWindowInHours(int pillWindowInHours) {
        this.pillWindowInHours = pillWindowInHours;
    }
}
