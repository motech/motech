package org.motechproject.server.pillreminder.contract;

import java.util.Date;
import java.util.List;

public class PillRegimenRequest {
    private String externalId;
    private Date startDate;
    private Date endDate;
    private Integer reminderRepeatCount;
    private Integer reminderRepeatIntervalInMinutes;
    private List<DosageRequest> dosageContracts;

    public PillRegimenRequest(String externalId, Date startDate, Date endDate, Integer reminderRepeatCount, Integer reminderRepeatIntervalInMinutes, List<DosageRequest> dosageContracts) {
        this.externalId = externalId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reminderRepeatCount = reminderRepeatCount;
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
        this.dosageContracts = dosageContracts;
    }

    public String getExternalId() {
        return externalId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Integer getReminderRepeatCount() {
        return reminderRepeatCount;
    }

    public Integer getReminderRepeatIntervalInMinutes() {
        return reminderRepeatIntervalInMinutes;
    }

    public List<DosageRequest> getDosageContracts() {
        return dosageContracts;
    }
}
