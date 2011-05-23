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
package org.motechproject.server.tama;


import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.event.annotations.MotechListenerType;
import org.motechproject.server.event.annotations.MotechParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

/**
 * Tests if event is fired when max pending messages is reached
 * @author yyonkov
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testApplicationContext.xml"})
public class OutboxMaxPendingMessagesIT {
	
	@Autowired
	private VoiceOutboxService voiceOutboxService;
	
	@Autowired
	private OutboundVoiceMessageDao outboundVoiceMessageDao;
	
	@Autowired
	private OutboundEventGateway outboundEventGateway;
	
	@Before
	public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
		for( OutboundVoiceMessage m : outboundVoiceMessageDao.getAll()) {
			outboundVoiceMessageDao.remove(m);
		}
	}
	
	// subscribes this method the the expected event
	@MotechListener(subjects={EventKeys.OUTBOX_MAX_PENDING_MESSAGES_EVENT_SUBJECT})
	public void maxMessagesReached(MotechEvent event) {}

	@Test
	public void testMaxMsg() {
		for(int i = 1; i<voiceOutboxService.getMaxNumberOfPendingMessages(); i++) {
			voiceOutboxService.addMessage(newMsg());
		}
		// event not sent
		verify(outboundEventGateway, never()).sendEventMessage(any(MotechEvent.class));
		// event sent 
		voiceOutboxService.addMessage(newMsg());
		verify(outboundEventGateway, times(1)).sendEventMessage(any(MotechEvent.class));
		// event not sent
		voiceOutboxService.addMessage(newMsg());
		verify(outboundEventGateway, times(1)).sendEventMessage(any(MotechEvent.class));
	}


	private OutboundVoiceMessage newMsg() {
		OutboundVoiceMessage msg = new OutboundVoiceMessage();
		msg.setPartyId("001");
		msg.setExpirationDate(DateUtils.addDays(new Date(), 1));
		msg.setStatus(OutboundVoiceMessageStatus.PENDING);
		return msg;
	}
}
