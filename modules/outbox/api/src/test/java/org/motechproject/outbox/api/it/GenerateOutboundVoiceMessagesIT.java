package org.motechproject.outbox.api.it;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.outbox.api.repository.AllOutboundVoiceMessages;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is not a real integration test. The main purpose of this class is to generate test data for outbox IVR test.
 * In order to generate test outbox data uncomment code in the setUp() method and run the dummyTest()
 *
 * @author Igor (iopushnyev@2paths.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class GenerateOutboundVoiceMessagesIT {
    @Autowired
    private AllOutboundVoiceMessages allOutboundVoiceMessages;
    private String externalId1 = "10";

    @Before
    public void setUp() {
        VoiceMessageType messageType = new VoiceMessageType();
        messageType.setVoiceMessageTypeName("Type1");
        messageType.setTemplateName("appointmentReminder");
        messageType.setCanBeSaved(true);

        VoiceMessageType messageType1 = new VoiceMessageType();
        messageType1.setVoiceMessageTypeName("Type2");
        messageType1.setTemplateName("appointmentReminder");
        messageType1.setCanBeReplayed(true);

        VoiceMessageType messageType2 = new VoiceMessageType();
        messageType2.setVoiceMessageTypeName("Type3");
        messageType2.setTemplateName("appointmentReminder");
        messageType2.setCanBeSaved(true);
        messageType2.setCanBeReplayed(true);

        DateTime now = DateUtil.now();

        OutboundVoiceMessage msg1 = new OutboundVoiceMessage();
        msg1.setVoiceMessageType(messageType);
        msg1.setExternalId(externalId1);
        msg1.setCreationTime(now.toDate());
        msg1.setExpirationDate(now.plusDays(2).toDate());
        msg1.setStatus(OutboundVoiceMessageStatus.PENDING);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("message", "with doctor John Doe");
        msg1.setParameters(params);
        allOutboundVoiceMessages.add(msg1);


        OutboundVoiceMessage msg2 = new OutboundVoiceMessage();
        msg2.setVoiceMessageType(messageType1);
        msg2.setExternalId(externalId1);
        msg2.setCreationTime(now.toDate());
        msg2.setExpirationDate(now.plusDays(2).toDate());
        msg2.setStatus(OutboundVoiceMessageStatus.PENDING);
        params.clear();
        params.put("message", "with doctor Simpson");
        msg2.setParameters(params);
        allOutboundVoiceMessages.add(msg2);


        OutboundVoiceMessage msg3 = new OutboundVoiceMessage();
        msg3.setVoiceMessageType(messageType2);
        msg3.setExternalId(externalId1);
        msg3.setCreationTime(now.toDate());
        msg3.setExpirationDate(now.plusDays(2).toDate());
        msg3.setStatus(OutboundVoiceMessageStatus.PENDING);
        params.clear();
        params.put("message", "with doctor House");
        msg3.setParameters(params);
        allOutboundVoiceMessages.add(msg3);


    }

    @Test
    public void dummyTest() {

        List<OutboundVoiceMessage> msgs = allOutboundVoiceMessages.getMessages(externalId1, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);

        for (OutboundVoiceMessage msg : msgs) {
            System.out.println(msg);
        }

        //System.out.println(msgs.get(0));
    }


}
