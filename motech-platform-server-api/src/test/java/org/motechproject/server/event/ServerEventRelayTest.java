/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
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
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.server.event;

import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.gateway.StubOutboundEventGateway;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationPlatformServerAPI.xml" })
public class ServerEventRelayTest
{
	@Autowired
    EventListenerRegistry registry;
    @Autowired
    MetricsAgent metricsAgent;

    ServerEventRelay eventRelay;
    OutboundEventGateway outboundEventGateway;
    MotechEvent motechEvent = null;

    @Before
    public void setUp() throws Exception {
        outboundEventGateway = Mockito.mock(StubOutboundEventGateway.class);
        eventRelay = new ServerEventRelay(outboundEventGateway, registry, metricsAgent);

        // Create the scheduled event message object
        Map<String, Object> messageParameters = new HashMap<String, Object>();
        messageParameters.put("test", "value");
        motechEvent = new MotechEvent("org.motechproject.server.someevent", messageParameters);
    }

    @After
    public void tearDown() throws NoSuchFieldException
    {
        // Clear out the event listener registry
        PrivateAccessor.setField(registry, "listenerTree", new EventListenerTree());
    }

    @Test
    public void testRelayToSingleListener() throws Exception {
        // Register a single listener for an event
        SampleEventListener sel = mock(SampleEventListener.class);
        registry.registerListener(sel, "org.motechproject.server.someevent");

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
        registry.registerListener(sel, "org.motechproject.server.someevent");

        FooEventListener fel = mock(FooEventListener.class);
        stub(fel.getIdentifier()).toReturn("FooEventListener");
        registry.registerListener(fel, "org.motechproject.server.someevent");

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

    @Test(expected = IllegalArgumentException.class)
    public void testRelayNullEvent() throws Exception {
        eventRelay.relayEvent(null);
    }

    @Test
    public void testRelaySpecificDestinationEvent() throws Exception {
        // Register a single listener for an event
        SampleEventListener sel = mock(SampleEventListener.class);
        stub(sel.getIdentifier()).toReturn("SampleEventListener");
        registry.registerListener(sel, "org.motechproject.server.someevent");

        FooEventListener fel = mock(FooEventListener.class);
        stub(fel.getIdentifier()).toReturn("FooEventListener");
        registry.registerListener(fel, "org.motechproject.server.someevent");

        // Create my own event so I don't pollute the main one with a new param
        // This event is the same as the one created in  setUp only it is augmented like a split relayed event
        Map<String , Object> originalParameters = new HashMap<String, Object>();
        originalParameters.put("test", "value");

        Map<String, Object> messageParameters = new HashMap<String, Object>();
        messageParameters.put("original-parameters", originalParameters);
        messageParameters.put("message-destination", "FooEventListener");
        MotechEvent _motechEvent = new MotechEvent("org.motechproject.server.someevent", messageParameters);

        eventRelay.relayEvent(_motechEvent);

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
