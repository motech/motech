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

import org.motechproject.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class acts as a registry for all scheduled event listeners. One can register themselves to listen for
 * a specific set of event types.
 */
public class EventListenerRegistry {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Central registry for scheduled event listeners
    private static ConcurrentHashMap<String, List<EventListener>> eventListeners = new ConcurrentHashMap<String, List<EventListener>>();

    /**
     * Register an event listener to be notified when events of a given type are received via the Server JMS Event Queue
     *
     * @param listener the listener instance
     * @param eventTypes the event types that a listener is interested in
     */
    public void registerListener(EventListener listener, List<EventType> eventTypes) {

        if (listener == null) {
            String errorMessage = "Invalid attempt to register a null EventListener";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (eventTypes == null) {
            String errorMessage = "Invalid attempt to register for null EventTypes";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        List<EventListener> listeners = null;
        // Add the listener to the  list of those interested in each event type
        for (int i = 0; i < eventTypes.size(); i++) {
            // Check if there are any other listeners for this event
            if (eventListeners.containsKey(eventTypes.get(i).getKey())) {
                listeners = eventListeners.get(eventTypes.get(i).getKey());
            } else {
                listeners = new ArrayList<EventListener>();
            }

            // Don't allow duplicate listener registrations
            if (!listeners.contains(listener)) {
                listeners.add(listener); // Add the listener to the list
                eventListeners.put(eventTypes.get(i).getKey(), listeners); // Add it back to the collection
            } else {
                log.info(String.format("Ignoring second request to register listener %s for event %s", listener.getIdentifier(), eventTypes.get(i).getKey()));
            }
        }
    }

    /**
     * Retrieve a list of event listeners for a given event type. If there are no listeners, null is returned.
     * @param type The event type that you are seeking listeners for
     * @return A list of scheduled event listeners that are interested in that event
     */
    public List<EventListener> getListeners(EventType type) {
        List<EventListener> listeners =  null;
        if (eventListeners.containsKey(type.getKey())) {
            listeners = eventListeners.get(type.getKey());
        }

        return listeners;

    }

}
