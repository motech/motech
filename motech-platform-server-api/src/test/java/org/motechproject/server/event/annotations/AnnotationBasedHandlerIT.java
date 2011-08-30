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
package org.motechproject.server.event.annotations;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.context.Context;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.EventListenerRegistry;
import org.motechproject.server.event.ServerEventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationPlatformServerAPI.xml", "/testAnnotatedHandlers.xml"})
public class AnnotationBasedHandlerIT {

    static boolean test = false;

    @Autowired
    ServerEventRelay eventRelay;

    private void send(String dest, Object... objects) {
        Map<String, Object> params = new HashMap<String, Object>();
        int i = 0;
        for (Object obj : objects) {
            params.put(Integer.toString(i++), obj);
        }
        MotechEvent event = new MotechEvent(dest, params);
        eventRelay.relayEvent(event);
    }

    // Annotation based handler (needs a spring bean config.)
    @Component
    public static class MyHandler {
        @MotechListener(subjects = {"sub_a", "sub_b"})
        public void handleX(MotechEvent event) {
            test = true;
//			System.out.println(event);
        }

        @MotechListener(subjects = {"sub_a", "sub_c"})
        public void handleY(MotechEvent event) {
            test = true;
//			System.out.println(event);
        }

        @MotechListener(subjects = {"params"}, type = MotechListenerType.ORDERED_PARAMETERS)
        public void handleParams(Integer a, Integer b, String s) {
            test = true;
//			System.out.printf("a+b= %d\n",a+b);
        }

        @MotechListener(subjects = {"exception"}, type = MotechListenerType.ORDERED_PARAMETERS)
        public void orderedParams(Integer a, Integer b, String s) {
            Assert.notNull(s, "s must not be null");
            test = true;
//			System.out.printf("a+b= %d\n"+s,a+b);
        }

        @MotechListener(subjects = {"named"}, type = MotechListenerType.NAMED_PARAMETERS)
        public void namedParams(@MotechParam("id") String id, @MotechParam("key") String key) {
            test = true;
//			System.out.printf("id: %s, key: %s\n", id,key);
		}
	}

	public static void clear() {
		test=false;
	}

	@Test
	public void testRegistry() {
		EventListenerRegistry registry = Context.getInstance().getEventListenerRegistry();
		assertEquals(2, registry.getListenerCount("sub_a"));
		assertEquals(1,registry.getListenerCount("sub_b"));
		assertEquals(1,registry.getListenerCount("sub_c"));
	}
	
	@Test
	public void testRelay() {
		MotechEvent e = new MotechEvent("sub_b", null);
		clear();
		eventRelay.relayEvent(e);
		assertTrue(test);
		
		e = new MotechEvent("sub_c", null);
		clear();
		eventRelay.relayEvent(e);
		assertTrue(test);
	}
	
	@Test
	public void testOrderedParams() {
		clear();
		send("params",23,44,null);
		assertTrue(test);
	}
	
	@Test
	public void testExeption() {
		clear();
		send("exception", 1, 3, null);
		assertFalse(test);
	}

	@Test
	public void testNamedParamsHappy() {
		clear();
		MotechEvent event = new MotechEvent("named");
		event.getParameters().put("id", "id0012");
		event.getParameters().put("key", "2354");
		eventRelay.relayEvent(event);
		assertTrue(test);
	}

	@Test
	public void testNamedParamsNotHappy() {
		clear();
		MotechEvent event = new MotechEvent("named");
		event.getParameters().put("id", "id0012");
		event.getParameters().put("key", 1);
		eventRelay.relayEvent(event);
		assertFalse(test);
		clear();
		event.getParameters().clear();
		event.getParameters().put("id", "id0012");
		eventRelay.relayEvent(event);
		assertFalse(test);
	}
}
