package org.motechproject.openmrs.atomfeed;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Properties;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.MotechException;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;

public class PollingConfigurationTest {
    private static final Long MILLISECONDS_IN_MINUTE = 1000 * 60L;
    private static final Long MILLISECONDS_IN_HOUR = MILLISECONDS_IN_MINUTE * 60;
    private static final Long MILLISECONDS_IN_DAY = MILLISECONDS_IN_HOUR * 24;

    private static final int TIME_MINUTE = 0;
    private static final int TIME_HOUR = 10;

    @Mock
    private MotechSchedulerService scheduleService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test(expected = MotechException.class)
    public void showThrowExceptionOnBadPollingTypeConfiguration() {
        new PollingConfiguration(null, getProperties("bad", "", ""));
    }

    private Properties getProperties(String type, String time, String interval) {
        Properties props = new Properties();
        props.put(PollingConfiguration.POLLING_ENABLED_PROP_KEY, "true");
        props.put(PollingConfiguration.POLLING_TYPE_PROP_KEY, type);
        props.put(PollingConfiguration.POLLING_START_TIME_PROP_KEY, time);
        props.put(PollingConfiguration.POLLING_INTERVAL_VALUE_PROP_KEY, interval);
        return props;
    }

    @Test
    public void shouldConfigureDailyPolling() {
        PollingConfiguration pollConfig = new PollingConfiguration(scheduleService, getProperties("daily", "10:00", ""));
        pollConfig.schedulePolling();

        RepeatingSchedulableJob expected = createExceptedRepeatSchedulableJob();
        ArgumentCaptor<RepeatingSchedulableJob> repeatingJob = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(scheduleService).safeScheduleRepeatingJob(repeatingJob.capture());

        assertEquals(expected, repeatingJob.getValue());
    }

    private RepeatingSchedulableJob createExceptedRepeatSchedulableJob() {
        MotechEvent event = buildMotechEvent();
        LocalTime startTime = new LocalTime(TIME_HOUR, TIME_MINUTE);
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startTime.toDateTimeToday().toDate(), null,
                MILLISECONDS_IN_DAY);
        return job;
    }

    private MotechEvent buildMotechEvent() {
        MotechEvent event = new MotechEvent(EventSubjects.POLLING_SUBJECT);
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, PollingConfiguration.POLLING_JOB_ID);
        return event;
    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionOnBadHour() {
        new PollingConfiguration(scheduleService, getProperties("daily", "AA:00", ""));
    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionOnHourTooHigh() {
        new PollingConfiguration(scheduleService, getProperties("daily", "24:00", ""));
    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionOnHourTooLow() {
        new PollingConfiguration(scheduleService, getProperties("daily", "-1:00", ""));
    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionOnBadMinute() {
        new PollingConfiguration(scheduleService, getProperties("daily", "10:AA", ""));
    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionOnMinuteTooHigh() {
        new PollingConfiguration(scheduleService, getProperties("daily", "10:60", ""));
    }

    @Test(expected = MotechException.class)
    public void shouldThrowExceptionOnMissingParams() {
        new PollingConfiguration(scheduleService, getProperties("daily", "BAD", ""));
    }

    @Test
    public void shouldConfigureIntervalWithMinuteUnit() {
        PollingConfiguration pollConfig = new PollingConfiguration(scheduleService, getProperties("interval", "09:00",
                "1m"));
        pollConfig.schedulePolling();

        RepeatingSchedulableJob expected = createExceptedIntervalRepeatSchedulableJob();
        ArgumentCaptor<RepeatingSchedulableJob> repeatingJob = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(scheduleService).safeScheduleRepeatingJob(repeatingJob.capture());

        assertEquals(expected, repeatingJob.getValue());
    }

    private RepeatingSchedulableJob createExceptedIntervalRepeatSchedulableJob() {
        MotechEvent event = buildMotechEvent();
        LocalTime time = new LocalTime(9, 0);
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, time.toDateTimeToday().toDate(), null,
                MILLISECONDS_IN_MINUTE);
        return job;
    }

    @Test
    public void shouldConfigureIntervalWithHourUnit() {
        PollingConfiguration pollConfig = new PollingConfiguration(scheduleService, getProperties("interval", "09:00",
                "1h"));
        pollConfig.schedulePolling();

        RepeatingSchedulableJob expected = createExceptedIntervalHourRepeatSchedulableJob();
        ArgumentCaptor<RepeatingSchedulableJob> repeatingJob = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(scheduleService).safeScheduleRepeatingJob(repeatingJob.capture());

        assertEquals(expected, repeatingJob.getValue());
    }

    private RepeatingSchedulableJob createExceptedIntervalHourRepeatSchedulableJob() {
        MotechEvent event = buildMotechEvent();
        LocalTime time = new LocalTime(9, 0);
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, time.toDateTimeToday().toDate(), null,
                MILLISECONDS_IN_HOUR);
        return job;
    }

    @Test(expected = MotechException.class)
    public void testUnknownIntervalUnit() {
        new PollingConfiguration(scheduleService, getProperties("interval", "09:00", "1d"));
    }

    @Test(expected = MotechException.class)
    public void testBadMinuteValue() {
        new PollingConfiguration(scheduleService, getProperties("interval", "09:00", "Am"));
    }

    @Test(expected = MotechException.class)
    public void testMissingMinuteValue() {
        new PollingConfiguration(scheduleService, getProperties("interval", "09:00", "m"));
    }

    @Test(expected = MotechException.class)
    public void testBadHourValue() {
        new PollingConfiguration(scheduleService, getProperties("interval", "09:00", "Ah"));
    }

    @Test(expected = MotechException.class)
    public void testMissingHourValue() {
        new PollingConfiguration(scheduleService, getProperties("interval", "09:00", "h"));
    }

    @Test
    public void shouldUnscheduleJob() {
        PollingConfiguration pollConfig = new PollingConfiguration(scheduleService, getProperties("interval", "09:00",
                "1h"));
        pollConfig.unschedulePolling();

        verify(scheduleService).safeUnscheduleRepeatingJob(EventSubjects.POLLING_SUBJECT,
                PollingConfiguration.POLLING_JOB_ID);
    }

    @Test
    public void shouldNotScheduleIfNotEnabled() {
        Properties props = new Properties();
        props.put(PollingConfiguration.POLLING_ENABLED_PROP_KEY, "false");
        PollingConfiguration pollConfig = new PollingConfiguration(scheduleService, props);

        pollConfig.schedulePolling();

        verifyZeroInteractions(scheduleService);
    }

}
