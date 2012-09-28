package org.motechproject.openmrs.ws.resource;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Patient;
import org.motechproject.openmrs.ws.resource.model.PatientListResult;

public interface PatientResource {

    Patient createPatient(Patient patient) throws HttpException;

    PatientListResult queryForPatient(String term) throws HttpException;

    Patient getPatientById(String patientId) throws HttpException;

    String getMotechPatientIdentifierUuid() throws HttpException;
}
