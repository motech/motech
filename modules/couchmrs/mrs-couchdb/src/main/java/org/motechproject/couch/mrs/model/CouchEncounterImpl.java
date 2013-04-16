package org.motechproject.couch.mrs.model;

import java.util.Set;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Encounter'")
public class CouchEncounterImpl extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    private String encounterId;
    private String providerId;
    private String creatorId;
    private String facilityId;
    private DateTime date;
    private Set<String> observationIds;
    private String patientId;
    private String encounterType;

    private final String type = "Encounter";

    public CouchEncounterImpl() {
        super();
        this.setType(type);
    }

    public CouchEncounterImpl(String encounterId, String providerId, String creatorId, String facilityId, DateTime date, Set<String> observationIds, String patientId, String encounterType) {
        this();
        this.encounterId = encounterId;
        this.providerId = providerId;
        this.creatorId = creatorId;
        this.facilityId = facilityId;
        this.date = date;
        this.observationIds = observationIds;
        this.patientId = patientId;
        this.encounterType = encounterType;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Set<String> getObservationIds() {
        return observationIds;
    }

    public void setObservationIds(Set<String> observationIds) {
        this.observationIds = observationIds;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public String getType() {
        return type;
    }
}
