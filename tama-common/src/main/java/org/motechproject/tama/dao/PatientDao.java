package org.motechproject.tama.dao;

import org.motechproject.dao.BaseDao;
import org.motechproject.tama.model.Patient;

public interface PatientDao extends BaseDao<Patient> {

	public Patient findByClinicPatientId(String clinicId, String clinicPatientId);
	
}
