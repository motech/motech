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
    private Set<Dosage> dosages;

    public PillRegimen() {
    }

    public PillRegimen(String externalId, Date startDate, Date endDate, Set<Dosage> dosages) {
        this.externalId = externalId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dosages = dosages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PillRegimen that = (PillRegimen) o;
        if (dosages != null ? !dosages.equals(that.dosages) : that.dosages != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (externalId != null ? !externalId.equals(that.externalId) : that.externalId != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = externalId != null ? externalId.hashCode() : 0;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (dosages != null ? dosages.hashCode() : 0);
        return result;
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
