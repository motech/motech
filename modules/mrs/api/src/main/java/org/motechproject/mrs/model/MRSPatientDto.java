package org.motechproject.mrs.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;

public class MRSPatientDto implements MRSPatient {

    private String patientId;
    private String motechId;

    @JsonDeserialize(as = MRSPersonDto.class)
    private MRSPerson person;

    @JsonDeserialize(as = MRSFacilityDto.class)
    private MRSFacility facility;

    public MRSPatientDto() {
    }

    public MRSPatientDto(String patientId, MRSFacility facility, MRSPerson person, String motechId) {
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

    public MRSFacility getFacility() {
        return facility;
    }

    public void setFacility(MRSFacility facility) {
        this.facility = facility;
    }

    public MRSPerson getPerson() {
        return person;
    }

    public void setPerson(MRSPerson person) {
        this.person = person;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }
}
