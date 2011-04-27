package org.motechproject.tama.api.dao;

import org.motechproject.dao.BaseDao;
import org.motechproject.tama.api.model.Doctor;

import java.util.List;

public interface DoctorDAO extends BaseDao<Doctor> {
	
	public List<Doctor> findByClinicId(String clinicId);
}
