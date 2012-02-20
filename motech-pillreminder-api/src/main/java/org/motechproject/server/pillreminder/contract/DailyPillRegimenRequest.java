package org.motechproject.server.pillreminder.contract;

import java.util.List;

public class DailyPillRegimenRequest {
    private int pillWindowInHours;
    private String externalId;
    private List<DosageRequest> dosageRequests;
    protected int reminderRepeatIntervalInMinutes;
    protected int bufferOverDosageTimeInMinutes;

    public DailyPillRegimenRequest(String externalId, int pillWindowInHours, int reminderRepeatIntervalInMinutes, int bufferTimeForPatientToTakePill, List<DosageRequest> dosageRequests) {
        this.externalId = externalId;
        this.dosageRequests = dosageRequests;
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
        this.pillWindowInHours = pillWindowInHours;
        this.bufferOverDosageTimeInMinutes = bufferTimeForPatientToTakePill;
    }

    public int getPillWindowInHours() {
        return pillWindowInHours;
    }

    public String getExternalId() {
        return externalId;
    }

    public List<DosageRequest> getDosageRequests() {
        return dosageRequests;
    }

    public int getReminderRepeatIntervalInMinutes() {
        return reminderRepeatIntervalInMinutes;
    }

    public int getBufferOverDosageTimeInMinutes() {
        return bufferOverDosageTimeInMinutes;
    }

    public void setBufferOverDosageTimeInMinutes(int bufferOverDosageTimeInMinutes) {
        this.bufferOverDosageTimeInMinutes = bufferOverDosageTimeInMinutes;
    }
}