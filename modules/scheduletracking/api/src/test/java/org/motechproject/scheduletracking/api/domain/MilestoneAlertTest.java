package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.weeks;
import static org.motechproject.util.DateUtil.newDateTime;

public class MilestoneAlertTest {

    @Test
    public void shouldCalcuateAndStoreAlertDates() {
        final String milestoneName = "M1";
        final Milestone milestone = new Milestone(milestoneName, weeks(1), weeks(1), weeks(1), weeks(1));
        final DateTime referenceDateTime = newDateTime(2000, 1, 1, 0, 0, 0);
        final MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, referenceDateTime);

        final DateTime earliestDateTime = newDateTime(2000, 1, 1, 0, 0, 0);
        final DateTime dueDateTime = newDateTime(2000, 1, 8, 0, 0, 0);
        final DateTime lateDateTime = newDateTime(2000, 1, 15, 0, 0, 0);
        final DateTime defaultmentDateTime = newDateTime(2000, 1, 22, 0, 0, 0);

        String milestoneAlertToString = "MilestoneAlert{" +
                "milestoneName='" + milestoneName + '\'' +
                ", earliestDateTime=" + earliestDateTime +
                ", dueDateTime=" + dueDateTime +
                ", lateDateTime=" + lateDateTime +
                ", defaultmentDateTime=" + defaultmentDateTime +
                '}';

        assertThat(milestoneAlert.getMilestoneName(), is(equalTo(milestoneName)));
        assertThat(milestoneAlert.getEarliestDateTime(), is(equalTo(earliestDateTime)));
        assertThat(milestoneAlert.getDueDateTime(), is(equalTo(dueDateTime)));
        assertThat(milestoneAlert.getLateDateTime(), is(equalTo(lateDateTime)));
        assertThat(milestoneAlert.getDefaultmentDateTime(), is(equalTo(defaultmentDateTime)));
        assertThat(milestoneAlert.toString(), is(equalTo(milestoneAlertToString)));
    }
}
