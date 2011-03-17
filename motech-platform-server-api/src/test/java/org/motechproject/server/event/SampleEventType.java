package org.motechproject.server.event;

import org.motechproject.event.EventType;

public class SampleEventType implements EventType {

	private String key = "sampleeventtype";
	private String name = "SampleEventType";
	
	@Override
	public String getKey() {
		return key;
	}
	
	@Override
	public String getName() {
		return name;
	}

}
