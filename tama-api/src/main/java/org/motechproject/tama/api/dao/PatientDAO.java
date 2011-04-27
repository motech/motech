package org.motechproject.tama.api.dao;

import org.motechproject.dao.BaseDao;
import org.motechproject.tama.api.model.Patient;

public interface PatientDAO extends BaseDao<Patient> {

	public Patient findByClinicIDPatientId(String clinicId, String clinicPatientId);
	
}
