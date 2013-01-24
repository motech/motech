package org.motechproject.mrs.services;

import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.exception.ObservationNotFoundException;

import java.util.List;

public interface ObservationAdapter {
    /**
     * Voids an observation for the MOTECH user, with the given reason
     *
     * @param mrsObservation  MRSObservation to be voided
     * @param reason          Reason for voiding the MRSObservation
     * @param mrsUserMotechId MOTECH ID of the user who's MRSObservation needs to be voided
     * @throws ObservationNotFoundException Exception when the expected Observation does not exist
     */
    void voidObservation(Observation mrsObservation, String reason, String mrsUserMotechId) throws ObservationNotFoundException;

    /**
     * Returns the Latest MRSObservation of the MRS patient, given the concept name. (e.g. WEIGHT)
     *
     * @param patientMotechId MOTECH Id of the patient
     * @param conceptName     Concept Name of the MRSObservation
     * @return MRSObservation if present.
     */
    Observation findObservation(String patientMotechId, String conceptName);
    List<Observation> findObservations(String patientMotechId, String conceptName);


    Observation getObservationById(String id);
}
