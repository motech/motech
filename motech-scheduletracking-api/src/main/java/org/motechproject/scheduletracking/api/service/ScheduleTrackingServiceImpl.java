package org.motechproject.scheduletracking.api.service;

import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.contract.EnrolmentRequest;
import org.motechproject.scheduletracking.api.dao.AllEnrolments;
import org.motechproject.scheduletracking.api.dao.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.domain.Enrolment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.ScheduleTrackingException;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduleTrackingServiceImpl implements ScheduleTrackingService {
    @Autowired
    private AllTrackedSchedules allTrackedSchedules;
    @Autowired
    private MotechSchedulerService schedulerService;
    @Autowired
    private AllEnrolments allEnrolments;

    @Override
    public void enrol(EnrolmentRequest enrolmentRequest) {
        Schedule schedule = allTrackedSchedules.get(enrolmentRequest.getScheduleName());
        if (schedule == null) throw new ScheduleTrackingException("No schedule with name: " + enrolmentRequest.getScheduleName());

        WallTime wallTime = new WallTime(enrolmentRequest.getEnroledAt(), WallTimeUnit.valueOf(enrolmentRequest.getEnroledInMilestone()));
        allEnrolments.add(new Enrolment(enrolmentRequest.getExternalId(), new Date(), wallTime, enrolmentRequest.getScheduleName()));

//        MotechEvent motechEvent = new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER, eventParams);
//        String cronJobExpression = new CronJobExpressionBuilder(
//                dosage.getStartTime(),
//                pillRegimen.getReminderRepeatWindowInHours(),
//                pillRegimen.getReminderRepeatIntervalInMinutes()).build();
//
//        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, dosage.getStartDate(), dosage.getEndDate());
//        schedulerService.scheduleJob(schedulableJob);
    }
}
