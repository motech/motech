package org.motechproject.openmrs.dao.hibernate;

import java.util.List;

import org.hibernate.SessionFactory;
import org.motechproject.openmrs.dao.AppointmentReminderPreferenceDAO;
import org.motechproject.openmrs.model.AppointmentReminderPreferences;
import org.openmrs.Patient;

public class HibernateAppointmentReminderPreferenceDAO implements
		AppointmentReminderPreferenceDAO {

    private SessionFactory sessionFactory;
	
	/* (non-Javadoc)
	 * @see org.motechproject.openmrs.dao.AppointmentReminderPreferenceDAO#getAppointmentReminderPreferences(java.lang.Integer)
	 */
	@Override
	public AppointmentReminderPreferences getAppointmentReminderPreferences(
			Integer id) {
		return (AppointmentReminderPreferences) sessionFactory.getCurrentSession().get(AppointmentReminderPreferences.class, id);
	}

	/* (non-Javadoc)
	 * @see org.motechproject.openmrs.dao.AppointmentReminderPreferenceDAO#saveAppointmentAppointmentReminderPreferences(org.motechproject.openmrs.model.AppointmentReminderPreferences)
	 */
	@Override
	public AppointmentReminderPreferences saveAppointmentAppointmentReminderPreferences(
			AppointmentReminderPreferences preferences) {
		sessionFactory.getCurrentSession().saveOrUpdate(preferences);
		return preferences;
	}

	/* (non-Javadoc)
	 * @see org.motechproject.openmrs.dao.AppointmentReminderPreferenceDAO#getAppointmentReminderPreferencesByPatient(org.openmrs.Patient)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AppointmentReminderPreferences getAppointmentReminderPreferencesByPatient(
			Patient patient) {
		List<AppointmentReminderPreferences> results = (List<AppointmentReminderPreferences>) sessionFactory.getCurrentSession().createQuery(
                "from AppointmentReminderPreferences as ap where ap.patient = :patient").setParameter("patient", patient).list();
		if (results != null && results.size() > 0) {
			return results.get(0); // There should only be one item in the list
		} else {
			return null;
		}
	}
	
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


}
