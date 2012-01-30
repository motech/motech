package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.ScheduleTrackingException;
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

    @Autowired
    public ScheduleTrackingServiceImpl(MotechSchedulerService schedulerService, AllTrackedSchedules allTrackedSchedules, AllEnrollments allEnrollments) {
        this.schedulerService = schedulerService;
        this.allTrackedSchedules = allTrackedSchedules;
        this.allEnrollments = allEnrollments;
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
            enrollment = new Enrollment(externalId, schedule, now(), referenceDate, enrollmentRequest.getStartingMilestoneName());
        else
            enrollment = new Enrollment(externalId, schedule, now(), referenceDate);
        allEnrollments.add(enrollment);
    }
}
