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
import java.util.List;

@Component
public class ScheduleFactory {

    private List<PeriodParser> parsers;

    public ScheduleFactory() {
        initializePeriodParsers();
    }

    public Schedule build(ScheduleRecord scheduleRecord) {
        Schedule schedule = new Schedule(scheduleRecord.name());
        int alertIndex = 0;
        for (MilestoneRecord milestoneRecord : scheduleRecord.milestoneRecords()) {
            ScheduleWindowsRecord windowsRecord = milestoneRecord.scheduleWindowsRecord();

            List<String> earliestValue = windowsRecord.earliest();
            List<String> dueValue = windowsRecord.due();
            List<String> lateValue = windowsRecord.late();
            List<String> maxValue = windowsRecord.max();

            Period earliest = new Period(), due = new Period(), late = new Period(), max = new Period();
            if (isWindowNotEmpty(earliestValue))
                earliest = getWindowPeriod(earliestValue);
            if (isWindowNotEmpty(dueValue))
                due = getWindowPeriod(dueValue).minus(earliest);
            if (isWindowNotEmpty(lateValue))
                late = getWindowPeriod(lateValue).minus(earliest.plus(due));
            if (isWindowNotEmpty(maxValue))
                max = getWindowPeriod(maxValue).minus(earliest.plus(due).plus(late));

            Milestone milestone = new Milestone(milestoneRecord.name(), earliest, due, late, max);
            milestone.setData(milestoneRecord.data());
            for (AlertRecord alertRecord : milestoneRecord.alerts()) {
                List<String> offset = alertRecord.offset();
                milestone.addAlert(WindowName.valueOf(alertRecord.window()), new Alert(getWindowPeriod(offset), getWindowPeriod(alertRecord.interval()), Integer.parseInt(alertRecord.count()), alertIndex++));
            }
            schedule.addMilestones(milestone);
        }
        return schedule;
    }

    private boolean isWindowNotEmpty(List<String> windowValue) {
        return !getWindowPeriod(windowValue).equals(new Period());
    }

    private void initializePeriodParsers() {
        parsers = new ArrayList<PeriodParser>();

        parsers.add(new PeriodFormatterBuilder()
                .appendYears()
                .appendSuffix(" year", " years")
                .toParser());
        parsers.add(new PeriodFormatterBuilder()
                .appendMonths()
                .appendSuffix(" month", " months")
                .toParser());
        parsers.add(new PeriodFormatterBuilder()
                .appendWeeks()
                .appendSuffix(" week", " weeks")
                .toParser());
        parsers.add(new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(" day", " days")
                .toParser());
        parsers.add(new PeriodFormatterBuilder()
                .appendHours()
                .appendSuffix(" hour", " hours")
                .toParser());
    }

    private Period getWindowPeriod(List<String> readableValues) {
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
