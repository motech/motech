package org.motechproject.tama.api.dao;

import org.motechproject.dao.BaseDao;
import org.motechproject.tama.api.model.Patient;

import java.util.List;

public interface PatientDAO extends BaseDao<Patient> {

	public Patient findByClinicIdPatientId(String clinicId, String clinicPatientId);
    public List<Patient> findByPhoneNumber(String phoneNumber);
}
