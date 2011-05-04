package org.motechproject.tama.dao;

import java.util.List;

import org.motechproject.dao.BaseDao;
import org.motechproject.tama.model.Appointment;


/**
 * Appointment DAO interface provides Appointment persistent data access methods
 * @author yyonkov
 */
public interface AppointmentDao extends BaseDao<Appointment> {
	public List<Appointment> findByPatientId(String patientId);
}