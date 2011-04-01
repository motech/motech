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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.motechproject.event.EventType;
import org.motechproject.event.EventTypeRegistry;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.gateway.OutboundEventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class EventRelayTest {

	@Autowired
    EventListenerRegistry registry;

    @Autowired
    EventTypeRegistry eventTypeRegistry;

    @Autowired
    EventRelay eventRelay;

    @Autowired
    OutboundEventGateway outboundEventGateway;

    MotechEvent motechEvent = null;
    
    List<EventType> eventTypes = null;
    SampleEventListener sel = null;


    @Before
    public void setUp() throws Exception {
        // Create event type array for creating a scheduled event listener
        eventTypes = new ArrayList<EventType>();
        eventTypes.add(new SampleEventType());
        
        // Add the event type to the registry
        eventTypeRegistry.add(eventTypes.get(0));

        // Create the scheduled event message object
        Map<String, Object> messageParameters = new HashMap<String, Object>();
        messageParameters.put("test", "value");
        motechEvent = new MotechEvent("abcd123", eventTypes.get(0).getKey(), messageParameters);
    }

    @After
    public void tearDown() throws NoSuchFieldException
    {
        // Clear out the event listener registry
        PrivateAccessor.setField(registry, "eventListeners", new ConcurrentHashMap<String, List<EventListener>>());
    }

    @Test
    public void testRelayToSingleListener() throws Exception {
        // Register a single listener for an event
        SampleEventListener sel = mock(SampleEventListener.class);
        registry.registerListener(sel, eventTypes);

        eventRelay.relayEvent(motechEvent);

        verify(sel).handle(motechEvent);
    }

    @Test
    public void testSplittingRelay() throws Exception {
        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);
        String firstListener = "";
        String secondListener = "";

        // Register a single listener for an event
        SampleEventListener sel = mock(SampleEventListener.class);
        stub(sel.getIdentifier()).toReturn("SampleEventListener");
        registry.registerListener(sel, eventTypes);

        FooEventListener fel = mock(FooEventListener.class);
        stub(fel.getIdentifier()).toReturn("FooEventListener");
        registry.registerListener(fel, eventTypes);

        eventRelay.relayEvent(motechEvent);

        verify(outboundEventGateway, times(2)).sendEventMessage(argument.capture());
        MotechEvent event = argument.getAllValues().get(0);
        firstListener = (String)event.getParameters().get("message-destination");
        assertTrue(event.getParameters().containsKey("message-destination"));

        verify(outboundEventGateway, times(2)).sendEventMessage(argument.capture());
        event = argument.getAllValues().get(1);
        secondListener = (String)event.getParameters().get("message-destination");
        assertTrue(event.getParameters().containsKey("message-destination"));

        assertFalse(firstListener.equals(secondListener));
    }

    @Test
    public void testRelayNullEvent() throws Exception {
        boolean exceptionThrown = false;

        try {
            eventRelay.relayEvent(null);
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertFalse(exceptionThrown);
    }

    @Test
    public void testRelaySpecificDestinationEvent() throws Exception {
        // Register a single listener for an event
        SampleEventListener sel = mock(SampleEventListener.class);
        stub(sel.getIdentifier()).toReturn("SampleEventListener");
        registry.registerListener(sel, eventTypes);

        FooEventListener fel = mock(FooEventListener.class);
        stub(fel.getIdentifier()).toReturn("FooEventListener");
        registry.registerListener(fel, eventTypes);

        // Create my own event so I don't pollute the main one with a new param
        Map<String, Object> messageParameters = new HashMap<String, Object>();
        messageParameters.put("test", "value");
        messageParameters.put("message-destination", "FooEventListener");
        MotechEvent motechEvent = new MotechEvent("abcd123", eventTypes.get(0).getKey(), messageParameters);

        eventRelay.relayEvent(motechEvent);

        verify(fel).handle(motechEvent);
        verify(sel, never()).handle(any(MotechEvent.class));
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
}
