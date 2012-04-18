package org.motechproject.server.pillreminder.api.contract;

import java.util.List;

public class PillRegimenResponse {
    private String pillRegimenId;
    private String externalId;
    private int reminderRepeatWindowInHours;
    private int reminderRepeatIntervalInMinutes;
    private int bufferOverDosageTimeInMinutes;
    private List<DosageResponse> dosages;

    public PillRegimenResponse(String pillRegimenId, String externalId, int reminderRepeatWindowInHours, int reminderRepeatIntervalInMinutes, int bufferOverDosageTimeInMinutes, List<DosageResponse> dosages) {
        this.pillRegimenId = pillRegimenId;
        this.externalId = externalId;
        this.reminderRepeatWindowInHours = reminderRepeatWindowInHours;
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
        this.bufferOverDosageTimeInMinutes = bufferOverDosageTimeInMinutes;
        this.dosages = dosages;
    }

    public String getPillRegimenId() {
        return pillRegimenId;
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

    public List<DosageResponse> getDosages() {
        return dosages;
    }

    public int getBufferOverDosageTimeInMinutes() {
        return bufferOverDosageTimeInMinutes;
    }

    public void setBufferOverDosageTimeInMinutes(int bufferOverDosageTimeInMinutes) {
        this.bufferOverDosageTimeInMinutes = bufferOverDosageTimeInMinutes;
    }
}
