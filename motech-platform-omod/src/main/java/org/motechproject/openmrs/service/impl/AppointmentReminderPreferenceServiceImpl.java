package org.motechproject.openmrs.service.impl;

import org.motechproject.dao.PatientDao;
import org.motechproject.openmrs.dao.AppointmentReminderPreferenceDAO;
import org.motechproject.openmrs.model.AppointmentReminderPreferences;
import org.motechproject.openmrs.service.AppointmentReminderPreferenceService;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.springframework.beans.factory.annotation.Autowired;

public class AppointmentReminderPreferenceServiceImpl extends
		BaseOpenmrsService implements AppointmentReminderPreferenceService {

	private AppointmentReminderPreferenceDAO dao = null;

	@Autowired(required=false)
	private PatientDao motechPatientDao;

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
		preferences = dao.saveAppointmentAppointmentReminderPreferences(preferences);
		
		// save the appointment into motech's data store
		if (motechPatientDao != null) {
			try {
				// Retrieve the patient object
				org.motechproject.model.Patient patient = motechPatientDao.get(preferences.getPatient().getUuid());
				
				// Update patient information (TODO: Refactor patient to be componentized)
				patient.setSendAppointmentReminder(preferences.getModuleEnabled());
				patient.setBestCallTime(preferences.getPreferredTime());
				patient.setReminderLeadupDays(preferences.getDaysBefore());
				
				// Persist changes
				motechPatientDao.update(patient);
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return preferences;
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
