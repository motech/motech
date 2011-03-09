/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
