package org.motechproject.mrs.services;

import org.motechproject.mrs.model.Patient;

public interface MRSPatientAdaptor {
    Patient savePatient(Patient patient);

    Patient getPatient(String patientId);

    Patient getPatientByMotechId(String motechId);
}
