package org.motechproject.mrs.model;

/**
 * Domain to hold patient information
 */
public class MRSPatient {

    private String id;
    private MRSFacility facility;
    private MRSPerson person;
    private String motechId;

    /**
     * Creates a new Patient
     *
     * @param id Patient ID
     */
    public MRSPatient(String id) {
        this.id = id;
    }

    /**
     * Creates a new Patient
     *
     * @param motechId    MOTECH Id of the patient
     * @param person      Person object containing the personal details of the patient
     * @param mrsFacility Location of the patient
     */
    public MRSPatient(String motechId, MRSPerson person, MRSFacility mrsFacility) {
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
    public MRSPatient(String id, String motechId, MRSPerson person, MRSFacility mrsFacility) {
        this(motechId, person, mrsFacility);
        this.id = id;
    }

    public String getId() {
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
        if (!(o instanceof MRSPatient)) {
            return false;
        }
        MRSPatient that = (MRSPatient) o;
        if (facility != null ? !facility.equals(that.facility) : that.facility != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (motechId != null ? !motechId.equals(that.motechId) : that.motechId != null) {
            return false;
        }
        if (person != null ? !person.equals(that.person) : that.person != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (facility != null ? facility.hashCode() : 0);
        result = 31 * result + (person != null ? person.hashCode() : 0);
        result = 31 * result + (motechId != null ? motechId.hashCode() : 0);
        return result;
    }
}
