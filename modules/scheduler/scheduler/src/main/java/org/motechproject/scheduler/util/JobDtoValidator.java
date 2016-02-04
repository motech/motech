package org.motechproject.scheduler.util;

import org.motechproject.scheduler.contract.JobDto;

import static org.apache.commons.lang.Validate.notNull;
import static org.motechproject.scheduler.constants.SchedulerConstants.CRON_EXPRESSION;
import static org.motechproject.scheduler.constants.SchedulerConstants.DAYS;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEAT_COUNT;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEAT_INTERVAL_IN_SECONDS;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEAT_PERIOD;
import static org.motechproject.scheduler.constants.SchedulerConstants.TIME;

public final class JobDtoValidator {

    private static final String JOB_MISSING_PARAMETERS = "Job is missing parameters";

    public static void validateJob(JobDto dto) {
        notNull(dto.getType(), "Job type cannot be null!");
        notNull(dto.getMotechEventSubject(), "Motech event subject cannot be null!");
        notNull(dto.getStartDate(), "Job start date cannot be null!");
    }

    public static void validateForCronJob(JobDto dto) {
        notNull(dto.getParameters(), JOB_MISSING_PARAMETERS);
        notNull(dto.getParameters().get(CRON_EXPRESSION), "Cron expressions cannot be null!");
    }

    public static void validateForRepeatingJob(JobDto dto) {
        notNull(dto.getParameters(), JOB_MISSING_PARAMETERS);
        notNull(dto.getParameters().get(REPEAT_COUNT), "Repeat count cannot be null!");
        notNull(dto.getParameters().get(REPEAT_INTERVAL_IN_SECONDS), "Repeat interval cannot be null!");
    }

    public static void validateForRepeatingPeriodJob(JobDto dto) {
        notNull(dto.getParameters(), JOB_MISSING_PARAMETERS);
        notNull(dto.getParameters().get(REPEAT_PERIOD), "Repeat period cannot be null!");
    }

    public static void validateForDayOfWeekJob(JobDto dto) {
        notNull(dto.getParameters(), JOB_MISSING_PARAMETERS);
        notNull(dto.getParameters().get(DAYS), "Days cannot be null!");
        notNull(dto.getParameters().get(TIME), "Time cannot be null!");
    }

    private JobDtoValidator() {
    }

}
