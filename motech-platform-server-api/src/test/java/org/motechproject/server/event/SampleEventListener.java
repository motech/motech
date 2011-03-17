package org.motechproject.server.event;

import org.motechproject.model.MotechEvent;

public class SampleEventListener implements EventListener {

	private boolean handleCalled = false;
	
	@Override
	public void handle(MotechEvent event) {
		handleCalled = true;
	}
	
	@Override
	public String getIdentifier() {
		return "TestEventListener";
	}
	
	public boolean handledMethodCalled() {
		return handleCalled;
	}

}
