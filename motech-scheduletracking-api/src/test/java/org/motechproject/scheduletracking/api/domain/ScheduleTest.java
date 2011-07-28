package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import java.util.List;

public class ScheduleTest {
    @Test
    public void alerts() {
        Schedule schedule = new Schedule("IPTI Schedule");
        String firstMilestone = "IPTI 1";
        Milestone milestone = new Milestone(firstMilestone, "IPTI Schedule", new WallTime(1, WallTimeUnit.Week),
                new WallTime(2, WallTimeUnit.Week), new WallTime(2, WallTimeUnit.Week), new WallTime(2, WallTimeUnit.Week));
        schedule.addMilestone(milestone);
        List<Alert> alerts = schedule.alerts(firstMilestone, 0);
    }
}
