package org.motechproject.scheduletracking.api.domain;

import org.motechproject.scheduletracking.api.domain.exception.InvalidScheduleDefinitionException;
import org.motechproject.scheduletracking.api.domain.json.AlertRecord;
import org.motechproject.scheduletracking.api.domain.json.MilestoneRecord;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.domain.json.ScheduleWindowsRecord;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;
import org.springframework.stereotype.Component;

@Component
public class ScheduleFactory {

    public Schedule build(ScheduleRecord scheduleRecord) {
        Schedule schedule = new Schedule(scheduleRecord.name());
        int alertIndex = 0;
        for (MilestoneRecord milestoneRecord : scheduleRecord.milestoneRecords()) {
            ScheduleWindowsRecord windowsRecord = milestoneRecord.scheduleWindowsRecord();
            WallTime earliest = WallTimeFactory.wallTime(windowsRecord.earliest());
            WallTime due = WallTimeFactory.wallTime(windowsRecord.due());
            WallTime late = WallTimeFactory.wallTime(windowsRecord.late());
            WallTime max = WallTimeFactory.wallTime(windowsRecord.max());
            if (earliest == null)
                earliest = new WallTime(0, null);
            if (due == null)
                due = earliest;
            if (late == null)
                late = due;
            if (max == null)
                max = late;
            Milestone milestone = new Milestone(milestoneRecord.name(), earliest, due, late, max);
            milestone.setData(milestoneRecord.data());
            for (AlertRecord alertRecord : milestoneRecord.alerts()) {
                String offset = alertRecord.offset();
                if (offset == null || offset.isEmpty())
                    throw new InvalidScheduleDefinitionException("alert needs an offset parameter.");
                milestone.addAlert(WindowName.valueOf(alertRecord.window()), new Alert(WallTimeFactory.wallTime(offset), WallTimeFactory.wallTime(alertRecord.interval()), Integer.parseInt(alertRecord.count()), alertIndex++));
            }
            schedule.addMilestones(milestone);
        }
        return schedule;
    }
}
