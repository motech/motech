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
    private AllTrackedSchedules allTrackedSchedules;

    @Autowired
    public AllEnrollments(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db) {
        super(Enrollment.class, db);
    }

    @View(name = "find_active_by_external_id_and_schedule_name", map = "function(doc) {{emit([doc.externalId, doc.scheduleName, doc.status]);}}")
    public Enrollment getActiveEnrollment(String externalId, String scheduleName) {
        List<Enrollment> enrollments = queryView("find_active_by_external_id_and_schedule_name", ComplexKey.of(externalId, scheduleName, EnrollmentStatus.ACTIVE.name()));
        return enrollments.isEmpty() ? null : populateSchedule(enrollments.get(0));
    }

    public static final String FUNCTION_DOC_EMIT_DOC_METADATA = "function(doc) {\n" +
            "  for (i = 0; i < doc.metadata.length; i++)\n" +
            "    emit([doc.metadata[i].property, doc.metadata[i].value], doc._id);\n" +
            "}";
    @View(name = "find_by_property", map = FUNCTION_DOC_EMIT_DOC_METADATA)
    public List<Enrollment> findByMetadataProperty(String property, String value) {
        return queryView("find_by_property", ComplexKey.of(property, value));
    }

    @View(name = "find_by_external_id", map = "function(doc) { emit(doc.externalId); }")
    public List<Enrollment> findByExternalId(String externalId) {
        return queryView("find_by_external_id", externalId);
    }

    @Override
    public List<Enrollment> getAll()
    {
        List<Enrollment> enrollments = super.getAll();
        for(Enrollment enrollment : enrollments)
            populateSchedule(enrollment);
        return enrollments;
    }

    private Enrollment populateSchedule(Enrollment enrollment) {
        enrollment.setSchedule(allTrackedSchedules.getByName(enrollment.getScheduleName()));
        return enrollment;
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
