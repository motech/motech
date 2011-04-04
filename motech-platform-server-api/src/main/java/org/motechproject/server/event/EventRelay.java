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

import org.motechproject.event.EventTypeRegistry;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.gateway.OutboundEventGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

/**
 * This class handled incoming scheduled events and relays those events to the appropriate event listeners
 */
public class EventRelay {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EventTypeRegistry eventTypeRegistry;

	@Autowired
	private EventListenerRegistry eventListenerRegistry;

	@Autowired
    private OutboundEventGateway outboundEventGateway;

    @Autowired
    private MetricsAgent metricsAgent;

    private static final String MESSAGE_DESTINATION = "message-destination";
    
    public EventRelay() {

    }
    
    /**
     * Relay an event to all the listeners of that event.
     * @param event event being relayed
     */
    public void relayEvent(MotechEvent event) {

        // Retrieve a list of listeners for the given event type
    	if (eventListenerRegistry == null) {
            String errorMessage = "eventListenerRegistry == null";
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
    	}

    	if (eventTypeRegistry == null) {
            String errorMessage = "eventTypeRegistry == null";
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
    	}

        if (event == null) {
            String errorMessage = "Invalid request to relay null event";
            log.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        List<EventListener> listeners = eventListenerRegistry.getListeners( eventTypeRegistry.getEventType(event.getEventType()) );

        // Is this message destine for a specific listener?
        if (event.getParameters().containsKey(MESSAGE_DESTINATION)) {
        	
        	EventListener listener = null;
        	String messageDestination = (String) event.getParameters().get(MESSAGE_DESTINATION);

        	Iterator<EventListener> iter = listeners.iterator();
        	while( iter.hasNext() ) {
        		listener = iter.next();
                if (listener.getIdentifier().equals(messageDestination)) {
                    String timer = listener.getIdentifier() + ".handler." + event.getEventType();
                    metricsAgent.startTimer(timer);
        			listener.handle(event);
                    metricsAgent.stopTimer(timer);
        			break;
        		}
        	} // END while( iter.hasNext() )
        	
        } else {
        	
        	// Is there a single listener?
	        if (listeners.size() > 1) {
	        	// We need to split the message for each listener to ensure the work units
	        	// are completed individually. Therefore, if a message fails it will be
	        	// re-distributed to another server without being lost
	        	splitEvent(event, listeners);
	        } else {
                EventListener listener = listeners.get(0);
                String timer = listener.getIdentifier() + ".handler." + event.getEventType();
                metricsAgent.startTimer(timer);
	        	listener.handle(event);
                metricsAgent.stopTimer(timer);
	        } // END IF/ELSE if (listeners.size() > 1)
        } // END IF/ELSE if (event.getParameters().containsKey(MESSAGE_DESTINATION))

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("event", event.getEventType());
        parameters.put("listeners", String.format("%d", listeners.size()));
        metricsAgent.logEvent("motech.event-relay.relayEvent", parameters);
    }
    
    /**
     * Split a given message into multiple messages with specific message destination
     * parameters. Message destinations will route the message to the specific message
     * listener.
     * @param event Event message to be split
     * @param listeners A list of listeners for this given message that will be used as message destinations
     */
    private void splitEvent(MotechEvent event, List<EventListener> listeners) {
    	MotechEvent enrichedEventMessage = null;
    	EventListener listener = null;
    	Map<String, Object> parameters = null;
    	for( Iterator<EventListener> iter = listeners.iterator(); iter.hasNext(); ) {
    		listener = iter.next();
    		parameters = new HashMap<String, Object>(event.getParameters());
    		parameters.put(MESSAGE_DESTINATION, listener.getIdentifier());
    		enrichedEventMessage = new MotechEvent(event.getJobId(), event.getEventType(), parameters);
    		
    		outboundEventGateway.sendEventMessage(enrichedEventMessage);
    	}
    }

}
