package org.motechproject.openmrs.services;

import org.motechproject.mrs.services.PatientService;
import org.motechproject.openmrs.model.OpenMRSPatient;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientServiceImpl implements PatientService<OpenMRSPatient>{

    @Autowired
    private org.openmrs.api.PatientService patientService;

    @Override
    public OpenMRSPatient savePatient(OpenMRSPatient patient) {

        return null;

    }
}
