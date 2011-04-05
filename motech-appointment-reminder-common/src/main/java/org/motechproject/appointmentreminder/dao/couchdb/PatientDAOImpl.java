package org.motechproject.appointmentreminder.dao.couchdb;

import org.ektorp.CouchDbConnector;
import org.motechproject.appointmentreminder.dao.PatientDAO;
import org.motechproject.appointmentreminder.model.Appointment;
import org.motechproject.appointmentreminder.model.Patient;
import org.motechproject.appointmentreminder.model.Visit;
import org.motechproject.dao.MotechAuditableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PatientDAOImpl extends MotechAuditableRepository<Patient> implements PatientDAO {

    @Autowired
    public PatientDAOImpl(@Qualifier("appointmentReminderDatabase") CouchDbConnector db) {
        super(Patient.class, db);
        initStandardDesignDocument();
    }

    @Override
    public void addVisit(Visit visit){

        db.create(visit);
    }

    @Override
    public void updateVisit(Visit visit){

        db.update(visit);
    }
    
    @Override
    public void removeVisit(String visitId) {

        Visit visit = db.get(Visit.class, visitId);
        db.delete(visit);
    }
    
    @Override
    public void removeVisit(Visit visit) {

        db.delete(visit);
    }

    @Override
    public void addAppointment(Appointment appointment){

        db.create(appointment);
    }

    @Override
    public void updateAppointment(Appointment appointment){

        db.update(appointment);
    }

    @Override
    public Appointment getAppointment(String appointmentId) {

        Appointment appointment = db.get(Appointment.class, appointmentId);
        return appointment;
    }

    @Override
    public void removeAppointment(String appointmentId) {

        Appointment appointment = getAppointment(appointmentId);
        db.delete(appointment);
    }

    @Override
    public void removeAppointment(Appointment appointment) {

        db.delete(appointment);
    }
    
}
