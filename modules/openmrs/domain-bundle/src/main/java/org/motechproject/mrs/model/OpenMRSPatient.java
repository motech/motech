package org.motechproject.mrs.model;

import java.util.Objects;

import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;

/**
 * Domain to hold patient information
 */
public class OpenMRSPatient implements Patient {

    private String id;
    private OpenMRSFacility facility;
    private OpenMRSPerson person;
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
    public OpenMRSPatient(String motechId, OpenMRSPerson person, OpenMRSFacility mrsFacility) {
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
    public OpenMRSPatient(String id, String motechId, OpenMRSPerson person, OpenMRSFacility mrsFacility) {
        this(motechId, person, mrsFacility);
        this.id = id;
    }

    public String getPatientId() {
        return id;
    }

    public OpenMRSFacility getFacility() {
        return facility;
    }

    public OpenMRSPerson getPerson() {
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
    public void setFacility(Facility facility) {
        this.facility = (OpenMRSFacility) facility;
    }

    @Override
    public void setPerson(Person person) {
        this.person = (OpenMRSPerson) person;
    }

    @Override
    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }
}
