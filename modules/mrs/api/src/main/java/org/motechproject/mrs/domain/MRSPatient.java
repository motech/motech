package org.motechproject.mrs.domain;

public interface MRSPatient {

    String getPatientId();

    void setPatientId(String id);

    MRSFacility getFacility();

    void setFacility(MRSFacility facility);

    MRSPerson getPerson();

    void setPerson(MRSPerson person);

    String getMotechId();

    void setMotechId(String motechId);

}
