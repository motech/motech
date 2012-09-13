package org.motechproject.openmrs.atomfeed;

import org.apache.commons.lang.ObjectUtils;
import org.joda.time.LocalTime;
import org.motechproject.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

/**
 * This class is responsible for configuring the motech scheduler to fire events
 * to poll the OpenMRS. Currently it can be configured in 2 ways: <br />
 * - Daily polling, where an event is fired once a day at a time specified by
 * the user <br />
 * - Interval polling, where an event is fired on some interval specified by the
 * user (e.g. every 1 hour) <br />
 */
public class PollingConfiguration {

    private static final int LAST_MINUTE_IN_HOUR = 59;
    private static final int LAST_HOUR_IN_DAY = 23;
    private static final Long MILLISECONDS_IN_MINUTE = 1000 * 60L;
    private static final Long MILLISECONDS_IN_HOUR = MILLISECONDS_IN_MINUTE * 60;
    private static final Long MILLISECONDS_IN_DAY = MILLISECONDS_IN_HOUR * 24;

    public static final String POLLING_JOB_ID = "polling.job";

    private final MotechSchedulerService scheduleService;

    private boolean pollingDisabled;
    private boolean intervalPolling;

    private int hour;
    private int minute;

    private int interval;
    private int intervalUnit;

    @Autowired
    public PollingConfiguration(MotechSchedulerService scheduleService,
            @Value("${polling.enabled}") String pollingEnabled, @Value("${polling.type}") String pollingType,
            @Value("${polling.start.time}") String pollingStartTime,
            @Value("${polling.interval}") String pollingInterval) {

        if ("true".equals(pollingEnabled)) {
            configurePolling(pollingType, pollingStartTime, pollingInterval);
        } else {
            pollingDisabled = true;
        }

        this.scheduleService = scheduleService;
    }

    private void configurePolling(String pollingType, String pollingStartTime, String pollingInterval) {
        if (!ObjectUtils.equals("daily", pollingType) && !ObjectUtils.equals("interval", pollingType)) {
            throw new MotechException("Motech OpenMRS Atom Feed polling type can only be: interval or daily");
        }

        parseRequiredConfigString(pollingStartTime);

        if ("interval".equals(pollingType)) {
            intervalPolling = true;
            parseIntervalConfigString(pollingInterval);
        }
    }

    private void parseRequiredConfigString(String time) {
        String[] hourMinutes = time.split(":");
        if (hourMinutes.length != 2) {
            throw new MotechException(
                    "The configuration for daily time is not configured correctly, it should be formatted as ##:## (e.g. 10:00)");
        }

        try {
            hour = Integer.parseInt(hourMinutes[0]);
        } catch (NumberFormatException e) {
            throw new MotechException("The hour value for daily polling configuration must be a number");
        }

        if (hour < 0 || hour > LAST_HOUR_IN_DAY) {
            throw new MotechException("The hour value for the daily polling configuration must be between 0 and 23");
        }

        try {
            minute = Integer.parseInt(hourMinutes[1]);
        } catch (NumberFormatException e) {
            throw new MotechException("The minute value for daily polling configuration must be a number");
        }

        if (minute < 0 || minute > LAST_MINUTE_IN_HOUR) {
            throw new MotechException("The minute value for the daily polling configuration must be between 0 and 59");
        }
    }

    public void schedulePolling() {
        if (pollingDisabled) {
            return;
        }

        MotechEvent event = new MotechEvent(EventSubjects.POLLING_SUBJECT);
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, POLLING_JOB_ID);
        LocalTime time = new LocalTime(hour, minute);
        Date realTime = time.toDateTimeToday().toDate();

        if (!intervalPolling) {
            RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, realTime, null, MILLISECONDS_IN_DAY, false);
            scheduleService.safeScheduleRepeatingJob(job);
        } else {
            RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, realTime, null, (MILLISECONDS_IN_MINUTE
                    * intervalUnit * interval), false);
            scheduleService.safeScheduleRepeatingJob(job);
        }
    }

    public void unschedulePolling() {
        scheduleService.safeUnscheduleRepeatingJob(EventSubjects.POLLING_SUBJECT, POLLING_JOB_ID);
    }

    private void parseIntervalConfigString(String pollingInterval) {
        if (pollingInterval.endsWith("m")) {
            // minutes
            intervalUnit = 1;
            try {
                interval = Integer.parseInt(pollingInterval.substring(0, pollingInterval.length() - 1));
            } catch (NumberFormatException e) {
                throw new MotechException(
                        "The interval value for the interval polling configuration must be a number, e.g. 1m");
            }
        } else if (pollingInterval.endsWith("h")) {
            // hours
            intervalUnit = 60;
            try {
                interval = Integer.parseInt(pollingInterval.substring(0, pollingInterval.length() - 1));
            } catch (NumberFormatException e) {
                throw new MotechException(
                        "The interval value for the interval polling configuration must be a number, e.g 1h");
            }
        } else {
            throw new MotechException(
                    "The interval value time unit for the interval configuration must be either: m or h");
        }
    }
}
