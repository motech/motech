package org.motechproject.tama.dao.couchdb;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.tama.dao.AppointmentDao;
import org.motechproject.tama.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author yyonkov
 */
@Component
public class AppointmentDaoImpl extends MotechAuditableRepository<Appointment> implements AppointmentDao {

	@Autowired
	protected AppointmentDaoImpl(@Qualifier("tamaPatientDatabase") CouchDbConnector db) {
        super(Appointment.class, db);
        initStandardDesignDocument();
    }

	@Override
	@GenerateView
	public List<Appointment> findByPatientId(String patientId) {
		return queryView("by_patientId", patientId);
	}

}
