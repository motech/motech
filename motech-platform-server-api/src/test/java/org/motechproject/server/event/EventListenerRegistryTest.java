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
package org.motechproject.server.event;

import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.EventType;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
////specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={"/testApplicationContext.xml"})
public class EventListenerRegistryTest {
	
	@Autowired
	private EventListenerRegistry registry;

    @After
    public void tearDown() throws NoSuchFieldException
    {
        PrivateAccessor.setField(registry, "eventListeners", new ConcurrentHashMap<String, List<EventListener>>());
    }

    @Test
    public void testNullEventListenerRegistration() {
        boolean exceptionThrown = false;
        List<EventType> et = new ArrayList<EventType>();
		et.add(new SampleEventType());
        EventListener sel = null;

        try {
            registry.registerListener(sel, et);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    public void testNullEventTypeRegistration() {
        boolean exceptionThrown = false;
        List<EventType> et = null;
        EventListener sel = new SampleEventListener();

        try {
            registry.registerListener(sel, et);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    public void testEmptyEventListRegistration() {
        boolean exceptionThrown = false;
        List<EventType> et = new ArrayList<EventType>();
		EventListener sel = new SampleEventListener();

        registry.registerListener(sel, et);

        try
        {
            Object o = PrivateAccessor.getField(registry, "eventListeners");
            Map<String, List<EventListener>> eventListeners;
            eventListeners = (Map<String, List<EventListener>>)o;

            assertEquals(eventListeners.size(), 0);
        } catch (NoSuchFieldException e)
        {
            exceptionThrown = true;
        }

        assertFalse(exceptionThrown);
    }

	@Test
	public void testRegisterSingleListener() {
		List<EventType> et = new ArrayList<EventType>(); 
		et.add(new SampleEventType());
		EventListener sel = new SampleEventListener();
		registry.registerListener(sel, et);
		
		assertNotNull(registry.getListeners(et.get(0)));
		assertTrue(registry.getListeners(et.get(0)).size() == 1);
		assertEquals(registry.getListeners(et.get(0)).get(0), sel);
	}

    @Test
	public void testRegisterMultipleListener() {
		List<EventType> et = new ArrayList<EventType>();
		et.add(new SampleEventType());
		EventListener sel = new SampleEventListener();
        EventListener sel2 = new FooEventListener();

		registry.registerListener(sel, et);
        registry.registerListener(sel2, et);

        List<EventListener> el = registry.getListeners(et.get(0));
		assertNotNull(el);
		assertTrue(el.size() == 2);
		assertTrue(el.contains(sel));
        assertTrue(el.contains(sel2));
	}

    @Test
	public void testRegisterForMultipleEvents() {
		List<EventType> et = new ArrayList<EventType>();
		et.add(new SampleEventType());
        et.add(new FooEventType());

		EventListener sel = new SampleEventListener();

		registry.registerListener(sel, et);

        List<EventListener> el = registry.getListeners(et.get(0));
		assertNotNull(el);
		assertTrue(el.size() == 1);
		assertTrue(el.contains(sel));

        el = registry.getListeners(et.get(1));
		assertNotNull(el);
		assertTrue(el.size() == 1);
		assertTrue(el.contains(sel));
	}

    @Test
	public void testRegisterTwice() {
		List<EventType> et = new ArrayList<EventType>();
		et.add(new SampleEventType());

		EventListener sel = new SampleEventListener();

		registry.registerListener(sel, et);
    	registry.registerListener(sel, et);

        List<EventListener> el = registry.getListeners(et.get(0));
		assertNotNull(el);
		assertTrue(el.size() == 1);
		assertTrue(el.contains(sel));
	}


    @Test
	public void testRegisterForSameEventTwice() {
		List<EventType> et = new ArrayList<EventType>();
		et.add(new SampleEventType());
        et.add(new SampleEventType());

		EventListener sel = new SampleEventListener();

		registry.registerListener(sel, et);

        List<EventListener> el = registry.getListeners(et.get(0));
		assertNotNull(el);
		assertTrue(el.size() == 1);
		assertTrue(el.contains(sel));

        el = registry.getListeners(et.get(1));
		assertNotNull(el);
		assertTrue(el.size() == 1);
		assertTrue(el.contains(sel));
	}

	@Test
	public void testGetListeners() {
		List<EventType> et = new ArrayList<EventType>(); 
		et.add(new SampleEventType());
		EventListener sel = new SampleEventListener();
		registry.registerListener(sel, et);
		
		assertNotNull(registry.getListeners(et.get(0)));
		assertTrue(registry.getListeners(et.get(0)).size() == 1);
		assertEquals(registry.getListeners(et.get(0)).get(0), sel);
	}
	
	@Test
	public void testGetEmptyListenerList() {
		List<EventType> et = new ArrayList<EventType>();
		et.add(new SampleEventType());

		assertNull(registry.getListeners(et.get(0)));
	}

    class FooEventListener implements EventListener {

        @Override
        public void handle(MotechEvent event) {
        }

        @Override
        public String getIdentifier() {
            return "FooEventListener";
        }
    }

    class FooEventType implements EventType {

        private String key = "fooeventtype";
        private String name = "FooEventType";

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

