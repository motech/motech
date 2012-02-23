package org.motechproject.mrs.services;

import org.motechproject.mrs.model.MRSObservation;

public interface MRSObservationAdapter {
    void voidObservation(MRSObservation mrsObservation, String reason, String mrsUserMotechId);

    MRSObservation findObservation(String patientMotechId, String conceptName);
}
