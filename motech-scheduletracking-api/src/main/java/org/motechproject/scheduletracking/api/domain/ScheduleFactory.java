package org.motechproject.scheduletracking.api.domain;

import org.motechproject.scheduletracking.api.domain.userspecified.AlertRecord;
import org.motechproject.scheduletracking.api.domain.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.domain.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.domain.userspecified.ScheduleWindowsRecord;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleFactory {

	public static Schedule create(ScheduleRecord scheduleRecord) {
        List<MilestoneRecord> milestones = scheduleRecord.milestoneRecords();

        Milestone lastMilestone = null;
        int index = milestones.size() - 1;
        do {
            MilestoneRecord milestoneRecord = milestones.get(index--);
            ScheduleWindowsRecord windowsRecord = milestoneRecord.scheduleWindowsRecord();
            Milestone milestone = new Milestone(milestoneRecord.name(), wallTime(windowsRecord.earliest()), wallTime(windowsRecord.due()), wallTime(windowsRecord.late()), wallTime(windowsRecord.max()));
            milestone.setData(milestoneRecord.data());
            for (AlertRecord alertRecord : milestoneRecord.alerts()) {
                MilestoneWindow milestoneWindow = milestone.getMilestoneWindow(WindowName.valueOf(alertRecord.window()));
                milestoneWindow.addAlerts(new Alert(WallTimeFactory.create(alertRecord.startOffset()), WallTimeFactory.create(alertRecord.interval()), Integer.parseInt(alertRecord.count())));
            }
            lastMilestone = milestone;
        } while (index >= 0);

        return new Schedule(scheduleRecord.name());
    }

    public Schedule build(ScheduleRecord scheduleRecord) {
        Schedule schedule = new Schedule(scheduleRecord.name());
        for (MilestoneRecord milestoneRecord : scheduleRecord.milestoneRecords()) {
            ScheduleWindowsRecord windowsRecord = milestoneRecord.scheduleWindowsRecord();
            Milestone milestone = new Milestone(milestoneRecord.name(), wallTime(windowsRecord.earliest()), wallTime(windowsRecord.due()), wallTime(windowsRecord.late()), wallTime(windowsRecord.max()));
            milestone.setData(milestoneRecord.data());
            for (AlertRecord alertRecord : milestoneRecord.alerts()) {
                MilestoneWindow milestoneWindow = milestone.getMilestoneWindow(WindowName.valueOf(alertRecord.window()));
                milestoneWindow.addAlerts(new Alert(WallTimeFactory.create(alertRecord.startOffset()), WallTimeFactory.create(alertRecord.interval()), Integer.parseInt(alertRecord.count())));
            }
            schedule.addMilestones(milestone);
        }
        return schedule;
    }

    private static WallTime wallTime(String userDefinedTime) {
        return WallTimeFactory.create(userDefinedTime);
    }
}
