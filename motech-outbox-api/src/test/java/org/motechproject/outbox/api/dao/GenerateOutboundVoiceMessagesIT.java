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
package org.motechproject.outbox.api.dao;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * This class is not a real integration test. The main purpose of this class is to generate test data for outbox IVR test.
 * In order to generate test outbox data uncomment code in the setUp() method and run the dummyTest()
 *
 * @author Igor (iopushnyev@2paths.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationOutboxAPI.xml"})
public class GenerateOutboundVoiceMessagesIT {
    @Autowired
    private OutboundVoiceMessageDao outboundVoiceMessageDao;
    private String partyId1 = "10";

    @Before
    public void setUp() {
        VoiceMessageType messageType = new VoiceMessageType();
        messageType.setVoiceMessageTypeName("Type1");
        messageType.setPriority(MessagePriority.HIGH);
        messageType.setvXmlTemplateName("appointmentReminder");
        messageType.setCanBeSaved(true);

        VoiceMessageType messageType1 = new VoiceMessageType();
        messageType1.setVoiceMessageTypeName("Type2");
        messageType1.setPriority(MessagePriority.HIGH);
        messageType1.setvXmlTemplateName("appointmentReminder");
        messageType1.setCanBeReplayed(true);

        VoiceMessageType messageType2 = new VoiceMessageType();
        messageType2.setVoiceMessageTypeName("Type3");
        messageType2.setPriority(MessagePriority.HIGH);
        messageType2.setvXmlTemplateName("appointmentReminder");
        messageType2.setCanBeSaved(true);
        messageType2.setCanBeReplayed(true);

        Date now = DateUtils.truncate(new Date(), Calendar.DATE);

        OutboundVoiceMessage msg1 = new OutboundVoiceMessage();
        msg1.setVoiceMessageType(messageType);
        msg1.setPartyId(partyId1);
        msg1.setCreationTime(now);
        msg1.setExpirationDate(DateUtils.addDays(now, 2));
        msg1.setStatus(OutboundVoiceMessageStatus.PENDING);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("message", "with doctor John Doe");
        msg1.setParameters(params);
        outboundVoiceMessageDao.add(msg1);


        OutboundVoiceMessage msg2 = new OutboundVoiceMessage();
        msg2.setVoiceMessageType(messageType1);
        msg2.setPartyId(partyId1);
        msg2.setCreationTime(now);
        msg2.setExpirationDate(DateUtils.addDays(now, 2));
        msg2.setStatus(OutboundVoiceMessageStatus.PENDING);
        params.clear();
        params.put("message", "with doctor Simpson");
        msg2.setParameters(params);
        outboundVoiceMessageDao.add(msg2);


        OutboundVoiceMessage msg3 = new OutboundVoiceMessage();
        msg3.setVoiceMessageType(messageType2);
        msg3.setPartyId(partyId1);
        msg3.setCreationTime(now);
        msg3.setExpirationDate(DateUtils.addDays(now, 2));
        msg3.setStatus(OutboundVoiceMessageStatus.PENDING);
        params.clear();
        params.put("message", "with doctor House");
        msg3.setParameters(params);
        outboundVoiceMessageDao.add(msg3);


    }

    @Test
    public void dummyTest() {

        List<OutboundVoiceMessage> msgs = outboundVoiceMessageDao.getPendingMessages(partyId1);

        for (OutboundVoiceMessage msg : msgs) {
            System.out.println(msg);
        }

        //System.out.println(msgs.get(0));
    }


}
