package org.motechproject.server.pillreminder.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import javax.xml.transform.Source;
import java.util.Set;

@TypeDiscriminator("doc.type === 'PILLREGIMEN'")
public class PillRegimen extends MotechAuditableDataObject {

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
        for (Dosage dosage : dosages) {
            dosage.validate();
        }
    }

    public Dosage getDosage(String dosageId) {
        for (Dosage dosage : dosages)
            if (dosage.getId().equals(dosageId)) return dosage;
        return null;
    }
}
