package org.motechproject.mrs.services;

import org.motechproject.mrs.model.Patient;

public interface PatientService<P extends Patient> {
    P savePatient(P patient);
}
