/**
 * \ingroup MRS
 *  Services offered by MRS
*/
package org.motechproject.mrs.services;

import org.motechproject.mrs.model.MRSEncounter;
 /**
 * Interface for fetching and storing MRSEncounter details
 */
public interface MRSEncounterAdapter {
    /**
     * Stores a given MRSEncounter in the MRS system
     * @param mrsEncounter Object to be saved.
     * @return Saved MRS Encounter object
     */
    MRSEncounter createEncounter(MRSEncounter mrsEncounter);

    /**
     * Fetches the latest encounter of a patient identified by MOTECH ID and the encounter type.
     * @param motechId Identifier of the patient
     * @param encounterType Type of the encounter. (e.g. ANCVISIT)
     * @return The latest MRSEncounter if found.
     */
    MRSEncounter getLatestEncounterByPatientMotechId(String motechId, String encounterType);
}
