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
        Schedule schedule = new Schedule(scheduleRecord.name(), WallTimeFactory.create(scheduleRecord.totalDuration()));
        List<MilestoneRecord> milestoneRecords = scheduleRecord.milestoneRecords();
        for (MilestoneRecord milestoneRecord : milestoneRecords) {
            ScheduleWindowsRecord scheduleWindowsRecord = milestoneRecord.scheduleWindowsRecord();

            Referenceable referenceable = null;
            Milestone referencedMilestone = schedule.milestone(milestoneRecord.referenceDate());
            if (scheduleRecord.name().equals(milestoneRecord.referenceDate())) referenceable = schedule;
            else if (referencedMilestone != null) referenceable = referencedMilestone;
            else throw new ScheduleTrackingException("Reference Date in milestone: %s doesn't match any preceding milestones or the schedule name", milestoneRecord.name());

            Milestone milestone = new Milestone(milestoneRecord.name(), referenceable, wallTime(scheduleWindowsRecord.earliest()),
                    wallTime(scheduleWindowsRecord.due()), wallTime(scheduleWindowsRecord.late()), wallTime(scheduleWindowsRecord.max()));
            milestone.data(milestoneRecord.data());

            schedule.addMilestone(milestone);

            for (AlertRecord alertRecord : milestoneRecord.alerts()) {
                MilestoneWindow milestoneWindow = milestone.window(WindowName.valueOf(alertRecord.window()));
                milestoneWindow.addAlert(new AlertConfiguration(WallTimeFactory.create(alertRecord.startOffset()), WallTimeFactory.create(alertRecord.interval()), Integer.parseInt(alertRecord.count())));
            }
        }
        return schedule;
    }

    private static WallTime wallTime(String userDefinedTime) {
        return WallTimeFactory.create(userDefinedTime);
    }
}
