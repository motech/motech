package org.motechproject.server.event.scheduled;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.*;
import static org.mockito.Mockito.*;
import org.motechproject.event.EventType;
import org.motechproject.model.MotechScheduledEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
////specifies the Spring configuration to load for this test fixture
//@ContextConfiguration(locations={"/testApplicationContext.xml"})
public class ScheduledEventListenerRegistryTest {
	@Test
	public void testRegisterListener() {
		List<EventType> et = new ArrayList<EventType>(); 
		et.add(new TestEventType("test", "test"));
		ScheduledEventListener sel = new TestScheduledEventListener();
		ScheduledEventListenerRegistry registry = ScheduledEventListenerRegistry.getInstance();
		registry.registerListener(sel, et);
		
		assertNotNull(registry.getListeners(et.get(0)));
		assertTrue(registry.getListeners(et.get(0)).size() == 1);
		assertEquals(registry.getListeners(et.get(0)).get(0), sel);
	}
	
	@Test
	public void testGetListeners() {
		List<EventType> et = new ArrayList<EventType>(); 
		et.add(new TestEventType("test", "test"));
		ScheduledEventListener sel = new TestScheduledEventListener();
		ScheduledEventListenerRegistry registry = ScheduledEventListenerRegistry.getInstance();
		registry.registerListener(sel, et);
		
		assertNotNull(registry.getListeners(et.get(0)));
		assertTrue(registry.getListeners(et.get(0)).size() == 1);
		assertEquals(registry.getListeners(et.get(0)).get(0), sel);
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
	
	class TestScheduledEventListener implements ScheduledEventListener {
		
		private boolean handleCalled = false;
		
		@Override
		public void handle(MotechScheduledEvent event) {
			handleCalled = true;
		}
		
		public boolean handledMethodCalled() {
			return handleCalled;
		}
	}
	
}
