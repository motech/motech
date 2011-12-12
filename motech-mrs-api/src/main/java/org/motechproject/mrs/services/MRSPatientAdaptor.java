package org.motechproject.mrs.services;

import org.motechproject.mrs.model.MRSPatient;

public interface MRSPatientAdaptor {
    MRSPatient savePatient(MRSPatient patient);

    MRSPatient getPatient(String patientId);

    MRSPatient getPatientByMotechId(String motechId);
}
