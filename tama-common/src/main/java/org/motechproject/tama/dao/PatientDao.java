package org.motechproject.tama.dao;

import java.util.List;

import org.motechproject.dao.BaseDao;
import org.motechproject.tama.model.Patient;

public interface PatientDao extends BaseDao<Patient> {

	public Patient findByClinicPatientId(String clinicId, String clinicPatientId);
	
	public List<Patient> findByPhoneNumber(String phoneNumber);
	
}
