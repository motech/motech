package org.motechproject.scheduletracking.api.domain;

import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.PeriodFormatterBuilder;
import org.joda.time.format.PeriodParser;
import org.motechproject.scheduletracking.api.domain.json.AlertRecord;
import org.motechproject.scheduletracking.api.domain.json.MilestoneRecord;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.domain.json.ScheduleWindowsRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleFactory {

    public static final Period EMPTY_PERIOD = new Period(0);
    private List<PeriodParser> parsers;

    public ScheduleFactory() {
        parsers = new ArrayList<PeriodParser>();
        parsers.add(new PeriodFormatterBuilder().appendYears().appendSuffix(" year", " years").toParser());
        parsers.add(new PeriodFormatterBuilder().appendMonths().appendSuffix(" month", " months").toParser());
        parsers.add(new PeriodFormatterBuilder().appendWeeks().appendSuffix(" week", " weeks").toParser());
        parsers.add(new PeriodFormatterBuilder().appendDays().appendSuffix(" day", " days").toParser());
        parsers.add(new PeriodFormatterBuilder().appendHours().appendSuffix(" hour", " hours").toParser());
        parsers.add(new PeriodFormatterBuilder().appendMinutes().appendSuffix(" minute", " minutes").toParser());
    }

    public Schedule build(ScheduleRecord scheduleRecord) {
        Schedule schedule = new Schedule(scheduleRecord.name());
        schedule.isBasedOnAbsoluteWindows(scheduleRecord.isAbsoluteSchedule());
        int alertIndex = 0;
        Period previousWindowEnd = new Period();

        for (MilestoneRecord milestoneRecord : scheduleRecord.milestoneRecords()) {
            Map<WindowName, List<String>> values = getWindowValues(milestoneRecord.scheduleWindowsRecord());

            Map<WindowName, Period> windowDurations = new HashMap<WindowName, Period>();
            Map<WindowName, Period> windowStarts = new HashMap<WindowName, Period>();
            for (WindowName windowName : WindowName.values()) {
                Period currentWindowEnd = getPeriodFromValue(values.get(windowName));
                windowStarts.put(windowName, previousWindowEnd);
                if (currentWindowEnd.equals(EMPTY_PERIOD))
                    windowDurations.put(windowName, currentWindowEnd);
                else {
                    windowDurations.put(windowName, currentWindowEnd.minus(previousWindowEnd));
                    previousWindowEnd = currentWindowEnd;
                }
            }
            if (!scheduleRecord.isAbsoluteSchedule())
                previousWindowEnd = EMPTY_PERIOD;

            Milestone milestone = new Milestone(milestoneRecord.name(), windowDurations.get(WindowName.earliest), windowDurations.get(WindowName.due), windowDurations.get(WindowName.late), windowDurations.get(WindowName.max));
            milestone.setData(milestoneRecord.data());
            addAlertsToMilestone(milestone, milestoneRecord.alerts(), windowStarts, scheduleRecord.isAbsoluteSchedule(), alertIndex);
            schedule.addMilestones(milestone);

            alertIndex += milestoneRecord.alerts().size();
        }
        return schedule;
    }

    private void addAlertsToMilestone(Milestone milestone, List<AlertRecord> alerts, Map<WindowName, Period> windowStarts, boolean isAbsoluteAlert, int alertIndex) {
        for (AlertRecord alertRecord : alerts) {
            Period offset = getPeriodFromValue(alertRecord.offset());
            if (isAbsoluteAlert)
                offset = offset.minus(windowStarts.get(WindowName.valueOf(alertRecord.window())));
            milestone.addAlert(WindowName.valueOf(alertRecord.window()), new Alert(offset, getPeriodFromValue(alertRecord.interval()), Integer.parseInt(alertRecord.count()), alertIndex++));
        }
    }

    private Map<WindowName, List<String>> getWindowValues(ScheduleWindowsRecord windowsRecord) {
        Map<WindowName, List<String>> values = new HashMap<WindowName, List<String>>();
        values.put(WindowName.earliest, windowsRecord.earliest());
        values.put(WindowName.due, windowsRecord.due());
        values.put(WindowName.late, windowsRecord.late());
        values.put(WindowName.max, windowsRecord.max());
        return values;
    }

    private Period getPeriodFromValue(List<String> readableValues) {
        ReadWritablePeriod period = new MutablePeriod();
        for (String s : readableValues)
            period.add(parse(s));
        return period.toPeriod();
    }

    private Period parse(String s) {
        ReadWritablePeriod period = new MutablePeriod();
        for (PeriodParser parser : parsers) {
            if (parser.parseInto(period, s, 0, null) > 0) {
                return period.toPeriod();
            }
        }
        return period.toPeriod();
    }
}
