/*
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
import org.motechproject.outbox.api.builder.OutboundVoiceMessageBuilder;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
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
public class AllOutboundVoiceMessagesTest {
    @Mock
    private CouchDbConnector db;

    private String EXTERNAL_ID = "001";
    private List<OutboundVoiceMessage> messages = new ArrayList<OutboundVoiceMessage>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sortedBasedOnCreationTime_LatestFirst() {
        AllOutboundVoiceMessages dao = new AllOutboundVoiceMessages(db);

        DateTime now = DateUtil.now();
        OutboundVoiceMessage message1 = buildMessage(now.minusDays(1).toDate(), 0);
        OutboundVoiceMessage message2 = buildMessage(now.toDate(), 0);
        OutboundVoiceMessage message3 = buildMessage(now.plusDays(1).toDate(), 0);

        messages.add(message1);
        messages.add(message2);
        messages.add(message3);

        when(db.queryView(any(ViewQuery.class), any(Class.class))).thenReturn(messages);

        List<OutboundVoiceMessage> pendingMessages = dao.getMessages(EXTERNAL_ID, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
        assertThat(pendingMessages.get(0), is(message3));
        assertThat(pendingMessages.get(1), is(message2));
        assertThat(pendingMessages.get(2), is(message1));
    }

    @Test
    public void sortedBasedOnSequenceNumber() {
        AllOutboundVoiceMessages dao = new AllOutboundVoiceMessages(db);

        DateTime now = DateUtil.now();
        OutboundVoiceMessage message1 = buildMessage(now.minusDays(1).toDate(), 3);
        OutboundVoiceMessage message2 = buildMessage(now.toDate(), 1);
        OutboundVoiceMessage message3 = buildMessage(now.plusDays(1).toDate(), 2);

        messages.add(message1);
        messages.add(message2);
        messages.add(message3);

        when(db.queryView(any(ViewQuery.class), any(Class.class))).thenReturn(messages);

        List<OutboundVoiceMessage> pendingMessages = dao.getMessages(EXTERNAL_ID, OutboundVoiceMessageStatus.PENDING, SortKey.SequenceNumber);
        assertThat(pendingMessages.get(0), is(message2));
        assertThat(pendingMessages.get(1), is(message3));
        assertThat(pendingMessages.get(2), is(message1));

    }

    private OutboundVoiceMessage buildMessage(Date creationTime, long sequenceNumber) {
        return new OutboundVoiceMessageBuilder().withDefaults().withCreationTime(creationTime).withExternalId(EXTERNAL_ID)
                .withSequenceNumber(sequenceNumber).build();
    }
}
