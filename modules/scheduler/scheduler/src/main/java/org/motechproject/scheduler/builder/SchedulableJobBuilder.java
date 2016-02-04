package org.motechproject.scheduler.builder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.contract.JobDto;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.motechproject.scheduler.constants.SchedulerConstants.CRON_EXPRESSION;
import static org.motechproject.scheduler.constants.SchedulerConstants.DAYS;
import static org.motechproject.scheduler.constants.SchedulerConstants.END_DATE;
import static org.motechproject.scheduler.constants.SchedulerConstants.IGNORE_PAST_FIRES_AT_START;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEAT_COUNT;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEAT_INTERVAL_IN_SECONDS;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEAT_PERIOD;
import static org.motechproject.scheduler.constants.SchedulerConstants.TIME;
import static org.motechproject.scheduler.constants.SchedulerConstants.USE_ORIGINAL_FIRE_TIME_AFTER_MISFIRE;
import static org.motechproject.scheduler.util.CustomDateParser.parseToDateTime;
import static org.motechproject.scheduler.util.CustomDateParser.parseToLocalDate;

public final class SchedulableJobBuilder {

    public static CronSchedulableJob buildCronSchedulableJob(JobDto dto) throws ParseException {
        Map<String, String> parameters = dto.getParameters();

        MotechEvent event = new MotechEvent(dto.getMotechEventSubject(), dto.getMotechEventParameters());
        String cronExpression = parameters.get(CRON_EXPRESSION);
        DateTime startDate = dto.getStartDate();
        DateTime endDate = parseToDateTime(parameters.get(END_DATE));
        boolean ignorePastFiresAtStart = Boolean.parseBoolean(parameters.get(IGNORE_PAST_FIRES_AT_START));

        return new CronSchedulableJob(event, cronExpression, startDate, endDate, ignorePastFiresAtStart, true);
    }

    public static RepeatingSchedulableJob buildRepeatingSchedulableJob(JobDto dto) throws ParseException {
        Map<String, String> parameters = dto.getParameters();

        MotechEvent event = new MotechEvent(dto.getMotechEventSubject(), dto.getMotechEventParameters());
        Integer repeatCount = Integer.parseInt(parameters.get(REPEAT_COUNT));
        Integer repeatInterval = Integer.parseInt(parameters.get(REPEAT_INTERVAL_IN_SECONDS));
        DateTime startDate = dto.getStartDate();
        DateTime endDate = parseToDateTime(parameters.get(END_DATE));
        boolean ignorePastFiresAtStart = Boolean.parseBoolean(parameters.get(IGNORE_PAST_FIRES_AT_START));

        boolean useOriginalFireTimeAfterMisfire = Boolean.parseBoolean(
                parameters.get(USE_ORIGINAL_FIRE_TIME_AFTER_MISFIRE)
        );

        return new RepeatingSchedulableJob(event, repeatCount, repeatInterval, startDate, endDate,
                ignorePastFiresAtStart, useOriginalFireTimeAfterMisfire, true);
    }

    public static RunOnceSchedulableJob buildRunOnceSchedulableJob(JobDto dto) throws ParseException {
        MotechEvent event = new MotechEvent(dto.getMotechEventSubject(), dto.getMotechEventParameters());
        DateTime startDate = dto.getStartDate();

        return new RunOnceSchedulableJob(event, startDate, true);
    }

    public static RepeatingPeriodSchedulableJob buildRepeatingPeriodSchedulableJob(JobDto dto) throws ParseException {
        Map<String, String> parameters = dto.getParameters();

        MotechEvent event = new MotechEvent(dto.getMotechEventSubject(), dto.getMotechEventParameters());
        DateTime startDate = dto.getStartDate();
        DateTime endDate = parseToDateTime(parameters.get(END_DATE));
        Period period = Period.parse(parameters.get(REPEAT_PERIOD));
        boolean ignorePastFiresAtStart = Boolean.parseBoolean(parameters.get(IGNORE_PAST_FIRES_AT_START));

        boolean useOriginalFireTimeAfterMisfire = Boolean.parseBoolean(
                parameters.get(USE_ORIGINAL_FIRE_TIME_AFTER_MISFIRE)
        );

        return new RepeatingPeriodSchedulableJob(event, startDate, endDate, period, ignorePastFiresAtStart,
                useOriginalFireTimeAfterMisfire, true);
    }

    public static DayOfWeekSchedulableJob buildDayOfWeekJob(JobDto dto) throws ParseException {
        Map<String, String> parameters = dto.getParameters();

        MotechEvent event = new MotechEvent(dto.getMotechEventSubject(), dto.getMotechEventParameters());
        LocalDate startDate = new LocalDate(dto.getStartDate());
        LocalDate endDate = parseToLocalDate(parameters.get(END_DATE));
        List<DayOfWeek> days = parseDays(parameters.get(DAYS));
        Time time = Time.parseTime(parameters.get(TIME), ":");
        boolean ignorePastFiresAtStart = Boolean.parseBoolean(parameters.get(IGNORE_PAST_FIRES_AT_START));

        return new DayOfWeekSchedulableJob(event, startDate, endDate, days, time, ignorePastFiresAtStart, true);
    }

    private static List<DayOfWeek> parseDays(String daysAsString) {
        List<DayOfWeek> days = new ArrayList<>();
        for (String day : daysAsString.split(",")) {
            days.add(DayOfWeek.getDayOfWeek(Integer.valueOf(day)));
        }
        return days;
    }

    private SchedulableJobBuilder() {

    }
}
