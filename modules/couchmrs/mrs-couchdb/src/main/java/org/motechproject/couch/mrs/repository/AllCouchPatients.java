package org.motechproject.couch.mrs.repository;

import java.util.List;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.MRSCouchException;

public interface AllCouchPatients {

    List<CouchPatientImpl> findByMotechId(String motechId);

    void addPatient(CouchPatientImpl patient) throws MRSCouchException;

    void update(CouchPatientImpl patient);

    void remove(CouchPatientImpl patient);

    List<CouchPatientImpl> findAllPatients();

    List<CouchPatientImpl> findByPatientId(String patientId);

    List<CouchPatientImpl> findByNameAndMotechId(String name, String motechId);

}
