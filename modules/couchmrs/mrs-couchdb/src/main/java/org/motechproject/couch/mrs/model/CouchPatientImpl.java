package org.motechproject.couch.mrs.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mrs.domain.Person;

@TypeDiscriminator("doc.type === 'Patient'")
public class CouchPatientImpl extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private String patientId;
    @JsonProperty
    private String facilityId;
    @JsonProperty
    private CouchPerson person;
    @JsonProperty
    private String motechId;

    private final String type = "Patient";

    public CouchPatientImpl() {
        super();
        this.setType(type);
    }

    public CouchPatientImpl(String patientId, String motechId, Person person, String facilityId) {
        this();
        this.patientId = patientId;
        this.motechId = motechId;
        this.person = (CouchPerson) person;
        this.facilityId = facilityId;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public CouchPerson getPerson() {
        return person;
    }

    public void setPerson(CouchPerson person) {
        this.person = person;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
}
