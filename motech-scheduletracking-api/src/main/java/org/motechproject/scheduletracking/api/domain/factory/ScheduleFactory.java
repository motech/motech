package org.motechproject.scheduletracking.api.domain.factory;

import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.userspecified.AlertRecord;
import org.motechproject.scheduletracking.api.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleWindowsRecord;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.List;

public class ScheduleFactory {
    public static Schedule create(ScheduleRecord scheduleRecord) {
        Schedule schedule = new Schedule(scheduleRecord.name());
        List<MilestoneRecord> milestoneRecords = scheduleRecord.milestoneRecords();
        for (MilestoneRecord milestoneRecord : milestoneRecords) {
            Milestone milestone = new Milestone(milestoneRecord.name(), milestoneRecord.referenceDate());
            ScheduleWindowsRecord scheduleWindowsRecord = milestoneRecord.scheduleWindowsRecord();

            milestone.addMilestoneWindow(WindowName.Upcoming, new MilestoneWindow(WallTimeFactory.create(scheduleWindowsRecord.earliest()), WallTimeFactory.create(scheduleWindowsRecord.due())));
            milestone.addMilestoneWindow(WindowName.Due, new MilestoneWindow(WallTimeFactory.create(scheduleWindowsRecord.due()), WallTimeFactory.create(scheduleWindowsRecord.late())));
            milestone.addMilestoneWindow(WindowName.Late, new MilestoneWindow(WallTimeFactory.create(scheduleWindowsRecord.late()), WallTimeFactory.create(scheduleWindowsRecord.max())));

            schedule.addMilestone(milestone);

            for (AlertRecord alertRecord : milestoneRecord.alerts()) {
                MilestoneWindow milestoneWindow = milestone.window(WindowName.valueOf(alertRecord.window()));
                milestoneWindow.addAlert(new Alert(WallTimeFactory.create(alertRecord.startOffset()), WallTimeFactory.create(alertRecord.interval()), Integer.parseInt(alertRecord.count())));
            }
        }
        return schedule;
    }
}
