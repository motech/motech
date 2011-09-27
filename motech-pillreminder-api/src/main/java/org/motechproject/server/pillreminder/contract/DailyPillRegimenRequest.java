package org.motechproject.server.pillreminder.contract;

import java.util.List;

public class DailyPillRegimenRequest {
    private String externalId;
    private int pillWindowInHours;
    private int reminderRepeatIntervalInMinutes;
    private List<DosageRequest> dosageRequests;

    public DailyPillRegimenRequest(String externalId, int pillWindowInHours, int reminderRepeatIntervalInMinutes, List<DosageRequest> dosageRequests) {
        this.externalId = externalId;
        this.pillWindowInHours = pillWindowInHours;
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
        this.dosageRequests = dosageRequests;
    }

    public String getExternalId() {
        return externalId;
    }

    public int getPillWindowInHours() {
        return pillWindowInHours;
    }

    public int getReminderRepeatIntervalInMinutes() {
        return reminderRepeatIntervalInMinutes;
    }

    public List<DosageRequest> getDosageRequests() {
        return dosageRequests;
    }
}
