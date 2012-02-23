package org.motechproject.scheduletracking.api.domain;

import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.PeriodFormatterBuilder;
import org.joda.time.format.PeriodParser;
import org.motechproject.scheduletracking.api.domain.exception.InvalidScheduleDefinitionException;
import org.motechproject.scheduletracking.api.domain.json.AlertRecord;
import org.motechproject.scheduletracking.api.domain.json.MilestoneRecord;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.domain.json.ScheduleWindowsRecord;
import org.springframework.stereotype.Component;

@Component
public class ScheduleFactory {

    PeriodParser dayParser = new PeriodFormatterBuilder()
        .appendDays()
        .appendSuffix(" day", " days")
        .toParser();
    PeriodParser weekParser = new PeriodFormatterBuilder()
        .appendWeeks()
        .appendSuffix(" week", " weeks")
        .toParser();

    public Schedule build(ScheduleRecord scheduleRecord) {
        Schedule schedule = new Schedule(scheduleRecord.name());
        int alertIndex = 0;
        for (MilestoneRecord milestoneRecord : scheduleRecord.milestoneRecords()) {
            ScheduleWindowsRecord windowsRecord = milestoneRecord.scheduleWindowsRecord();
            String earliestValue = windowsRecord.earliest();
            String dueValue = windowsRecord.due();
            if (dueValue.isEmpty())
                dueValue = earliestValue;
            String lateValue = windowsRecord.late();
            if (lateValue.isEmpty())
                lateValue = dueValue;
            String maxValue = windowsRecord.max();
            if (maxValue.isEmpty())
                maxValue = lateValue;

            Period earliest = parse(earliestValue);
            Period due = parse(dueValue).minus(earliest);
            Period late = parse(lateValue).minus(earliest.plus(due));
            Period max = parse(maxValue).minus(earliest.plus(due).plus(late));

            Milestone milestone = new Milestone(milestoneRecord.name(), earliest, due, late, max);
            milestone.setData(milestoneRecord.data());
            for (AlertRecord alertRecord : milestoneRecord.alerts()) {
                String offset = alertRecord.offset();
                if (offset == null || offset.isEmpty())
                    throw new InvalidScheduleDefinitionException("alert needs an offset parameter.");
                milestone.addAlert(WindowName.valueOf(alertRecord.window()), new Alert(parse(offset), parse(alertRecord.interval()), Integer.parseInt(alertRecord.count()), alertIndex++));
            }
            schedule.addMilestones(milestone);
        }
        return schedule;
    }

    private Period parse(String s) {
        ReadWritablePeriod period = new MutablePeriod();
        if (dayParser.parseInto(period, s, 0, null) > 0)
            return period.toPeriod();
        if (weekParser.parseInto(period, s, 0, null) > 0)
            return period.toPeriod();
        return period.toPeriod();
    }
}
