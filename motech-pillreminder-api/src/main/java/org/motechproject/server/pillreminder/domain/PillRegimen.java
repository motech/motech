package org.motechproject.server.pillreminder.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.Date;
import java.util.Set;

@TypeDiscriminator("doc.type === 'PILLREGIMEN'")
public class PillRegimen extends MotechAuditableDataObject {

    public static final String REGIMEN_END_DATE_CANNOT_BE_BEFORE_START_DATE = "Regimen end-date cannot be before start-date";

    @JsonProperty("type")
    private String type = "PILLREGIMEN";
    private String externalId;
    private Date startDate;
    private Date endDate;
    private Integer reminderRepeatWindowInHours;
    private Integer reminderRepeatIntervalInMinutes;
    private Set<Dosage> dosages;

    public PillRegimen() {
    }

    public PillRegimen(String externalId, Date startDate, Date endDate, Integer reminderRepeatWindowInHours, Integer reminderRepeatIntervalInMinutes, Set<Dosage> dosages) {
        this.externalId = externalId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reminderRepeatWindowInHours = reminderRepeatWindowInHours;
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
        this.dosages = dosages;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Set<Dosage> getDosages() {
        return dosages;
    }

    public void setDosages(Set<Dosage> dosages) {
        this.dosages = dosages;
    }

    public Integer getReminderRepeatWindowInHours() {
        return reminderRepeatWindowInHours;
    }

    public void setReminderRepeatWindowInHours(Integer reminderRepeatWindowInHours) {
        this.reminderRepeatWindowInHours = reminderRepeatWindowInHours;
    }

    public Integer getReminderRepeatIntervalInMinutes() {
        return reminderRepeatIntervalInMinutes;
    }

    public void setReminderRepeatIntervalInMinutes(Integer reminderRepeatIntervalInMinutes) {
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void validate() {
        if(startDate.after(endDate))
            throw(new ValidationException(REGIMEN_END_DATE_CANNOT_BE_BEFORE_START_DATE));
    }

}
