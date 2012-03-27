package org.motechproject.mrs.services;

import org.motechproject.mrs.model.MRSObservation;

import java.util.List;

public interface MRSObservationAdapter {
    void voidObservation(MRSObservation mrsObservation, String reason, String mrsUserMotechId);

    MRSObservation findObservation(String patientMotechId, String conceptName);
    List<MRSObservation> findObservations(String patientMotechId, String conceptName);
}
