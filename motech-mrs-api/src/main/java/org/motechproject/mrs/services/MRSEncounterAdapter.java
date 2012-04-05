package org.motechproject.mrs.services;

import org.motechproject.mrs.model.MRSEncounter;

/**
 * Interface for fetching and storing MRSEncounter details
 */
public interface MRSEncounterAdapter {
    /**
     * Stores a given MRSEncounter in the MRS system
     * @param MRSEncounter Object to be saved.
     * @return Saved MRS Encounter object
     */
    public MRSEncounter createEncounter(MRSEncounter MRSEncounter);

    /**
     * Fetches the latest encounter of a patient identified by MOTECH ID and the encounter type.
     * @param motechId Identifier of the patient
     * @param encounterType Type of the encounter. (e.g. ANCVISIT)
     * @return The latest MRSEncounter if found.
     */
    public MRSEncounter getLatestEncounterByPatientMotechId(String motechId, String encounterType);
}
