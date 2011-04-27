package org.motechproject.tama.api.dao.impl;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.tama.api.dao.PatientDAO;
import org.motechproject.tama.api.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PatientDAOImpl extends MotechAuditableRepository<Patient> implements PatientDAO {

	@Autowired
    public PatientDAOImpl(@Qualifier("tamaDatabase") CouchDbConnector db) {
        super(Patient.class, db);
        initStandardDesignDocument();
    }

	@Override
	@View( name = "findByClinicIDPatientId", map = "function(doc) {if (doc.clinicPatientId) {emit(doc.clinicId+':'+doc.clinicPatientId, doc._id);}}")
	public Patient findByClinicIDPatientId(String clinicId, String clinicPatientId) {
		ViewQuery q = createQuery("findByClinicIDPatientId").key(clinicId + ":" + clinicPatientId).includeDocs(true);
		List<Patient> patients = db.queryView(q, Patient.class);
		if (patients.size()>0) {
			return patients.get(0);
		}
		return null;
	}
	
}
