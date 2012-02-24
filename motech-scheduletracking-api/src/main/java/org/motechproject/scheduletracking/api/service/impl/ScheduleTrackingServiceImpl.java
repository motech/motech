package org.motechproject.scheduletracking.api.service.impl;

import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.domain.exception.ScheduleTrackingException;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.text.MessageFormat.format;
import static org.motechproject.util.DateUtil.today;

@Service
public class ScheduleTrackingServiceImpl implements ScheduleTrackingService {
    private AllTrackedSchedules allTrackedSchedules;
    private AllEnrollments allEnrollments;
    private EnrollmentService enrollmentService;

    @Autowired
    public ScheduleTrackingServiceImpl(AllTrackedSchedules allTrackedSchedules, AllEnrollments allEnrollments, EnrollmentService enrollmentService) {
        this.allTrackedSchedules = allTrackedSchedules;
        this.allEnrollments = allEnrollments;
        this.enrollmentService = enrollmentService;
    }

    @Override
    public EnrollmentResponse getEnrollment(String externalId, String scheduleName) {
        Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
        return new EnrollmentResponseMapper().map(activeEnrollment);
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

        return enrollmentService.enroll(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName(), startingMilestoneName, enrollmentRequest.getReferenceDate(), today(), enrollmentRequest.getPreferredAlertTime());
    }

    @Override
    public void fulfillCurrentMilestone(String externalId, String scheduleName) {
        Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
        if (activeEnrollment == null) {
            throw new InvalidEnrollmentException(format("Can fulfill only active enrollments. " +
                    "This enrollment has: External ID: {0}, Schedule name: {1}", externalId, scheduleName));
        }

        enrollmentService.fulfillCurrentMilestone(activeEnrollment);
    }

    @Override
    public void unenroll(String externalId, String scheduleName) {
        Enrollment activeEnrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
        if (activeEnrollment == null)
            throw new InvalidEnrollmentException();
        enrollmentService.unenroll(activeEnrollment);
    }
}
