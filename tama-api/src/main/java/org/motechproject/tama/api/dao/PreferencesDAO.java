package org.motechproject.tama.api.dao;

import org.motechproject.dao.BaseDao;
import org.motechproject.tama.api.model.Preferences;

public interface PreferencesDAO extends BaseDao<Preferences> {
	
	/**
	 * Retrieve a patient preference for a given patient from a given clinic
	 * @param clinicId
	 * @param clinicPatientId
	 * @return
	 */
	public Preferences findByClinicIdPatientId(String clinicId, String clinicPatientId);
	
}
