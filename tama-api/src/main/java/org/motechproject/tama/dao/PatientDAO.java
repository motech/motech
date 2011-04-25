package org.motechproject.tama.dao;

import org.motechproject.dao.BaseDao;
import org.motechproject.tama.model.Patient;

public interface PatientDAO extends BaseDao<Patient> {

	public Patient findByClinicIDPatientId(String clinicId, String clinicPatientId);
	
}
