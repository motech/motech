package org.motechproject.appointments.api.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AllVisits extends MotechBaseRepository<Visit> {
    @Autowired
    public AllVisits(@Qualifier("appointmentsDatabase") CouchDbConnector db) {
        super(Visit.class, db);
    }

    @GenerateView
    public List<Visit> findByAppointmentId(String appointmentId) {
        List<Visit> visits = queryView("by_appointmentId", appointmentId);
        if (null == visits) {
            visits = Collections.<Visit>emptyList();
        }
        return visits;
    }

    @GenerateView
    public List<Visit> findByExternalId(String externalId) {
        List<Visit> visits = queryView("by_externalId", externalId);
        if (null == visits) {
            visits = Collections.<Visit>emptyList();
        }
        return visits;
    }
}
