package org.motechproject.tama.dao.couchdb;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.tama.dao.PatientDao;
import org.motechproject.tama.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PatientDaoImpl extends MotechAuditableRepository<Patient> implements PatientDao {

	@Autowired
    public PatientDaoImpl(@Qualifier("tamaPatientDatabase") CouchDbConnector db) {
        super(Patient.class, db);
        initStandardDesignDocument();
    }

	@Override
	@View( name = "findByClinicPatientId", map = "function(doc) {if (doc.clinicPatientId) {emit(doc.clinicId+':'+doc.clinicPatientId, doc._id);}}")
	public Patient findByClinicPatientId(String clinicId, String clinicPatientId) {
		ViewQuery q = createQuery("findByClinicPatientId").key(clinicId + ":" + clinicPatientId).includeDocs(true);
		List<Patient> patients = db.queryView(q, Patient.class);
		if (patients.size()>0) {
			return patients.get(0);
		}
		return null;
	}
	
	@Override
	@GenerateView
	public List<Patient> findByPhoneNumber(String phoneNumber) {
		return queryView("by_phoneNumber", phoneNumber);
	}
	
}
