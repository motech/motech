package org.motechproject.mrs.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;

public class PatientDto implements Patient {

    private String patientId;
    private String motechId;

    @JsonDeserialize(as = PersonDto.class)
    private Person person;

    @JsonDeserialize(as = FacilityDto.class)
    private Facility facility;

    public PatientDto() {
    }

    public PatientDto(String patientId, Facility facility, Person person, String motechId) {
        this.patientId = patientId;
        this.facility = facility;
        this.person = person;
        this.motechId = motechId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }
}
