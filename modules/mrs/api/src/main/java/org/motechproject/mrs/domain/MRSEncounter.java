package org.motechproject.mrs.domain;

import org.joda.time.DateTime;

import java.util.Set;

public interface MRSEncounter {

    String getEncounterId();

    void setEncounterId(String encounterId);

    MRSProvider getProvider();

    void setProvider(MRSProvider provider);

    MRSUser getCreator();

    void setCreator(MRSUser creator);

    MRSFacility getFacility();

    void setFacility(MRSFacility facility);

    DateTime getDate();

    void setDate(DateTime date);

    Set<? extends MRSObservation> getObservations();

    void setObservations(Set<? extends MRSObservation> observations);

    MRSPatient getPatient();

    void setPatient(MRSPatient patient);

    String getEncounterType();

    void setEncounterType(String encounterType);
}
