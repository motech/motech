package org.motechproject.scheduletracking.api.domain.factory;

import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.userspecified.AlertRecord;
import org.motechproject.scheduletracking.api.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleWindowsRecord;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.List;

public class ScheduleFactory {
    public static Schedule create(ScheduleRecord scheduleRecord) {
        List<MilestoneRecord> milestones = scheduleRecord.milestoneRecords();

        Milestone lastMilestone = null;

        int index = milestones.size() - 1;
        do {
            MilestoneRecord milestoneRecord = milestones.get(index--);
            ScheduleWindowsRecord windowsRecord = milestoneRecord.scheduleWindowsRecord();
            Milestone milestone = new Milestone(milestoneRecord.name(), lastMilestone, wallTime(windowsRecord.earliest()), wallTime(windowsRecord.due()), wallTime(windowsRecord.late()), wallTime(windowsRecord.max()));

            milestone.data(milestoneRecord.data());
            for (AlertRecord alertRecord : milestoneRecord.alerts()) {
                MilestoneWindow milestoneWindow = milestone.window(WindowName.valueOf(alertRecord.window()));
                milestoneWindow.addAlert(new AlertConfiguration(WallTimeFactory.create(alertRecord.startOffset()), WallTimeFactory.create(alertRecord.interval()), Integer.parseInt(alertRecord.count())));
            }

            lastMilestone = milestone;
        } while (index >= 0);

        return new Schedule(scheduleRecord.name(), WallTimeFactory.create(scheduleRecord.totalDuration()), lastMilestone);
    }

    private static WallTime wallTime(String userDefinedTime) {
        return WallTimeFactory.create(userDefinedTime);
    }
}
