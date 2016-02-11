package org.motechproject.scheduler.validation;

import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.contract.SchedulableJob;

import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

/**
 * Utility class for validating if a job contains all required fields.
 */
public final class SchedulableJobValidator {

    private static final String JOB_CANNOT_BE_NULL = "Job cannot be null!";
    private static final String MOTECH_EVENT_CANNOT_BE_NULL = "Motech event cannot be null!";

    /**
     * Validates if the given {@code job} has cron expression, motech event and start date fields set.
     *
     * @param job  the job to be validated
     */
    public static void validateCronSchedulableJob(CronSchedulableJob job) {
        notNull(job, JOB_CANNOT_BE_NULL);
        notEmpty(job.getCronExpression(), "Cron expression cannot be null or empty!");
        notNull(job.getMotechEvent(), MOTECH_EVENT_CANNOT_BE_NULL);
    }

    /**
     * Validates if the given {@code job} has repeat count, repeat interval, motech event and start date fields set.
     *
     * @param job  the job to be validated
     */
    public static void validateRepeatingSchedulableJob(RepeatingSchedulableJob job) {
        notNull(job, JOB_CANNOT_BE_NULL);
        notNull(job.getRepeatIntervalInSeconds(), "Repeat interval cannot be null!");
        if (job.getRepeatIntervalInSeconds() == 0) {
            throw new IllegalArgumentException("Invalid RepeatingSchedulableJob. The job repeat interval in seconds can not be 0 ");
        }
        validateSchedulableJob(job);
    }

    /**
     * Validates if the given {@code job} has repeat period, motech event and start date fields set.
     *
     * @param job  the job to be validated
     */
    public static void validateRepeatingPeriodSchedulableJob(RepeatingPeriodSchedulableJob job) {
        notNull(job, JOB_CANNOT_BE_NULL);
        notNull(job.getRepeatPeriod(), "Repeat period cannot be null!");
        validateSchedulableJob(job);
    }

    /**
     * Validates if the given {@code job} has motech event and start date fields set.
     *
     * @param job  the job to be validated
     */
    public static void validateRunOnceSchedulableJob(RunOnceSchedulableJob job) {
        notNull(job, JOB_CANNOT_BE_NULL);
        validateSchedulableJob(job);

        DateTime startDate = job.getStartDate();
        DateTime currentDate = DateUtil.now();
        if (job.getStartDate().isBefore(currentDate)) {
            String errorMessage = "Invalid RunOnceSchedulableJob. The job start date can not be in the past. \n" +
                    " Job start date: " + startDate.toString() +
                    " Attempted to schedule at:" + currentDate.toString();
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Validates if the given {@code job} has days, time, motech event and start date fields set.
     *
     * @param job  the job to be validated
     */
    public static void validateDayOfWeekSchedulableJob(DayOfWeekSchedulableJob job) {
        notNull(job, JOB_CANNOT_BE_NULL);
        notNull(job.getDays(), "List of days of week cannot be null!");
        notNull(job.getTime(), "Time cannot be null!");
        validateSchedulableJob(job);
    }

    private static void validateSchedulableJob(SchedulableJob job) {
        notNull(job.getMotechEvent(), MOTECH_EVENT_CANNOT_BE_NULL);
        notNull(job.getStartDate(), "Start date cannot be null!");
    }

    /**
     * Utility class should not be initiated.
     */
    private SchedulableJobValidator() {
    }
}
