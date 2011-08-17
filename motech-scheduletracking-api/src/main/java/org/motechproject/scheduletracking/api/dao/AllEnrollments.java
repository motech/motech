package org.motechproject.scheduletracking.api.dao;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AllEnrollments extends MotechAuditableRepository<Enrollment> {
    @Autowired
    public AllEnrollments(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db) {
        super(Enrollment.class, db);
    }
}
