package org.motechproject.scheduler.validation;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.exception.MotechSchedulerException;

import java.util.ArrayList;
import java.util.HashMap;

import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

public class SchedulableJobValidatorTest {

    private MotechEvent motechEvent;

    @Before
    public void setUp() {
        motechEvent = new MotechEvent("test.subject", new HashMap<>());
    }

    @Test
    public void shouldValidateCronSchedulableJob() {
        CronSchedulableJob job = new CronSchedulableJob(motechEvent, "0 0 0 * * ? *", null, null, true, false);

        SchedulableJobValidator.validateCronSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfCronSchedulableJobIsNull() {
        SchedulableJobValidator.validateRepeatingSchedulableJob(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfMotechEventIsNullInCronSchedulableJob() {
        CronSchedulableJob job = new CronSchedulableJob(null, "0 0 0 * * ? *", DateTime.now().plusHours(1), null, false,
                true);

        SchedulableJobValidator.validateCronSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfCronExpressionIsNull() {
        CronSchedulableJob job = new CronSchedulableJob(motechEvent, null, DateTime.now().plusHours(1), null, false,
                true);

        SchedulableJobValidator.validateCronSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfCronExpressionIsEmpty() {
        CronSchedulableJob job = new CronSchedulableJob(motechEvent, "", DateTime.now().plusHours(1), null, false,
                true);

        SchedulableJobValidator.validateCronSchedulableJob(job);
    }

    @Test
    public void shouldValidateRepeatingSchedulableJob() {
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(motechEvent, 10, 100, DateTime.now().plusHours(1),
                null, true, true, false);

        SchedulableJobValidator.validateRepeatingSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfRepeatingSchedulableJobIsNull() {
        SchedulableJobValidator.validateRepeatingSchedulableJob(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfRepeatIntervalIsNull() {
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(motechEvent, 10, null, DateTime.now().plusHours(1),
                null, true, true, false);

        SchedulableJobValidator.validateRepeatingSchedulableJob(job);
    }

    @Test(expected = MotechSchedulerException.class)
    public void shouldThrowMotechSchedulerExceptionIfRepeatIntervalIsZero() {
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(motechEvent, 10, 0, DateTime.now().plusHours(1),
                null, true, true, false);

        SchedulableJobValidator.validateRepeatingSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfMotechEventIsNullInRepeatingSchedulableJob() {
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(null, 10, 100, DateTime.now().plusHours(1),
                null, true, true, false);

        SchedulableJobValidator.validateRepeatingSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfStartDateIsNullInRepeatingSchedulableJob() {
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(motechEvent, 10, 100, null, null, true, true, false);

        SchedulableJobValidator.validateRepeatingSchedulableJob(job);
    }

    @Test
    public void shouldValidateRepeatingPeriodJob() {
        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(motechEvent, DateTime.now(), null,
                Period.hours(1), false, true, true);

        SchedulableJobValidator.validateRepeatingPeriodSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfRepeatingPeriodJobIsNull() {
        SchedulableJobValidator.validateRepeatingPeriodSchedulableJob(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfRepeatPeriodIsNull() {
        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(motechEvent, DateTime.now(), null, null,
                false, true, true);

        SchedulableJobValidator.validateRepeatingPeriodSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfMotechEventIsNullInRepeatingPeriodJob() {
        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(null, DateTime.now(), null, null,
                false, true, true);

        SchedulableJobValidator.validateRepeatingPeriodSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfStartDateIsNullInRepeatingPeriodJob() {
        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(motechEvent, null, null, null, false,
                true, true);

        SchedulableJobValidator.validateRepeatingPeriodSchedulableJob(job);
    }

    @Test
    public void shouldValidateRunOnceJob() {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));
            RunOnceSchedulableJob job = new RunOnceSchedulableJob(motechEvent, DateUtil.now().plusHours(1), false);

            SchedulableJobValidator.validateRunOnceSchedulableJob(job);
        } finally {
            stopFakingTime();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfMotechEventIsNullInRunOnceSchedulableJob() {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));
            RunOnceSchedulableJob job = new RunOnceSchedulableJob(null, DateUtil.now().plusHours(1), false);

            SchedulableJobValidator.validateRunOnceSchedulableJob(job);
        } finally {
            stopFakingTime();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfStartDatesNullInRunOnceSchedulableJob() {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));
            RunOnceSchedulableJob job = new RunOnceSchedulableJob(motechEvent, null, false);

            SchedulableJobValidator.validateRunOnceSchedulableJob(job);
        } finally {
            stopFakingTime();
        }
    }

    @Test(expected = MotechSchedulerException.class)
    public void shouldThrowMotechSchedulerExceptionIfStartDateIsInThePast() {
        try {
            fakeNow(newDateTime(2020, 7, 15, 10, 0, 0));
            RunOnceSchedulableJob job = new RunOnceSchedulableJob(motechEvent, DateUtil.now().minusHours(1), false);

            SchedulableJobValidator.validateRunOnceSchedulableJob(job);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldValidateDayOfWeekSchedulableJob() {
        DayOfWeekSchedulableJob job = new DayOfWeekSchedulableJob(motechEvent, DateTime.now(), null, new ArrayList<>(),
                Time.valueOf("16:15"), false, true);

        SchedulableJobValidator.validateDayOfWeekSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfMotechEventIsNullInDayOfWeekSchedulableJob() {
        DayOfWeekSchedulableJob job = new DayOfWeekSchedulableJob(null, DateTime.now(), null, new ArrayList<>(),
                Time.valueOf("16:15"), false, true);

        SchedulableJobValidator.validateDayOfWeekSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfStartDateIsNullInDayOfWeekSchedulableJob() {
        DayOfWeekSchedulableJob job = new DayOfWeekSchedulableJob(motechEvent, null, null, new ArrayList<>(),
                Time.valueOf("16:15"), false, true);

        SchedulableJobValidator.validateDayOfWeekSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfDaysAreNull() {
        DayOfWeekSchedulableJob job = new DayOfWeekSchedulableJob(motechEvent, DateTime.now(), null, null,
                Time.valueOf("16:15"), false, true);

        SchedulableJobValidator.validateDayOfWeekSchedulableJob(job);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfTimeIsNull() {
        DayOfWeekSchedulableJob job = new DayOfWeekSchedulableJob(null, DateTime.now(), null, new ArrayList<>(), null,
                false, true);

        SchedulableJobValidator.validateDayOfWeekSchedulableJob(job);
    }

}