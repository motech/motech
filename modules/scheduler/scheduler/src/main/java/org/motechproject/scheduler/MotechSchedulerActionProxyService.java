package org.motechproject.scheduler;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

public interface MotechSchedulerActionProxyService {

    void scheduleCronJob(String subject, Map<Object, Object> parameters, String cronExpression, DateTime startTime, DateTime endTime, Boolean ignorePastFiresAtStart);

    void scheduleRepeatingJob(String subject, Map<Object, Object> parameters, DateTime startTime, DateTime endTime, Integer repeatCount, Long repeatIntervalInMilliSeconds, Boolean ignorePastFiresAtStart, Boolean useOriginalFireTimeAfterMisfire);

    void scheduleRunOnceJob(String subject, Map<Object, Object> parameters, DateTime startDate);

    void scheduleDayOfWeekJob(String subject, Map<Object, Object> parameters, DateTime start, DateTime end, List<Object> days, DateTime time, Boolean ignorePastFiresAtStart);

    void unscheduleJobs(String subject);
}
