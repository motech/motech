package org.motechproject.mrs.domain;

public interface Patient {

    String getPatientId();

    void setPatientId(String id);

    Facility getFacility();

    void setFacility(Facility facility);

    Person getPerson();

    void setPerson(Person person);

    String getMotechId();

    void setMotechId(String motechId);

}
