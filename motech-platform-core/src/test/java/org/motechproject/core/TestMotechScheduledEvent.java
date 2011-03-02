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
package org.motechproject.core;

import org.junit.Test;
import org.motechproject.model.MotechScheduledEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 2/28/11
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestMotechScheduledEvent {
    private String uuidStr = UUID.randomUUID().toString();
    private String uuidStr2 = UUID.randomUUID().toString();

    @Test
    public void newTest() throws Exception{
        MotechScheduledEvent scheduledEvent;
        boolean exceptionThrown = false;
        try {
            scheduledEvent = new MotechScheduledEvent(null, "testEvent", null);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            scheduledEvent = new MotechScheduledEvent(uuidStr, null, null);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testGetParameters() {
        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, "testEvent", null);
        Map<String, Object> params = scheduledEvent.getParameters();

        assertNotNull("Expecting param object", params);

        HashMap hashMap = new HashMap();
        hashMap.put("One", new Integer(1));

        MotechScheduledEvent nonNullParams = new MotechScheduledEvent(uuidStr, "testEvent", hashMap);
        params = nonNullParams.getParameters();

        assertTrue(params.equals(hashMap));
        assertFalse(params == hashMap);
    }

    @Test
    public void equalsTest() throws Exception{
        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent(uuidStr, "testEvent", null);
        MotechScheduledEvent scheduledEventSame = new MotechScheduledEvent(uuidStr, "testEvent", null);
        MotechScheduledEvent scheduledEventDifferentJobId = new MotechScheduledEvent(uuidStr2, "testEvent", null);
        MotechScheduledEvent scheduledEventDifferentEventType = new MotechScheduledEvent(uuidStr, "testEvent2", null);

        HashMap hashMap = new HashMap();
        hashMap.put("One", new Integer(1));

        MotechScheduledEvent nonNullParams = new MotechScheduledEvent(uuidStr, "testEvent", hashMap);
        MotechScheduledEvent nonNullParams2 = new MotechScheduledEvent(uuidStr, "testEvent", hashMap);

        assertTrue(scheduledEvent.equals(scheduledEvent));
        assertTrue(scheduledEvent.equals(scheduledEventSame));
        assertTrue(nonNullParams.equals(nonNullParams2));

        assertFalse(scheduledEvent.equals(null));
        assertFalse(scheduledEvent.equals(uuidStr));
        assertFalse(scheduledEvent.equals(scheduledEventDifferentEventType));
        assertFalse(scheduledEvent.equals(scheduledEventDifferentJobId));

        assertFalse(scheduledEvent.equals(nonNullParams));
        assertFalse(nonNullParams.equals(scheduledEvent));
    }
}
