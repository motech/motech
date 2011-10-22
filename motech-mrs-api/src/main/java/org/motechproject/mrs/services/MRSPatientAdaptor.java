package org.motechproject.mrs.services;

import org.motechproject.mrs.model.Patient;

public interface MRSPatientAdaptor<P extends Patient> {
    P savePatient(P patient);
}
