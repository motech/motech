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
        schedule.isBasedOnAbsoluteWindows(scheduleRecord.hasAbsoluteWindows());
        int alertIndex = 0;
        Period previousWindow = new Period();

        for (MilestoneRecord milestoneRecord : scheduleRecord.milestoneRecords()) {
            ScheduleWindowsRecord windowsRecord = milestoneRecord.scheduleWindowsRecord();
            Map<WindowName, List<String>> values = new HashMap<WindowName, List<String>>();
            values.put(WindowName.earliest, windowsRecord.earliest());
            values.put(WindowName.due, windowsRecord.due());
            values.put(WindowName.late, windowsRecord.late());
            values.put(WindowName.max, windowsRecord.max());

            Map<WindowName, Period> periods = new HashMap<WindowName, Period>();
            for (WindowName windowName : WindowName.values()) {
                Period currentWindow = getWindowPeriod(values.get(windowName));
                if (currentWindow.equals(EMPTY_PERIOD))
                    periods.put(windowName, currentWindow);
                else {
                    periods.put(windowName, currentWindow.minus(previousWindow));
                    previousWindow = currentWindow;
                }
            }
            Milestone milestone = new Milestone(milestoneRecord.name(), periods.get(WindowName.earliest), periods.get(WindowName.due), periods.get(WindowName.late), periods.get(WindowName.max));
            milestone.setData(milestoneRecord.data());
            alertIndex = setAlerts(alertIndex, milestone, milestoneRecord.alerts());
            schedule.addMilestones(milestone);
            if(!scheduleRecord.hasAbsoluteWindows())
                previousWindow = EMPTY_PERIOD;
        }
        return schedule;
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

    private int setAlerts(int alertIndex, Milestone milestone, List<AlertRecord> alertRecords) {
        for (AlertRecord alertRecord : alertRecords) {
            List<String> offset = alertRecord.offset();
            milestone.addAlert(WindowName.valueOf(alertRecord.window()), new Alert(getWindowPeriod(offset), getWindowPeriod(alertRecord.interval()), Integer.parseInt(alertRecord.count()), alertIndex++));
        }
        return alertIndex;
    }
}
