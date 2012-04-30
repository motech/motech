package org.motechproject.outbox.api.it;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.outbox.api.repository.AllOutboundVoiceMessages;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationOutboxAPI.xml"})
public class OutboundVoiceMessageDaoIT {
    @Autowired
    private AllOutboundVoiceMessages outboundVoiceMessageDao;

    private String externalId1 = "0001";
    private String externalId2 = "0002";
    private VoiceMessageType type1;
    private VoiceMessageType type2;

    @Before
    public void setUp() {
        outboundVoiceMessageDao.removeAll();

        type1 = new VoiceMessageType();
        type1.setVoiceMessageTypeName("Play something");
        type1.setTemplateName("appointmentReminder");

        type2 = new VoiceMessageType();
        type2.setVoiceMessageTypeName("Text something");
        type2.setTemplateName("appointmentReminder");

        // create messages
        createMessages(OutboundVoiceMessageStatus.PENDING);
        createMessages(OutboundVoiceMessageStatus.SAVED);
    }

    /*
        ExternalId1 - Type1 - Valid -  1
        ExternalId1 - Type1 - Invalid -  1
        ExternalId2 - Type2 - Valid - 2
        ExternalId2 - Type2 - Invalid - 1
     */
    private void createMessages(OutboundVoiceMessageStatus messageStatus) {
        DateTime now = DateUtil.now();
        for (int i = 0; i < 5; i++) {
            OutboundVoiceMessage msg = new OutboundVoiceMessage();
            msg.setCreationTime(now.minusDays(5).toDate());
            msg.setStatus(messageStatus);
            msg.setExpirationDate(i<2 ? now.minusDays(1).toDate() : now.plusDays(2).toDate());

            if ((i % 2) != 0) {
                msg.setExternalId(externalId1);
                msg.setVoiceMessageType(type1);
            } else {
                msg.setExternalId(externalId2);
                msg.setVoiceMessageType(type2);
            }
            outboundVoiceMessageDao.add(msg);
        }
    }

    @After
    public void tearDown() {
        outboundVoiceMessageDao.removeAll();
    }

    @Test
    public void shouldSaveTheListSpecifiedAsAParameter() {
        VoiceMessageType messageType = new VoiceMessageType();
        messageType.setVoiceMessageTypeName("Play something");
        messageType.setTemplateName("playSequentially");

        String patientId = "Patient1";
        OutboundVoiceMessage messageWithAudioFiles = new OutboundVoiceMessage();
        messageWithAudioFiles.setExternalId(patientId);
        messageWithAudioFiles.setVoiceMessageType(messageType);

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        List<String> sequenceOfFilesToPlay = Arrays.asList("file1.wav", "file2.wav", "file3.wav");
        parameters.put("audioFiles", sequenceOfFilesToPlay);
        messageWithAudioFiles.setParameters(parameters);
        messageWithAudioFiles.setStatus(OutboundVoiceMessageStatus.PENDING);
        messageWithAudioFiles.setExpirationDate(DateUtil.now().plusDays(10).toDate());

        outboundVoiceMessageDao.add(messageWithAudioFiles);

        List<OutboundVoiceMessage> messages = outboundVoiceMessageDao.getMessages(patientId, OutboundVoiceMessageStatus.PENDING);
        assertEquals(1, messages.size());
        OutboundVoiceMessage message = messages.get(0);
        assertTrue(message.getParameters().containsKey("audioFiles"));
        assertEquals(message.getParameters().get("audioFiles"), sequenceOfFilesToPlay);
    }

    @Test
    public void getAllMessagesGivenStatusAndExternalId() {
        List<OutboundVoiceMessage> messages = outboundVoiceMessageDao.getMessages(externalId1, OutboundVoiceMessageStatus.SAVED);
        assertNotNull(messages);
        assertEquals(1, messages.size());
        for (OutboundVoiceMessage m : messages) {
            assertEquals(OutboundVoiceMessageStatus.SAVED, m.getStatus());
            assertTrue(m.getExpirationDate().after(new Date()));
        }
    }

    @Test
    public void getAllMessagesForGivenExternalIdStatusAndMessageType() {
        int pendingMessagesCount = outboundVoiceMessageDao.getMessagesCount(externalId1, OutboundVoiceMessageStatus.PENDING, type1.getVoiceMessageTypeName());
        assertThat(pendingMessagesCount, is(1));

        pendingMessagesCount = outboundVoiceMessageDao.getMessagesCount(externalId2, OutboundVoiceMessageStatus.PENDING, type2.getVoiceMessageTypeName());
        assertThat(pendingMessagesCount, is(2));
    }
}
