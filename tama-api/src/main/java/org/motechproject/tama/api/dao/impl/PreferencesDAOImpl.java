package org.motechproject.tama.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.tama.api.dao.PreferencesDAO;
import org.motechproject.tama.api.model.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PreferencesDAOImpl extends MotechAuditableRepository<Preferences>
		implements PreferencesDAO {

	@Autowired
    public PreferencesDAOImpl(@Qualifier("tamaDatabase") CouchDbConnector db) {
        super(Preferences.class, db);
        initStandardDesignDocument();
    }
	
	@View( name = "findByClinicIdPatientId", map = "function(doc) {if (doc.clinicPatientId) {emit(doc.clinicId+':'+doc.clinicPatientId	, doc._id);}}")
	public Preferences findByClinicIdPatientId(String clinicId, String clinicPatientId) {
		ViewQuery q = createQuery("findByClinicIdPatientId").key(clinicId + ":" + clinicPatientId).includeDocs(true);
		List<Preferences> patientPrefs = db.queryView(q, Preferences.class);
		if (patientPrefs.size()>0) {
			return patientPrefs.get(0);
		}
		return null;
	}

}
