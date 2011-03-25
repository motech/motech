package org.motechproject.openmrs.messaging;

import org.motechproject.model.MotechEvent;

public interface MotechEventSender {

	public void send(MotechEvent motechEvent);
	
}
