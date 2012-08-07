package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.domain.exception.ScheduleTrackingException;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentUpdater;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.MilestoneAlerts;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.scheduletracking.api.service.contract.UpdateCriteria;
import org.motechproject.scheduletracking.api.service.contract.UpdateCriterion;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static org.motechproject.util.DateUtil.newDateTime;

public class ScheduleTrackingServiceImpl implements ScheduleTrackingService {
    private AllSchedules allSchedules;
    private AllEnrollments allEnrollments;
    private EnrollmentService enrollmentService;
    private EnrollmentsQueryService enrollmentsQueryService;
    private EnrollmentRecordMapper enrollmentRecordMapper;
    private TrackedSchedulesJsonReader schedulesJsonReader;

    @Autowired
    public ScheduleTrackingServiceImpl(AllSchedules allSchedules, AllEnrollments allEnrollments, EnrollmentService enrollmentService, EnrollmentsQueryService enrollmentsQueryService, EnrollmentRecordMapper enrollmentRecordMapper) {
        this.allSchedules = allSchedules;
        this.allEnrollments = allEnrollments;
        this.enrollmentService = enrollmentService;
        this.enrollmentsQueryService = enrollmentsQueryService;
        this.enrollmentRecordMapper = enrollmentRecordMapper;
        this.schedulesJsonReader = new TrackedSchedulesJsonReaderImpl();
    }

    @Override
    public EnrollmentRecord getEnrollment(String externalId, String scheduleName) {
        Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
        return enrollmentRecordMapper.map(activeEnrollment);
    }

    @Override
    public void updateEnrollment(String externalId, String scheduleName, UpdateCriteria updateCriteria) {
        Enrollment enrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
        if (enrollment == null) {
            throw new InvalidEnrollmentException(
                    format("Cannot find an active enrollment with " +
                            "External ID: {0} & Schedule name: {1}", externalId, scheduleName));
        } else {
            Map<UpdateCriterion, Object> criteria = updateCriteria.getAll();
            for (UpdateCriterion updateCriterion : criteria.keySet()) {
                EnrollmentUpdater.get(updateCriterion).update(enrollment, criteria.get(updateCriterion));
            }
            allEnrollments.update(enrollment);
        }
    }

    @Override
    public List<EnrollmentRecord> search(EnrollmentsQuery query) {
        List<EnrollmentRecord> enrollmentRecords = new ArrayList<EnrollmentRecord>();
        for (Enrollment enrollment : enrollmentsQueryService.search(query)) {
            enrollmentRecords.add(enrollmentRecordMapper.map(enrollment));
        }
        return enrollmentRecords;
    }

    @Override
    public List<EnrollmentRecord> searchWithWindowDates(EnrollmentsQuery query) {
        List<EnrollmentRecord> enrollmentRecords = new ArrayList<EnrollmentRecord>();
        for (Enrollment enrollment : enrollmentsQueryService.search(query)) {
            enrollmentRecords.add(enrollmentRecordMapper.mapWithDates(enrollment));
        }
        return enrollmentRecords;
    }

    @Override
    public MilestoneAlerts getAlertTimings(EnrollmentRequest enrollmentRequest) {
        Schedule schedule = allSchedules.getByName(enrollmentRequest.getScheduleName());
        if (schedule == null) {
            throw new ScheduleTrackingException("No schedule with name: %s", enrollmentRequest.getScheduleName());
        }

        String startingMilestoneName;
        if (enrollmentRequest.isStartingMilestoneSpecified()) {
            startingMilestoneName = enrollmentRequest.getStartingMilestoneName();
        } else {
            startingMilestoneName = schedule.getFirstMilestone().getName();
        }

        return enrollmentService.getAlertTimings(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName(), startingMilestoneName, enrollmentRequest.getReferenceDateTime(), enrollmentRequest.getEnrollmentDateTime(), enrollmentRequest.getPreferredAlertTime());
    }

    @Override
    public void add(String scheduleJson) {
        ScheduleRecord schedule = schedulesJsonReader.getSchedule(scheduleJson);

        allSchedules.add(schedule);
    }

    @Override
    public String enroll(EnrollmentRequest enrollmentRequest) {
        Schedule schedule = allSchedules.getByName(enrollmentRequest.getScheduleName());
        if (schedule == null) {
            throw new ScheduleTrackingException("No schedule with name: %s", enrollmentRequest.getScheduleName());
        }

        String startingMilestoneName;
        if (enrollmentRequest.isStartingMilestoneSpecified()) {
            startingMilestoneName = enrollmentRequest.getStartingMilestoneName();
        } else {
            startingMilestoneName = schedule.getFirstMilestone().getName();
        }

        return enrollmentService.enroll(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName(), startingMilestoneName, enrollmentRequest.getReferenceDateTime(), enrollmentRequest.getEnrollmentDateTime(), enrollmentRequest.getPreferredAlertTime(), enrollmentRequest.getMetadata());
    }

    @Override
    public void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate, Time fulfillmentTime) {
        Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
        if (activeEnrollment == null) {
            throw new InvalidEnrollmentException(format("Can fulfill only active enrollments. This enrollment has: External ID: {0}, Schedule name: {1}", externalId, scheduleName));
        }
        if (isDuplicateFulfillment(activeEnrollment, fulfillmentDate, fulfillmentTime)) {
            return;
        }
        enrollmentService.fulfillCurrentMilestone(activeEnrollment, newDateTime(fulfillmentDate, fulfillmentTime));
    }

    private boolean isDuplicateFulfillment(Enrollment activeEnrollment, LocalDate fulfillmentDate, Time fulfillmentTime) {
        return activeEnrollment.getFulfillments().size() > 0 && activeEnrollment.getLastFulfilledDate().equals(newDateTime(fulfillmentDate, fulfillmentTime));
    }

    @Override
    public void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate) {
        fulfillCurrentMilestone(externalId, scheduleName, fulfillmentDate, new Time(0, 0));
    }

    @Override
    public void unenroll(String externalId, List<String> scheduleNames) {
        for (String scheduleName : scheduleNames) {
            Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
            if (activeEnrollment != null) {
                enrollmentService.unenroll(activeEnrollment);
            }
        }
    }
}
