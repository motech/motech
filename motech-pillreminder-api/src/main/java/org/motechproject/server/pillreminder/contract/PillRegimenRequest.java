package org.motechproject.server.pillreminder.contract;

import java.util.List;

public class PillRegimenRequest {
    private String externalId;
    private int reminderRepeatWindowInHours;
    private int reminderRepeatIntervalInMinutes;
    private List<DosageRequest> dosageRequests;

    public PillRegimenRequest(String externalId, int reminderRepeatWindowInHours, int reminderRepeatIntervalInMinutes, List<DosageRequest> dosageRequests) {
        this.externalId = externalId;
        this.reminderRepeatWindowInHours = reminderRepeatWindowInHours;
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
        this.dosageRequests = dosageRequests;
    }

    public String getExternalId() {
        return externalId;
    }

    public int getReminderRepeatWindowInHours() {
        return reminderRepeatWindowInHours;
    }

    public int getReminderRepeatIntervalInMinutes() {
        return reminderRepeatIntervalInMinutes;
    }

    public List<DosageRequest> getDosageRequests() {
        return dosageRequests;
    }
}
