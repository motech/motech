package org.motechproject.scheduletracking.api.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
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

    @View(name = "find_active_by_external_id_and_schedule_name", map = "function(doc) {{emit([doc.externalId, doc.scheduleName, doc.status]);}}")
    public Enrollment getActiveEnrollment(String externalId, String scheduleName) {
        List<Enrollment> enrollments = queryView("find_active_by_external_id_and_schedule_name", ComplexKey.of(externalId, scheduleName, EnrollmentStatus.ACTIVE.name()));
        return enrollments.isEmpty() ? null : enrollments.get(0);
    }

    public Enrollment addOrReplace(Enrollment enrollment) {
        Enrollment existingEnrollment = getActiveEnrollment(enrollment.getExternalId(), enrollment.getScheduleName());

        if (existingEnrollment == null) {
            add(enrollment);
            existingEnrollment = enrollment;
        } else
            update(existingEnrollment.copyFrom(enrollment));

        return existingEnrollment;
    }
}
