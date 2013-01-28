/**
 * \ingroup MRS
 *  Services offered by MRS
 */
package org.motechproject.mrs.services;

import org.motechproject.mrs.domain.Encounter;
import java.util.List;

/**
 * Interface for fetching and storing MRSEncounter details
 */
public interface EncounterAdapter {
    /**
     * Stores a given MRSEncounter in the MRS system
     * @param mrsEncounter Object to be saved.
     * @return Saved MRS Encounter object
     */
    Encounter createEncounter(Encounter mrsEncounter);

    /**
     * Fetches the latest encounter of a patient identified by MOTECH ID and the encounter type.
     * @param motechId Identifier of the patient
     * @param encounterType Type of the encounter. (e.g. ANCVISIT)
     * @return The latest MRSEncounter if found.
     */
    Encounter getLatestEncounterByPatientMotechId(String motechId, String encounterType);

    /**
     * Fetches an encounter by its UUID
     * @param id The UUID in OpenMRS of the encounter to retrieve
     * @return The MRSEncounter with the specified id
     */
    Encounter getEncounterById(String id);

    /**
     * Fetches a list of encounters of a patient identified by MOTECH ID and the encounter type.
     * @param motechId Identifier of the patient
     * @param encounterType Type of the encounter. (e.g. ANCVISIT)
     * @return A list of all MRSEncounters of the corresponding encounter type for the patient identified by MOTECH ID
     */
    List<Encounter> getEncountersByEncounterType(String motechId, String encounterType);
}
