package org.motechproject.couch.mrs.repository.impl;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCouchPatientsImpl extends MotechBaseRepository<CouchPatientImpl> implements AllCouchPatients {

    @Autowired
    protected AllCouchPatientsImpl(@Qualifier("couchPatientDatabaseConnector") CouchDbConnector db) {
        super(CouchPatientImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_motechId", map = "function(doc) { if (doc.type ==='Patient') { emit(doc.motechId, doc._id); }}")
    public List<CouchPatientImpl> findByMotechId(String motechId) {
        if (motechId == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("by_motechId").key(motechId).includeDocs(true);
        return db.queryView(viewQuery, CouchPatientImpl.class);
    }

    @Override
    public void addPatient(CouchPatientImpl patient) {

        if (patient.getMotechId() == null) {
            throw new NullPointerException("Motech ID for the patient cannot be null.");
        }

        List<CouchPatientImpl> patients = findByMotechId(patient.getMotechId());

        if (!patients.isEmpty()) {
            CouchPatientImpl couchPatient = patients.get(0);
            updateFields(couchPatient, patient);
            update(couchPatient);
            return;
        }

        try {
            super.add(patient);
        } catch (IllegalArgumentException e) {
            throw new MRSCouchException(e.getMessage(), e);
        }
    }

    private void updateFields(CouchPatientImpl couchPatient, CouchPatientImpl patient) {
        couchPatient.setFacilityId(patient.getFacilityId());
        couchPatient.setPatientId(patient.getPatientId());
        couchPatient.setPersonId(patient.getPersonId());
    }

    @Override
    public List<CouchPatientImpl> findAllPatients() {
        return this.getAll();
    }

    @Override
    @View(name = "by_patientId", map = "function(doc) { if (doc.type ==='Patient') { emit(doc.patientId, doc._id); }}")
    public List<CouchPatientImpl> findByPatientId(String patientId) {
        if (patientId == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("by_patientId").key(patientId).includeDocs(true);
        return db.queryView(viewQuery, CouchPatientImpl.class);
    }

}
