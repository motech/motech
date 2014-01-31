package org.motechproject.scheduler.impl;

import org.joda.time.DateTime;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerActionProxyService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("schedulerActionProxyService")
public class MotechSchedulerActionProxyServiceImpl implements MotechSchedulerActionProxyService {
    private MotechSchedulerService scheduler;

    @Autowired
    public MotechSchedulerActionProxyServiceImpl(MotechSchedulerService schedulerService) {
        this.scheduler = schedulerService;
    }

    @Override
    public void scheduleCronJob(String subject, Map<Object, Object> parameters, String cronExpression, DateTime startTime, DateTime endTime, Boolean ignorePastFiresAtStart) {
        MotechEvent motechEvent = new MotechEvent(subject, createMotechEventParameters(parameters));
        CronSchedulableJob job = new CronSchedulableJob(motechEvent, cronExpression, startTime.toDate(), endTime.toDate(), ignorePastFiresAtStart);

        scheduler.scheduleJob(job);
    }

    @Override
    public void scheduleRepeatingJob(String subject, // NO CHECKSTYLE More than 7 parameters (found 8).
                                     Map<Object, Object> parameters, DateTime startTime, DateTime endTime,
                                     Integer repeatCount, Long repeatIntervalInMilliSeconds,
                                     Boolean ignorePastFiresAtStart, Boolean useOriginalFireTimeAfterMisfire) {
        MotechEvent motechEvent = new MotechEvent(subject, createMotechEventParameters(parameters));
        RepeatingSchedulableJob job = new RepeatingSchedulableJob()
                .setMotechEvent(motechEvent)
                .setStartTime(startTime.toDate())
                .setEndTime(endTime.toDate())
                .setRepeatCount(repeatCount)
                .setRepeatIntervalInMilliSeconds(repeatIntervalInMilliSeconds)
                .setIgnorePastFiresAtStart(ignorePastFiresAtStart)
                .setUseOriginalFireTimeAfterMisfire(useOriginalFireTimeAfterMisfire);

        scheduler.scheduleRepeatingJob(job);
    }

    @Override
    public void scheduleRunOnceJob(String subject, Map<Object, Object> parameters, DateTime startDate) {
        MotechEvent motechEvent = new MotechEvent(subject, createMotechEventParameters(parameters));
        RunOnceSchedulableJob job = new RunOnceSchedulableJob(motechEvent, startDate.toDate());

        scheduler.scheduleRunOnceJob(job);
    }

    @Override
    public void scheduleDayOfWeekJob(String subject, Map<Object, Object> parameters, DateTime start, DateTime end, List<Object> days, DateTime time, Boolean ignorePastFiresAtStart) {
        MotechEvent motechEvent = new MotechEvent(subject, createMotechEventParameters(parameters));
        Time jobTime = new Time(time.getHourOfDay(), time.getMinuteOfHour());
        List<DayOfWeek> jobDayOfWeeks = createDayOfWeeks(days);

        DayOfWeekSchedulableJob job = new DayOfWeekSchedulableJob(motechEvent, start.toLocalDate(), end.toLocalDate(), jobDayOfWeeks, jobTime, ignorePastFiresAtStart);

        scheduler.scheduleDayOfWeekJob(job);
    }

    @Override
    public void unscheduleJobs(String subject) {
        scheduler.safeUnscheduleAllJobs(subject);
    }

    private List<DayOfWeek> createDayOfWeeks(List<Object> list) {
        List<DayOfWeek> dayOfWeeks = new ArrayList<>(list.size());

        for (Object item : list) {
            DayOfWeek dayOfWeek = DayOfWeek.parse(item.toString());

            if (dayOfWeek != null) {
                dayOfWeeks.add(dayOfWeek);
            }
        }

        return dayOfWeeks;
    }

    private Map<String, Object> createMotechEventParameters(Map<Object, Object> map) {
        Map<String, Object> parameters = new HashMap<>(map.size());

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            parameters.put(entry.getKey().toString(), entry.getValue());
        }

        return parameters;
    }

}
