package org.motechproject.mrs.services;

import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSObservation;

import java.util.List;

public interface MRSObservationAdapter {
    /**
     * Voids an observation for the MOTECH user, with the given reason
     *
     * @param mrsObservation  MRSObservation to be voided
     * @param reason          Reason for voiding the MRSObservation
     * @param mrsUserMotechId MOTECH ID of the user who's MRSObservation needs to be voided
     * @throws ObservationNotFoundException Exception when the expected Observation does not exist
     */
    void voidObservation(MRSObservation mrsObservation, String reason, String mrsUserMotechId) throws ObservationNotFoundException;

    /**
     * Returns the Latest MRSObservation of the MRS patient, given the concept name. (e.g. WEIGHT)
     *
     * @param patientMotechId MOTECH Id of the patient
     * @param conceptName     Concept Name of the MRSObservation
     * @return MRSObservation if present.
     */
    MRSObservation findObservation(String patientMotechId, String conceptName);
    List<MRSObservation> findObservations(String patientMotechId, String conceptName);
}
