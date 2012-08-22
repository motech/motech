package org.motechproject.server.pillreminder.api.domain;

public class DailyScheduleDetails {
    private int pillWindowInHours;
    private int repeatIntervalInMinutes;
    private int bufferOverDosageTimeInMinutes;

    //Needed for Jackson.
    public DailyScheduleDetails() {
    }

    public DailyScheduleDetails(int repeatIntervalInMinutes, int pillWindowInHours, int bufferOverDosageTimeInMinutes) {
        this.repeatIntervalInMinutes = repeatIntervalInMinutes;
        this.pillWindowInHours = pillWindowInHours;
        this.bufferOverDosageTimeInMinutes = bufferOverDosageTimeInMinutes;
    }

    public int getPillWindowInHours() {
        return pillWindowInHours;
    }

    public void setPillWindowInHours(int pillWindowInHours) {
        this.pillWindowInHours = pillWindowInHours;
    }

    public int getRepeatIntervalInMinutes() {
        return repeatIntervalInMinutes;
    }

    public void setRepeatIntervalInMinutes(int repeatIntervalInMinutes) {
        this.repeatIntervalInMinutes = repeatIntervalInMinutes;
    }

    public int getBufferOverDosageTimeInMinutes() {
        return bufferOverDosageTimeInMinutes;
    }

    public void setBufferOverDosageTimeInMinutes(int bufferOverDosageTimeInMinutes) {
        this.bufferOverDosageTimeInMinutes = bufferOverDosageTimeInMinutes;
    }

}
