package org.motechproject.server.event.scheduled;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.event.EventType;
import org.motechproject.event.EventTypeRegistry;

public class EventTypeRegistryTest {

	@Test
	public void testGetEventType() {
		EventType eventType = new TestEventType("test", "test");
		EventTypeRegistry etr = EventTypeRegistry.getInstance();
		assertNotNull( "EventTyepRegistry.getInstance() returned null", etr );
		etr.setEventType(eventType);
		assertNotNull( etr.getEventType(eventType.getKey()) );
		assertTrue(etr.getEventType(eventType.getKey()).getKey() == eventType.getKey());
		assertTrue(etr.getEventType(eventType.getKey()).getName() == eventType.getName());
	}
	
	class TestEventType implements EventType {
		
		private String key = null;
		private String name = null;
		
		public TestEventType(String name, String key) {
			this.name = name;
			this.key = key;
		}
		
		@Override
		public String getKey() {
			return key;
		}
		
		@Override
		public String getName() {
			return name;
		}
	}

}
