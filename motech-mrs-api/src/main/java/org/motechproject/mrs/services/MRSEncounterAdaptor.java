package org.motechproject.mrs.services;

import org.motechproject.mrs.model.MRSEncounter;


public interface MRSEncounterAdaptor {
    public MRSEncounter createEncounter(MRSEncounter MRSEncounter);

    public MRSEncounter getLatestEncounterByPatientMotechId(String motechId, String encounterType);
}
