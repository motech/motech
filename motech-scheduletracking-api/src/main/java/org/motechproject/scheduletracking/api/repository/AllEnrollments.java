package org.motechproject.scheduletracking.api.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllEnrollments extends MotechBaseRepository<Enrollment> {
    @Autowired
    public AllEnrollments(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db) {
        super(Enrollment.class, db);
    }

    @View(name = "find_by_external_id_and_schedule_name", map = "function(doc) {{emit([doc.externalId, doc.scheduleName]);}}")
    public Enrollment findByExternalIdAndScheduleName(String externalId, String scheduleName) {
        List<Enrollment> enrollments = queryView("find_by_external_id_and_schedule_name", ComplexKey.of(externalId, scheduleName));
        return enrollments.isEmpty() ? null : enrollments.get(0);
    }

    public Enrollment addOrReplace(Enrollment enrollment) {
        Enrollment existingEnrollment = findByExternalIdAndScheduleName(enrollment.getExternalId(), enrollment.getScheduleName());
        if (existingEnrollment == null) {
            add(enrollment);
        } else {
            update(existingEnrollment.copyFrom(enrollment));
        }
        return enrollment;
    }
}
