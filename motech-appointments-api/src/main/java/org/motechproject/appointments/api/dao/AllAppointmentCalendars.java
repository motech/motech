package org.motechproject.appointments.api.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
}