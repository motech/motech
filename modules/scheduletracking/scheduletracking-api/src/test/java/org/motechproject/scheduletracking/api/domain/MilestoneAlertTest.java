package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.weeks;
import static org.motechproject.util.DateUtil.newDateTime;

public class MilestoneAlertTest {

    @Test
    public void shouldCalcuateAndStoreAlertDates() {
        final Milestone milestone = new Milestone("M1", weeks(1), weeks(1), weeks(1), weeks(1));
        final DateTime referenceDateTime = newDateTime(2000, 1, 1, 0, 0, 0);
        final MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, referenceDateTime);

        assertThat(milestoneAlert.getMilestoneName(), is(equalTo("M1")));
        assertThat(milestoneAlert.getEarliestDateTime(), is(equalTo(newDateTime(2000, 1, 1, 0, 0, 0))));
        assertThat(milestoneAlert.getDueDateTime(), is(equalTo(newDateTime(2000, 1, 8, 0, 0, 0))));
        assertThat(milestoneAlert.getLateDateTime(), is(equalTo(newDateTime(2000, 1, 15, 0, 0, 0))));
        assertThat(milestoneAlert.getDefaultmentDateTime(), is(equalTo(newDateTime(2000, 1, 22, 0, 0, 0))));
    }
}
