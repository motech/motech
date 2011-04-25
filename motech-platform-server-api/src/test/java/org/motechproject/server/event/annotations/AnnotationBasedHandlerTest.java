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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.EventListenerRegistry;
import org.motechproject.server.event.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testApplicationContext.xml"})
public class AnnotationBasedHandlerTest {
	static MotechEvent motechEvent;
	
	
	// Annotation based handler (needs a spring bean config.) 
	public static class MyHandler {
		@MotechListener(subjects={"sub_a","sub_b"})
		public void handleX(MotechEvent event) {
			AnnotationBasedHandlerTest.motechEvent = event;
		}
		@MotechListener(subjects={"sub_a","sub_c"})
		public void handleY(MotechEvent event) {
			AnnotationBasedHandlerTest.motechEvent = event;
		}
	}
	

	@Autowired
	private EventListenerRegistry registry;
	@Autowired
    private EventRelay eventRelay;

	public static void clearMotechEvent() {
		AnnotationBasedHandlerTest.motechEvent = null;
	}
	@Test
	public void testRegistry() {
		assertEquals(2, registry.getListenerCount("sub_a"));
		assertEquals(1,registry.getListenerCount("sub_b"));
		assertEquals(1,registry.getListenerCount("sub_c"));
	}
	@Test
	public void testRelay() {
		MotechEvent e = new MotechEvent("sub_b", null);
		clearMotechEvent();
		eventRelay.relayEvent(e);
		assertEquals(e, motechEvent);

		e = new MotechEvent("sub_c", null);
		clearMotechEvent();
		eventRelay.relayEvent(e);
		assertEquals(e, motechEvent);
	}
}
