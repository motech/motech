package org.motechproject.scheduletracking.api.events;

import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKey;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class EnrolledEntityAlertEventTest {
	@Test
	public void toMotechEventShouldHaveAJobId() {
		String externalId = "externalId";
        String scheduleName = "scheduleName";

        EnrolledEntityAlertEvent event = new EnrolledEntityAlertEvent(scheduleName, externalId);
		MotechEvent motechEvent = event.toMotechEvent();
        Map<String,Object> parameters = motechEvent.getParameters();
        assertEquals(externalId, parameters.get(EventDataKey.EXTERNAL_ID));
        assertEquals(scheduleName, parameters.get(EventDataKey.SCHEDULE_NAME));
	}
}
