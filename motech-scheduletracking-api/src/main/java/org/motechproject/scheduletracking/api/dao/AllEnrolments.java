package org.motechproject.scheduletracking.api.dao;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.scheduletracking.api.domain.enrolment.Enrolment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AllEnrolments extends MotechAuditableRepository<Enrolment> {
    @Autowired
    public AllEnrolments(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db) {
        super(Enrolment.class, db);
    }
}
