package org.motechproject.tama.dao;

import org.motechproject.dao.BaseDao;
import org.motechproject.tama.model.PatientPreferences;

public interface PatientPreferencesDao extends BaseDao<PatientPreferences> {
	
	/**
	 * Retrieve a patient preference for a given patient from a given clinic
	 * @param clinicId
	 * @param clinicPatientId
	 * @return
	 */
	public PatientPreferences findByClinicPatientId(String clinicId, String clinicPatientId);
	
}
