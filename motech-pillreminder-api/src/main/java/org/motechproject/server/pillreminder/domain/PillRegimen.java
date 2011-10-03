package org.motechproject.server.pillreminder.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.Set;

@TypeDiscriminator("doc.type === 'PILLREGIMEN'")
public class PillRegimen extends MotechAuditableDataObject {

    @JsonProperty("type")
    private String type = "PILLREGIMEN";
    private String externalId;
    private DailyScheduleDetails scheduleDetails;
    private Set<Dosage> dosages;

    public PillRegimen() {
    }

    public PillRegimen(String externalId, Set<Dosage> dosages, DailyScheduleDetails scheduleDetails) {
        this.externalId = externalId;
        this.dosages = dosages;
        this.scheduleDetails = scheduleDetails;
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

    public DailyScheduleDetails getScheduleDetails() {
        return scheduleDetails;
    }

    public void setScheduleDetails(DailyScheduleDetails scheduleDetails) {
        this.scheduleDetails = scheduleDetails;
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