package org.motechproject.tama.dao.impl;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.tama.dao.DoctorDAO;
import org.motechproject.tama.model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DoctorDAOImpl extends MotechAuditableRepository<Doctor> implements DoctorDAO {

	@Autowired
    public DoctorDAOImpl(@Qualifier("tamaDatabase") CouchDbConnector db) {
        super(Doctor.class, db);
        initStandardDesignDocument();
    }
	
	@Override
	@GenerateView
	public List<Doctor> findByClinicId(String clinicId){
		return queryView("by_clinicId", clinicId);
	}
}
