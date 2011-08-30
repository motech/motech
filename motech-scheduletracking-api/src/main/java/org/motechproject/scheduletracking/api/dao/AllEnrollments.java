package org.motechproject.scheduletracking.api.dao;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllEnrollments extends MotechAuditableRepository<Enrollment> {
    @Autowired
    public AllEnrollments(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db) {
        super(Enrollment.class, db);
    }

    @View(name = "find_by_external_id_and_schedule_name", map = "function(doc) {{emit([doc.externalId, doc.scheduleName]);}}")
    public List<Enrollment> findByExternalIdAndScheduleName(String externalId, String scheduleName) {
        return queryView("find_by_external_id_and_schedule_name", ComplexKey.of(externalId, scheduleName));
    }
}
