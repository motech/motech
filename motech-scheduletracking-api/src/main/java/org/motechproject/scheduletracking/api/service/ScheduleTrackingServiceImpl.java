package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.joda.time.LocalDate.now;

@Component
public class ScheduleTrackingServiceImpl implements ScheduleTrackingService {

    private AllTrackedSchedules allTrackedSchedules;
    private MotechSchedulerService schedulerService;
    private AllEnrollments allEnrollments;
    private EnrollmentService enrollmentService;

    @Autowired
    public ScheduleTrackingServiceImpl(MotechSchedulerService schedulerService, AllTrackedSchedules allTrackedSchedules, AllEnrollments allEnrollments, EnrollmentService enrollmentService) {
        this.schedulerService = schedulerService;
        this.allTrackedSchedules = allTrackedSchedules;
        this.allEnrollments = allEnrollments;
        this.enrollmentService = enrollmentService;
    }

    @Override
    public void enroll(EnrollmentRequest enrollmentRequest) {
        String externalId = enrollmentRequest.getExternalId();
        String scheduleName = enrollmentRequest.getScheduleName();
        LocalDate referenceDate = enrollmentRequest.getReferenceDate();

        Enrollment enrollment = allEnrollments.findByExternalIdAndScheduleName(externalId, scheduleName);
        if (enrollment != null) return;

        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        if (schedule == null) {
            throw new ScheduleTrackingException("No schedule with name: %s", scheduleName);
        }

        if (enrollmentRequest.enrollIntoMilestone())
            enrollment = new Enrollment(externalId, schedule, referenceDate, now(), enrollmentRequest.getPreferredAlertTime(), enrollmentRequest.getStartingMilestoneName());
        else
            enrollment = new Enrollment(externalId, schedule, referenceDate, now(), enrollmentRequest.getPreferredAlertTime());
        allEnrollments.add(enrollment);
        enrollmentService.scheduleAlertsForCurrentMilestone(enrollment);
    }

    @Override
    public void fulfillCurrentMilestone(String externalId, String scheduleName) {
        enrollmentService.fulfillCurrentMilestone(allEnrollments.findByExternalIdAndScheduleName(externalId, scheduleName));
    }
}
