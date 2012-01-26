package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.CronJobExpressionBuilder;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.ScheduleTrackingException;
import org.motechproject.scheduletracking.api.events.EnrolledEntityAlertEvent;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

        List<Enrollment> enrollments = allEnrollments.findByExternalIdAndScheduleName(externalId, scheduleName);
        if (!enrollments.isEmpty()) return;

        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        if (schedule == null)
            throw new ScheduleTrackingException("No schedule with name: %s", scheduleName);

        LocalDate referenceDate = enrollmentRequest.getReferenceDate();

        String currentMilestoneName = schedule.getFirstMilestone().getName();
        if (enrollmentRequest.enrollIntoMilestone())
            allEnrollments.add(new Enrollment(externalId, scheduleName, now(), referenceDate, enrollmentRequest.getStartingMilestoneName()));
        else
            allEnrollments.add(new Enrollment(externalId, scheduleName, now(), referenceDate, currentMilestoneName));

        MotechEvent motechEvent = new EnrolledEntityAlertEvent(schedule.getName(), externalId).toMotechEvent();
        String cronJobExpression = new CronJobExpressionBuilder(enrollmentRequest.getPreferredAlertTime(), 0, 0).build();
        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, now().toDate(), schedule.getEndDate(referenceDate).toDate());
        schedulerService.scheduleJob(schedulableJob);
    }
}
