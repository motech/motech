package org.motechproject.appointments.api.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AllAppointments extends MotechBaseRepository<Appointment> {

    @Autowired
    public AllAppointments(@Qualifier("appointmentsDatabase") CouchDbConnector db) {
        super(Appointment.class, db);
    }

    @GenerateView
    public List<Appointment> findByExternalId(String externalId) {
        List<Appointment> appointments = queryView("by_externalId", externalId);
        if (null == appointments) {
            appointments = Collections.<Appointment>emptyList();
        }
        return appointments;
    }
}
