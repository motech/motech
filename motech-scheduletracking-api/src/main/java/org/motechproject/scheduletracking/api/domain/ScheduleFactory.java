package org.motechproject.scheduletracking.api.domain;

import org.motechproject.scheduletracking.api.domain.userspecified.AlertRecord;
import org.motechproject.scheduletracking.api.domain.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.domain.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.domain.userspecified.ScheduleWindowsRecord;
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
            WallTime earliest = WallTimeFactory.create(windowsRecord.earliest());
            WallTime due = WallTimeFactory.create(windowsRecord.due());
            WallTime late = WallTimeFactory.create(windowsRecord.late());
            WallTime max = WallTimeFactory.create(windowsRecord.max());
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
            for (AlertRecord alertRecord : milestoneRecord.alerts())
                milestone.addAlert(WindowName.valueOf(alertRecord.window()), new Alert(WallTimeFactory.create(alertRecord.offset()), WallTimeFactory.create(alertRecord.interval()), Integer.parseInt(alertRecord.count()), alertIndex++));
            schedule.addMilestones(milestone);
        }
        return schedule;
    }
}
