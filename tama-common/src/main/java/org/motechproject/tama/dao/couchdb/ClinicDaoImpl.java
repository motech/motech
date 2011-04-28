package org.motechproject.tama.dao.couchdb;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.tama.dao.ClinicDao;
import org.motechproject.tama.model.Clinic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ClinicDaoImpl extends MotechAuditableRepository<Clinic> implements ClinicDao {

	@Autowired
    public ClinicDaoImpl(@Qualifier("tamaClinicDatabase") CouchDbConnector db) {
        super(Clinic.class, db);
        initStandardDesignDocument();
    }
	
	
}
