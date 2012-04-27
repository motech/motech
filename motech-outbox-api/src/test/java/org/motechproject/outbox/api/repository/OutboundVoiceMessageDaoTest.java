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
package org.motechproject.outbox.api.repository;


import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.outbox.api.domain.MessagePriority;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * @author yyonkov
 */
@RunWith(MockitoJUnitRunner.class)
public class OutboundVoiceMessageDaoTest {
    private static final String PARTY_ID = "001";
    @Mock
    CouchDbConnector db;
    List<OutboundVoiceMessage> messages = new ArrayList<OutboundVoiceMessage>();

    static OutboundVoiceMessage buildMessage(
            Date creationTime,
            MessagePriority priority) {

        VoiceMessageType mt = new VoiceMessageType();
        mt.setPriority(priority);
        mt.setTemplateName("http://motech.2paths.com");
        OutboundVoiceMessage msg = new OutboundVoiceMessage();
        msg.setExternalId(PARTY_ID);
        msg.setStatus(OutboundVoiceMessageStatus.PENDING);
        msg.setCreationTime(creationTime);
        msg.setExpirationDate(new Date());
        msg.setVoiceMessageType(mt);
        return msg;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sortedBasedOnCreationTime_LatestFirst() {
        AllOutboundVoiceMessages dao = new AllOutboundVoiceMessages(db);

        DateTime now = DateUtil.now();
        OutboundVoiceMessage message1 = buildMessage(now.minusDays(1).toDate(), MessagePriority.HIGH);
        OutboundVoiceMessage message2 = buildMessage(now.toDate(), MessagePriority.HIGH);
        OutboundVoiceMessage message3 = buildMessage(now.plusDays(1).toDate(), MessagePriority.HIGH);

        messages.add(message1);
        messages.add(message2);
        messages.add(message3);

        when(db.queryView(any(ViewQuery.class), any(Class.class))).thenReturn(messages);

        List<OutboundVoiceMessage> pendingMessages = dao.getPendingMessages(PARTY_ID);
        assertThat(pendingMessages.get(0), is(message3));
        assertThat(pendingMessages.get(1), is(message2));
        assertThat(pendingMessages.get(2), is(message1));
    }
}
