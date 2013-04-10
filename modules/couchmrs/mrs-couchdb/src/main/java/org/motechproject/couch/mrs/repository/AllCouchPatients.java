package org.motechproject.couch.mrs.repository;

import org.motechproject.couch.mrs.model.CouchPatientImpl;

import java.util.List;

public interface AllCouchPatients {

    List<CouchPatientImpl> findByMotechId(String motechId);

    void addPatient(CouchPatientImpl patient);

    void update(CouchPatientImpl patient);

    void remove(CouchPatientImpl patient);

    List<CouchPatientImpl> findAllPatients();

    List<CouchPatientImpl> findByPatientId(String patientId);

}
