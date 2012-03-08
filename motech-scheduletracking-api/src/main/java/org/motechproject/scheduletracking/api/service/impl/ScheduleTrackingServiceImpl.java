package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.domain.exception.ScheduleTrackingException;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.text.MessageFormat.format;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.today;

@Service
public class ScheduleTrackingServiceImpl implements ScheduleTrackingService {
    private AllTrackedSchedules allTrackedSchedules;
    private AllEnrollments allEnrollments;
    private EnrollmentService enrollmentService;
    private EnrollmentsQueryService enrollmentsQueryService;
    private EnrollmentRecordMapper enrollmentRecordMapper;

    @Autowired
    public ScheduleTrackingServiceImpl(AllTrackedSchedules allTrackedSchedules, AllEnrollments allEnrollments, EnrollmentService enrollmentService, EnrollmentsQueryService enrollmentsQueryService, EnrollmentRecordMapper enrollmentRecordMapper) {
        this.allTrackedSchedules = allTrackedSchedules;
        this.allEnrollments = allEnrollments;
        this.enrollmentService = enrollmentService;
        this.enrollmentsQueryService = enrollmentsQueryService;
        this.enrollmentRecordMapper = enrollmentRecordMapper;
    }

    @Override
    public EnrollmentRecord getEnrollment(String externalId, String scheduleName) {
        Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
        return enrollmentRecordMapper.map(activeEnrollment);
    }

    @Override
    public List<EnrollmentRecord> search(EnrollmentsQuery query) {
        List<EnrollmentRecord> enrollmentRecords = new ArrayList<EnrollmentRecord>();
        for (Enrollment enrollment : enrollmentsQueryService.search(query))
            enrollmentRecords.add(enrollmentRecordMapper.map(enrollment));
        return enrollmentRecords;
    }

    @Override
    public List<EnrollmentRecord> searchWithWindowDates(EnrollmentsQuery query) {
        List<EnrollmentRecord> enrollmentRecords = new ArrayList<EnrollmentRecord>();
        for (Enrollment enrollment : enrollmentsQueryService.search(query))
            enrollmentRecords.add(enrollmentRecordMapper.mapWithDates(enrollment));
        return enrollmentRecords;
    }

    @Override
    public String enroll(EnrollmentRequest enrollmentRequest) {
        Schedule schedule = allTrackedSchedules.getByName(enrollmentRequest.getScheduleName());
        if (schedule == null)
            throw new ScheduleTrackingException("No schedule with name: %s", enrollmentRequest.getScheduleName());

        String startingMilestoneName;
        if (enrollmentRequest.enrollIntoMilestone())
            startingMilestoneName = enrollmentRequest.getStartingMilestoneName();
        else
            startingMilestoneName = schedule.getFirstMilestone().getName();

        return enrollmentService.enroll(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName(), startingMilestoneName, enrollmentRequest.getReferenceDateTime(), enrollmentRequest.getEnrollmentDateTime(), enrollmentRequest.getPreferredAlertTime());
    }

    @Override
    public void fulfillCurrentMilestone(String externalId, String scheduleName) {
        fulfillCurrentMilestone(externalId, scheduleName, today(), new Time(0, 0));
    }

    @Override
    public void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate) {
        fulfillCurrentMilestone(externalId, scheduleName, fulfillmentDate, new Time(0, 0));
    }

    @Override
    public void fulfillCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate, Time fulfillmentTime) {
        Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
        if (activeEnrollment == null) {
            throw new InvalidEnrollmentException(format("Can fulfill only active enrollments. " +
                    "This enrollment has: External ID: {0}, Schedule name: {1}", externalId, scheduleName));
        }

        enrollmentService.fulfillCurrentMilestone(activeEnrollment, newDateTime(fulfillmentDate, fulfillmentTime));
    }

    @Override
    public void unenroll(String externalId, List<String> scheduleNames) {
        for (String scheduleName : scheduleNames) {
            Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
            if (activeEnrollment != null)
                enrollmentService.unenroll(activeEnrollment);
        }
    }
}
