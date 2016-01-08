package org.motechproject.scheduler.service;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;
import java.util.Map;

/**
 * Proxy registered with the task channel, exposing the scheduler service as task actions.
 */
public interface MotechSchedulerActionProxyService {

    /**
     * Schedules job, which will be fired on every match with given cron expression.
     *
     * @param subject  the subject for {@code MotechEvent} fired, when job is triggered, not null
     * @param parameters  the parameters for {@code MotechEvent}, not null
     * @param cronExpression  the cron expression defining when job should be triggered, not null
     * @param startTime  the {@code DateTime} at which should become ACTIVE, not null
     * @param endTime  the {@code DateTime} at which job should be stopped, null treated as never end.
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not, not null
     */
    void scheduleCronJob(String subject, Map<Object, Object> parameters, String cronExpression, DateTime startTime, DateTime endTime, Boolean ignorePastFiresAtStart);

    /**
     * Schedules job, which will be fired every user-specified time interval(in milliseconds), but won’t be fired more than given number of times.
     *
     * @param subject  the subject for {@code MotechEvent} fired, when job is triggered, not null
     * @param parameters  the parameters for {@code MotechEvent}, not null
     * @param startTime  the {@code DateTime} at which should become ACTIVE, not null
     * @param endTime  the {@code DateTime} at which job should be stopped, null treated as never end
     * @param repeatCount  the number of time job should be triggered, -1 treated as infinite, not null
     * @param repeatIntervalInSeconds  the interval(in seconds) between job fires, not null
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not, not null
     * @param useOriginalFireTimeAfterMisfire  the flag defining whether job should use original fire time after misfire, not null
     */
    void scheduleRepeatingJob(String subject, // NO CHECKSTYLE More than 7 parameters (found 8).
                              Map<Object, Object> parameters, DateTime startTime, DateTime endTime, Integer repeatCount,
                              Integer repeatIntervalInSeconds, Boolean ignorePastFiresAtStart,
                              Boolean useOriginalFireTimeAfterMisfire);

    /**
     * Schedules job, which will be fired every, user-specified period.
     *
     * @param subject  the subject for {@code MotechEvent} fired, when job is triggered, not null
     * @param parameters  the parameters for {@code MotechEvent}, not null
     * @param startTime  the {@code DateTime} at which should become ACTIVE, not null
     * @param endTime  the {@code DateTime} at which job should be stopped, null treated as never end
     * @param repeatPeriod  the {@code Period} between job fires, not null
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not, not null
     * @param useOriginalFireTimeAfterMisfire  the flag defining whether job should use original fire time after misfire
     */
    void scheduleRepeatingPeriodJob(String subject, Map<Object, Object> parameters, DateTime startTime, DateTime endTime,
                                    Period repeatPeriod, Boolean ignorePastFiresAtStart, Boolean useOriginalFireTimeAfterMisfire);

    /**
     * Schedules job, which will be fired only once at date given by user.
     *
     * @param subject  the subject for {@code MotechEvent} fired, when job is triggered, not null
     * @param parameters  the parameters for {@code MotechEvent}, not null
     * @param startDate  the {@code DateTime} at which should become ACTIVE, not null
     */
    void scheduleRunOnceJob(String subject, Map<Object, Object> parameters, DateTime startDate);

    /**
     * Schedules job, which will be fired at given time on days of week provided by user.
     *
     * @param subject  the subject for {@code MotechEvent} fired, when job is triggered, not null
     * @param parameters  the parameters for {@code MotechEvent}, not null
     * @param start  the {@code DateTime} at which should become ACTIVE, not null
     * @param end  the {@code DateTime} at which job should be stopped, null treated as never end
     * @param days  the list of days at which job should be fired, not null
     * @param time  the {@code Time} at which job should be fired
     * @param ignorePastFiresAtStart  the flag defining whether job should ignore past fires at start or not, not null
     */
    void scheduleDayOfWeekJob(String subject, Map<Object, Object> parameters, DateTime start, DateTime end, List<Object> days, DateTime time, Boolean ignorePastFiresAtStart);

    /**
     * Unschedules all jobs with given subject in their name.
     *
     * @param subject  the subject for deleting jobs
     */
    void unscheduleJobs(String subject);
}
