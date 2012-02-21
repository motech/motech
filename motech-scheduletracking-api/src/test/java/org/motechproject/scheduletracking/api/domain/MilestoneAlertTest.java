package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;

public class MilestoneAlertTest {

    @Test
    public void shouldCalcuateAndStoreAlertDates(){
        final Milestone milestone = new Milestone("M1", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        final LocalDate referenceDate = DateUtil.newDate(2000, 1, 1);
        final MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, referenceDate);

        assertThat(milestoneAlert.getMilestoneName(), is(equalTo("M1")));
        assertThat(milestoneAlert.getEarliestDate(), is(equalTo(DateUtil.newDate(2000, 1, 8))));
        assertThat(milestoneAlert.getDueDate(), is(equalTo(DateUtil.newDate(2000, 1, 15))));
        assertThat(milestoneAlert.getLateDate(), is(equalTo(DateUtil.newDate(2000, 1, 22))));
        assertThat(milestoneAlert.getDefaultmentDate(), is(equalTo(DateUtil.newDate(2000, 1, 29))));
    }
}
