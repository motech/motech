package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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
    public void enroll(EnrollmentRequest enrollmentRequest) {
        String externalId = enrollmentRequest.getExternalId();
        String scheduleName = enrollmentRequest.getScheduleName();
        LocalDate referenceDate = enrollmentRequest.getReferenceDate();

        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        if (schedule == null) {
            throw new ScheduleTrackingException("No schedule with name: %s", scheduleName);
        }

        if (allEnrollments.findActiveByExternalIdAndScheduleName(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName()) != null)
            throw new ActiveEnrollmentExistsException("entity already has an active enrollment. unenroll the entity before enrolling in the same schedule.");

        Enrollment enrollment;
        if (enrollmentRequest.enrollIntoMilestone())
            enrollment = new Enrollment(externalId, scheduleName, enrollmentRequest.getStartingMilestoneName(), referenceDate, DateUtil.today(), enrollmentRequest.getPreferredAlertTime());
        else
            enrollment = new Enrollment(externalId, scheduleName, schedule.getFirstMilestone().getName(), referenceDate, DateUtil.today(), enrollmentRequest.getPreferredAlertTime());
        allEnrollments.add(enrollment);
        enrollmentService.scheduleAlertsForCurrentMilestone(enrollment);
    }

    @Override
    public void fulfillCurrentMilestone(String externalId, String scheduleName) {
        enrollmentService.fulfillCurrentMilestone(allEnrollments.findActiveByExternalIdAndScheduleName(externalId, scheduleName));
    }

    @Override
    public void unenroll(String externalId, String scheduleName) {
        Enrollment activeEnrollment = allEnrollments.findActiveByExternalIdAndScheduleName(externalId, scheduleName);
        if (activeEnrollment == null)
            throw new InvalidEnrollmentException("entity is not currently enrolled into the schedule.");
        enrollmentService.unscheduleAllAlerts(activeEnrollment);
        activeEnrollment.setActive(false);
        allEnrollments.update(activeEnrollment);
    }
}
