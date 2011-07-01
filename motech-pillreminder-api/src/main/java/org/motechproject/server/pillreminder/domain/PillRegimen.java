package org.motechproject.server.pillreminder.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.Set;

@TypeDiscriminator("doc.type === 'PILLREGIMEN'")
public class PillRegimen extends MotechAuditableDataObject {

    public static final String MEDICINE_END_DATE_CANNOT_BE_BEFORE_START_DATE = "Medicine end-date cannot be before start-date";

    @JsonProperty("type")
    private String type = "PILLREGIMEN";
    private String externalId;
    private int reminderRepeatWindowInHours;
    private int reminderRepeatIntervalInMinutes;
    private Set<Dosage> dosages;

    public PillRegimen() {
    }

    public PillRegimen(String externalId, int reminderRepeatWindowInHours, int reminderRepeatIntervalInMinutes, Set<Dosage> dosages) {
        this.externalId = externalId;
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

    public Set<Dosage> getDosages() {
        return dosages;
    }

    public void setDosages(Set<Dosage> dosages) {
        this.dosages = dosages;
    }

    public int getReminderRepeatWindowInHours() {
        return reminderRepeatWindowInHours;
    }

    public void setReminderRepeatWindowInHours(int reminderRepeatWindowInHours) {
        this.reminderRepeatWindowInHours = reminderRepeatWindowInHours;
    }

    public int getReminderRepeatIntervalInMinutes() {
        return reminderRepeatIntervalInMinutes;
    }

    public void setReminderRepeatIntervalInMinutes(int reminderRepeatIntervalInMinutes) {
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void validate() {
        for(Dosage dosage : dosages) {
            for(Medicine medicine : dosage.getMedicines()) {
               if(medicine.getEndDate()!=null && medicine.getStartDate().after(medicine.getEndDate()))
                    throw(new ValidationException(MEDICINE_END_DATE_CANNOT_BE_BEFORE_START_DATE));
            }
        }
    }

}
