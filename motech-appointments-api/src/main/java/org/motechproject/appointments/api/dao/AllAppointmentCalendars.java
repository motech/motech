package org.motechproject.appointments.api.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class AllAppointmentCalendars extends MotechBaseRepository<AppointmentCalendar> {
    @Autowired
    public AllAppointmentCalendars(@Qualifier("appointmentsDatabase") CouchDbConnector db) {
        super(AppointmentCalendar.class, db);
    }

    public void saveAppointmentCalendar(AppointmentCalendar appointmentCalendar) {
        addOrReplace(appointmentCalendar, "externalId", appointmentCalendar.externalId());
    }

    @View(name = "by_externalId", map = "function(doc) { if(doc.type === 'AppointmentCalendar' && doc.externalId) {emit(doc.externalId, doc._id)} }")
    public AppointmentCalendar findByExternalId(String externalId) {
        List<AppointmentCalendar> appointmentCalendars = queryView("by_externalId", externalId);
        return singleResult(appointmentCalendars);
    }

    @View(name = "by_appointment_id", map = "function(doc) {if(doc.type === 'AppointmentCalendar' && doc.visits){ for(var i in doc.visits){if(doc.visits[i].appointment){emit(doc.visits[i].appointment.id, doc.visits[i].appointment);}}}}")
    public Appointment findAppointmentById(String appointmentId) {
        ViewQuery query = createQuery("by_appointment_id").key(appointmentId);
        List<Appointment> appointments = db.queryView(query, Appointment.class);
        return CollectionUtils.isEmpty(appointments) ? null : appointments.get(0);
    }
}
