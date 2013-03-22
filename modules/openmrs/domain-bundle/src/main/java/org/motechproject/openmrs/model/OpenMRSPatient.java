package org.motechproject.openmrs.model;

import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;

import java.util.Objects;

/**
 * Domain to hold patient information
 */
public class OpenMRSPatient implements MRSPatient {

    private String id;
    private MRSFacility facility;
    private MRSPerson person;
    private String motechId;

    /**
     * Creates a new Patient
     *
     * @param id Patient ID
     */
    public OpenMRSPatient(String id) {
        this.id = id;
    }

    /**
     * Creates a new Patient
     *
     * @param motechId    MOTECH Id of the patient
     * @param person      Person object containing the personal details of the patient
     * @param mrsFacility Location of the patient
     */
    public OpenMRSPatient(String motechId, MRSPerson person, MRSFacility mrsFacility) {
        this.facility = mrsFacility;
        this.person = person;
        this.motechId = motechId;
    }

    /**
     * Creates a new Patient
     *
     * @param id          Patient ID
     * @param motechId    MOTECH Id of the patient
     * @param person      Person object containing the personal details of the patient
     * @param mrsFacility Location of the patient
     */
    public OpenMRSPatient(String id, String motechId, MRSPerson person, MRSFacility mrsFacility) {
        this(motechId, person, mrsFacility);
        this.id = id;
    }

    public String getPatientId() {
        return id;
    }

    public MRSFacility getFacility() {
        return facility;
    }

    public MRSPerson getPerson() {
        return person;
    }

    public String getMotechId() {
        return motechId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OpenMRSPatient)) {
            return false;
        }

        OpenMRSPatient that = (OpenMRSPatient) o;

        return Objects.equals(facility, that.facility) && Objects.equals(id, that.id) &&
                Objects.equals(motechId, that.motechId) && Objects.equals(person, that.person);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (facility != null ? facility.hashCode() : 0);
        result = 31 * result + (person != null ? person.hashCode() : 0);
        result = 31 * result + (motechId != null ? motechId.hashCode() : 0);
        return result;
    }

    @Override
    public void setPatientId(String id) {
        this.id = id;
    }

    @Override
    public void setFacility(MRSFacility facility) {
        this.facility = facility;
    }

    @Override
    public void setPerson(MRSPerson person) {
        this.person = person;
    }

    @Override
    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }
}
