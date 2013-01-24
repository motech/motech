package org.motechproject.couch.mrs.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;

@TypeDiscriminator("doc.type === 'Patient'")
public class CouchPatient extends MotechBaseDataObject implements Patient {

    private static final long serialVersionUID = 1L;

    private String patientId;
    private CouchFacility facility;
    private CouchPerson person;
    private String motechId;

    private final String type = "Patient";

    public CouchPatient() {
        super();
        this.setType(type);
    }

    public CouchPatient(String patientId, String motechId, CouchPerson person, CouchFacility facility) {
        this();
        this.patientId = patientId;
        this.facility = facility;
        this.person = person;
        this.motechId = motechId;

    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public CouchFacility getFacility() {
        return facility;
    }

    public void setFacility(CouchFacility facility) {
        this.facility = facility;
    }

    public CouchPerson getPerson() {
        return person;
    }

    public void setPerson(CouchPerson person) {
        this.person = person;
    }

    @Override
    public String getPatientId() {
        return patientId;
    }

    @Override
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public void setFacility(Facility facility) {
        this.facility = (CouchFacility) facility;
    }

    @Override
    public void setPerson(Person person) {
       this.person = (CouchPerson) person;
    }
}
