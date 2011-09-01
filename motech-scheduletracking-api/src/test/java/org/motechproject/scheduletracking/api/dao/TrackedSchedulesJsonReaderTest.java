package org.motechproject.scheduletracking.api.dao;

import org.junit.Test;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.scheduletracking.api.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TrackedSchedulesJsonReaderTest {
    @Test
    public void records() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json", new MotechJsonReader());
        List<ScheduleRecord> records = jsonReader.records();
        assertThat(records.size(), is(equalTo(1)));
        ScheduleRecord scheduleRecord = records.get(0);
        assertThat(scheduleRecord.name(), is(equalTo("IPTI Schedule")));

        List<MilestoneRecord> milestones = scheduleRecord.milestoneRecords();
        assertThat(milestones.size(), is(equalTo(2)));

        MilestoneRecord firstMilestone = milestones.get(0);
        assertThat(firstMilestone.name(), is(equalTo("IPTI 1")));

        MilestoneRecord secondMilestone = milestones.get(1);
        assertThat(secondMilestone.name(), is(equalTo("IPTI 2")));
    }
}
