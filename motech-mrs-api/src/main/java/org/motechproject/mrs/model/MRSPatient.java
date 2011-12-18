package org.motechproject.mrs.model;

public class MRSPatient {

    private String id;
    private MRSFacility facility;
    private MRSPerson person;

    public MRSPatient(String id) {
        this.id = id;
    }

    public MRSPatient(MRSPerson person, MRSFacility mrsFacility) {
        this.facility = mrsFacility;
        this.person = person;
    }

    public MRSPatient(String patientId,MRSPerson mrsPerson,MRSFacility mrsFacility) {
        this(mrsPerson,mrsFacility);
        this.id = patientId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MRSPatient that = (MRSPatient) o;

        if (facility != null ? !facility.equals(that.facility) : that.facility != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (person != null ? !person.equals(that.person) : that.person != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (facility != null ? facility.hashCode() : 0);
        result = 31 * result + (person != null ? person.hashCode() : 0);
        return result;
    }
}
