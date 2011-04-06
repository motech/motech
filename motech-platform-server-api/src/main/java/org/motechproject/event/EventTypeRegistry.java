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
package org.motechproject.event;

import java.util.concurrent.ConcurrentHashMap;


/**
 * A central registry for all event types
 */
public class EventTypeRegistry {

    // Central registry for scheduled event listeners
    private static ConcurrentHashMap<String, EventType> eventTypes = new ConcurrentHashMap<String, EventType>();

    /**
     * Register an event type so that it can be referenced by others
     *
     * @param type The event type being registered
     */
    public void add(EventType type) {
        eventTypes.put(type.getKey(), type);
    }

    /**
     * Register an event type so that it can be referenced by others
     *
     * @param types The event types being registered
     */
    public void addAll(EventType[] types) {
        for(int i = 0; i < types.length; i++) {
            this.add(types[i]);
        }
    }

    /**
     * Retrieve a concrete instance of the event type you are looking for
     * @param key The event type that you are looking for
     * @return A concrete instance of the event type that 
     */
    public EventType getEventType(String key) {
        EventType type = null;
        if (eventTypes.containsKey(key)) {
            type = eventTypes.get(key);
        }

        return type;

    }
}
