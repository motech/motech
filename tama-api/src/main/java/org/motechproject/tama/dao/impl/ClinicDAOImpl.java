package org.motechproject.tama.dao.impl;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.tama.dao.ClinicDAO;
import org.motechproject.tama.model.Clinic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ClinicDAOImpl extends MotechAuditableRepository<Clinic> implements ClinicDAO {

	@Autowired
    public ClinicDAOImpl(@Qualifier("tamaDatabase") CouchDbConnector db) {
        super(Clinic.class, db);
        initStandardDesignDocument();
    }
	
	
}
