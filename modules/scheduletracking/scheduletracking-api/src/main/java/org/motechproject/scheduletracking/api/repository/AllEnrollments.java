package org.motechproject.scheduletracking.api.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllEnrollments extends MotechBaseRepository<Enrollment> {
    @Autowired
    private AllSchedules allSchedules;

    @Autowired
    public AllEnrollments(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db) {
        super(Enrollment.class, db);
    }

    @View(name = "find_active_by_external_id_and_schedule_name", map = "function(doc) {if(doc.type === 'Enrollment') emit([doc.externalId, doc.scheduleName, doc.status]);}")
    public Enrollment getActiveEnrollment(String externalId, String scheduleName) {
        List<Enrollment> enrollments = queryView("find_active_by_external_id_and_schedule_name", ComplexKey.of(externalId, scheduleName, EnrollmentStatus.ACTIVE.name()));
        return enrollments.isEmpty() ? null : populateSchedule(enrollments.get(0));
    }

    private static final String FUNCTION_DOC_EMIT_DOC_METADATA = "function(doc) {\n" +
            "if(doc.type === 'Enrollment') for (var prop in doc.metadata)" +
            "    emit([prop, doc.metadata[prop]], doc._id);\n" +
            "}";

    @View(name = "by_property", map = FUNCTION_DOC_EMIT_DOC_METADATA)
    public List<Enrollment> findByMetadataProperty(String property, String value) {
        List<Enrollment> enrollments = queryView("by_property", ComplexKey.of(property, value));
        return populateWithSchedule(enrollments);
    }

    @View(name = "by_external_id", map = "function(doc) { if(doc.type === 'Enrollment') emit(doc.externalId); }")
    public List<Enrollment> findByExternalId(String externalId) {
        List<Enrollment> enrollments = queryView("by_external_id", externalId);
        return populateWithSchedule(enrollments);
    }

    @View(name = "by_schedule", map = "function(doc) { if(doc.type === 'Enrollment') emit(doc.scheduleName); }")
    public List<Enrollment> findBySchedule(List<String> scheduleName) {
        List<Enrollment> enrollments = queryViewWithKeyList("by_schedule", scheduleName);
        return populateWithSchedule(enrollments);
    }

    @View(name = "by_current_milestone", map = "function(doc) { if(doc.type === 'Enrollment') emit(doc.currentMilestoneName); }")
    public List<Enrollment> findByCurrentMilestone(String milestoneName) {
        List<Enrollment> enrollments = queryView("by_current_milestone", milestoneName);
        return populateWithSchedule(enrollments);
    }

    @View(name = "by_status", map = "function(doc) { if(doc.type === 'Enrollment') emit(doc.status); }")
    public List<Enrollment> findByStatus(EnrollmentStatus status) {
        List<Enrollment> enrollments = queryView("by_status", status.name());
        return populateWithSchedule(enrollments);
    }

    @View(name = "by_completed_in_time_range", map = "function(doc){ if(doc.type === 'Enrollment' && doc.status == 'COMPLETED' && doc.fulfillments.length != 0) { emit(doc.fulfillments[doc.fulfillments.length - 1].fulfillmentDateTime, null); }} ")
    public List<Enrollment> completedDuring(DateTime start, DateTime end) {
        List<Enrollment> enrollments = db.queryView(createQuery("by_completed_in_time_range").startKey(start).endKey(end).includeDocs(true), Enrollment.class);
        return populateWithSchedule(enrollments);
    }

    @Override
    public List<Enrollment> getAll() {
        List<Enrollment> enrollments = super.getAll();
        for (Enrollment enrollment : enrollments) {
            populateSchedule(enrollment);
        }
        return enrollments;
    }

    private List<Enrollment> populateWithSchedule(List<Enrollment> enrollments) {
        for (Enrollment enrollment : enrollments) {
            populateSchedule(enrollment);
        }
        return enrollments;
    }

    private Enrollment populateSchedule(Enrollment enrollment) {
        enrollment.setSchedule(allSchedules.getByName(enrollment.getScheduleName()));
        return enrollment;
    }
}
