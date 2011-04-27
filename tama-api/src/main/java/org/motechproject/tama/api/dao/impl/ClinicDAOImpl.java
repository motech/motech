package org.motechproject.tama.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.tama.api.dao.ClinicDAO;
import org.motechproject.tama.api.model.Clinic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ClinicDAOImpl extends MotechAuditableRepository<Clinic> implements ClinicDAO {

	@Autowired
    public ClinicDAOImpl(@Qualifier("tamaDatabase") CouchDbConnector db) {
        super(Clinic.class, db);
        initStandardDesignDocument();
    }
	
	
}
