package org.motechproject.mrs.services;

import org.motechproject.mrs.model.MRSPatient;

import java.util.List;

public interface MRSPatientAdaptor {
    MRSPatient savePatient(MRSPatient patient);

    String updatePatient(MRSPatient patient);

    MRSPatient getPatient(String patientId);

    MRSPatient getPatientByMotechId(String motechId);

    List<MRSPatient> search(String name, String id);
}
