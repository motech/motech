package org.motechproject.mrs.domain;

import java.util.Set;
import org.joda.time.DateTime;

public interface Encounter {

    String getEncounterId();

    void setEncounterId(String encounterId);

    Provider getProvider();

    void setProvider(Provider provider);

    User getCreator();

    void setCreator(User creator);

    Facility getFacility();

    void setFacility(Facility facility);

    DateTime getDate();

    void setDate(DateTime date);

    Set<? extends Observation> getObservations();

    void setObservations(Set<? extends Observation> observations);

    Patient getPatient();

    void setPatient(Patient patient);

    String getEncounterType();

    void setEncounterType(String encounterType);
}
