package org.motechproject.server.pillreminder.domain;

public class WeeklyScheduleDetails extends ScheduleDetails {

    private int retries;

    public WeeklyScheduleDetails(int repeatInterval, int retries) {
        super(repeatInterval);
        this.retries = retries;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}
