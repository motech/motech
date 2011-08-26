package org.motechproject.scheduletracking.api.events;

import org.junit.Test;
import org.motechproject.model.MotechEvent;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EnrolledEntityAlertEventTest {
    @Test
    public void toMotechEventShouldHaveAJobId() throws Exception {
        String jobId = "job_id_001";

        EnrolledEntityAlertEvent event = new EnrolledEntityAlertEvent("åå", jobId);
        MotechEvent motechEvent = event.toMotechEvent();
        String jobID = (String) motechEvent.getParameters().get(EnrolledEntityAlertEvent.JOB_ID_KEY);
        assertThat(jobID, is(equalTo(jobId)));
    }
}
