package org.motechproject.openmrs.services;

import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.motechproject.openmrs.model.OpenMRSPatient;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenMRSPatientAdaptor implements MRSPatientAdaptor<OpenMRSPatient> {

    @Autowired
    private org.openmrs.api.PatientService patientService;

    @Override
    public OpenMRSPatient savePatient(OpenMRSPatient patient) {
        return null;
    }
}
