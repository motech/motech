package org.motechproject.openmrs.service.impl;

import org.motechproject.openmrs.dao.AppointmentReminderPreferenceDAO;
import org.motechproject.openmrs.model.AppointmentReminderPreferences;
import org.motechproject.openmrs.service.AppointmentReminderPreferenceService;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;

public class AppointmentReminderPreferenceServiceImpl extends
		BaseOpenmrsService implements AppointmentReminderPreferenceService {

	private AppointmentReminderPreferenceDAO dao = null;
	
	/* (non-Javadoc)
	 * @see org.motechproject.openmrs.service.AppointmentReminderPreferenceService#setAppointmentReminderPreferenceDAO(org.motechproject.openmrs.dao.AppointmentReminderPreferenceDAO)
	 */
	@Override
	public void setAppointmentReminderPreferenceDAO(
			AppointmentReminderPreferenceDAO dao) {
		this.dao = dao;
	}

	/* (non-Javadoc)
	 * @see org.motechproject.openmrs.service.AppointmentReminderPreferenceService#getAppointmentReminderPreferences(java.lang.Integer)
	 */
	@Override
	public AppointmentReminderPreferences getAppointmentReminderPreferences(
			Integer id) {
		return dao.getAppointmentReminderPreferences(id);
	}

	/* (non-Javadoc)
	 * @see org.motechproject.openmrs.service.AppointmentReminderPreferenceService#saveAppointmentAppointmentReminderPreferences(org.motechproject.openmrs.model.AppointmentReminderPreferences)
	 */
	@Override
	public AppointmentReminderPreferences saveAppointmentAppointmentReminderPreferences(
			AppointmentReminderPreferences preferences) {
		return dao.saveAppointmentAppointmentReminderPreferences(preferences);
	}

	/* (non-Javadoc)
	 * @see org.motechproject.openmrs.service.AppointmentReminderPreferenceService#getAppointmentReminderPreferencesByPatient(org.openmrs.Patient)
	 */
	@Override
	public AppointmentReminderPreferences getAppointmentReminderPreferencesByPatient(
			Patient patient) {
		return dao.getAppointmentReminderPreferencesByPatient(patient);
	}

}
