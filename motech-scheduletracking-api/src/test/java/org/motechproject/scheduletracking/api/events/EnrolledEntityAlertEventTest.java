package org.motechproject.scheduletracking.api.events;

import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKey;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EnrolledEntityAlertEventTest {
	@Test
	public void toMotechEventShouldHaveAJobId() {
		String jobId = "job_id_001";

		EnrolledEntityAlertEvent event = new EnrolledEntityAlertEvent("åå", jobId);
		MotechEvent motechEvent = event.toMotechEvent();
		String jobID = (String) motechEvent.getParameters().get(EventDataKey.JOB_ID);
		assertThat(jobID, is(equalTo(jobId)));
	}
}
