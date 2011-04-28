package org.motechproject.tama.dao.couchdb;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.tama.dao.PatientPreferencesDao;
import org.motechproject.tama.model.PatientPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PatientPreferencesDaoImpl extends MotechAuditableRepository<PatientPreferences>
		implements PatientPreferencesDao {

	@Autowired
    public PatientPreferencesDaoImpl(@Qualifier("tamaPatientPreferenceDatabase") CouchDbConnector db) {
        super(PatientPreferences.class, db);
        initStandardDesignDocument();
    }
	
	@View( name = "findByClinicPatientId", map = "function(doc) {if (doc.clinicPatientId) {emit(doc.clinicId+':'+doc.clinicPatientId	, doc._id);}}")
	public PatientPreferences findByClinicPatientId(String clinicId, String clinicPatientId) {
		ViewQuery q = createQuery("findByClinicPatientId").key(clinicId + ":" + clinicPatientId).includeDocs(true);
		List<PatientPreferences> patientPrefs = db.queryView(q, PatientPreferences.class);
		if (patientPrefs.size()>0) {
			return patientPrefs.get(0);
		}
		return null;
	}

}
