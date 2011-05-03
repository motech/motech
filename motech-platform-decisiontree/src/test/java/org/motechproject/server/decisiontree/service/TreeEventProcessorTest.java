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
package org.motechproject.server.decisiontree.service;


import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.context.Context;
import org.motechproject.decisiontree.model.Action;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.EventRelay;

/**
 * Tests TreeEventProcessor by mocking EventRelay
 * @author yyonkov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TreeEventProcessorTest {
	private static final String PATIENT_ID = "0001";
	private Node node = Node.newBuilder()
							.setActionsBefore( Arrays.<Action>asList(
									Action.newBuilder().setEventId("eventbefore1").build(),
									Action.newBuilder().setEventId("eventbefore2").build(),
									Action.newBuilder().setEventId("eventbefore3").build()
							))
							.setActionsAfter( Arrays.<Action>asList(
									Action.newBuilder().setEventId("eventafter1").build(),
									Action.newBuilder().setEventId("eventafter2").build()
							))
							.setTransitions(new Object[][] {
									{ "1", Transition.newBuilder().setName("tr1").setActions(Arrays.<Action>asList(
											Action.newBuilder().setEventId("eventr1").build()
									)).build() }
							})
						.build();
	
	@InjectMocks
	TreeEventProcessor treeEventProcessor = new TreeEventProcessor();
	
	@Mock
	Context context;
	
	@Mock
	EventRelay eventRelay;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(context.getEventRelay()).thenReturn(eventRelay);
	}

	@Test
	public void testNodeActionsBefore() {
		treeEventProcessor.sendActionsBefore(node, "/", PATIENT_ID);
		verify(eventRelay, times(3)).sendEventMessage(any(MotechEvent.class));
	}
	
	@Test
	public void testNodeActionsAfter() {
		treeEventProcessor.sendActionsAfter(node, "/", PATIENT_ID);
		verify(eventRelay, times(2)).sendEventMessage(any(MotechEvent.class));
	}
	
	@Test
	public void testTransitionActions() {
		treeEventProcessor.sendTransitionActions(node.getTransitions().get("1"), PATIENT_ID);
		verify(eventRelay, times(1)).sendEventMessage(any(MotechEvent.class));		
	}
	@Test
	public void testActionsEdgeCases() {
		treeEventProcessor.sendTransitionActions(null, PATIENT_ID);		
		treeEventProcessor.sendTransitionActions(Transition.newBuilder().build(), PATIENT_ID);
		treeEventProcessor.sendActionsBefore(null, "/", PATIENT_ID);
		treeEventProcessor.sendActionsBefore(Node.newBuilder().build(), "/", PATIENT_ID);
		treeEventProcessor.sendActionsAfter(null, "/", PATIENT_ID);
		treeEventProcessor.sendActionsAfter(Node.newBuilder().build(), "/", PATIENT_ID);		
		verify(eventRelay, times(0)).sendEventMessage(any(MotechEvent.class));		
	}
}
