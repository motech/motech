package org.motechproject.appointmentreminder.dao;

import org.motechproject.appointmentreminder.model.Appointment;
import org.motechproject.appointmentreminder.model.Patient;
import org.motechproject.appointmentreminder.model.Visit;
import org.motechproject.dao.BaseDao;

public interface PatientDAO extends BaseDao<Patient> {

	public void addVisit(Visit visit);
    public void updateVisit(Visit visit);
    public void removeVisit(String visitId);
    public void removeVisit(Visit visit);

    public void addAppointment(Appointment appointment);
    public void updateAppointment(Appointment appointment);
    public Appointment getAppointment(String appointmentId);
    public void removeAppointment(String appointmentId);
    public void removeAppointment(Appointment appointment);
    
}
