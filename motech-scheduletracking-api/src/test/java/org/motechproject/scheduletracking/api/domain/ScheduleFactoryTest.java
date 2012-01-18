package org.motechproject.scheduletracking.api.domain;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.ScheduleFactory;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ScheduleFactoryTest {
    @Test
    public void records() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
        ScheduleRecord scheduleRecord = jsonReader.records().get(0);
        Schedule schedule = ScheduleFactory.create(scheduleRecord);

        assertThat(schedule, is(notNullValue()));
        assertThat(schedule.getName(), is(equalTo(scheduleRecord.name())));

        Milestone firstMilestone = schedule.getFirstMilestone();
        assertThat(firstMilestone.getName(), is(equalTo("IPTI 1")));
        assertThat(firstMilestone.getNextMilestone().getName(), is(equalTo("IPTI 2")));
        Map<String, String> data = firstMilestone.getData();
        assertThat(data.size(), is(equalTo(1)));
        assertThat(data.get("Foo"), is(equalTo("Bar")));

        Milestone secondMilestone = firstMilestone.getNextMilestone();
        assertThat(secondMilestone.getName(), is(equalTo("IPTI 2")));
        assertThat(secondMilestone.getNextMilestone(), is(nullValue()));
    }
}
