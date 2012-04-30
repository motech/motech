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

/**
     * @author yyonkov
     */
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(locations = {"/applicationOutboxAPI.xml"})
    public class OutboundVoiceMessageDaoIT {
        @Autowired
        private AllOutboundVoiceMessages outboundVoiceMessageDao;

        private String externalId1 = "0001";
        private String externalId2 = "0002";
        private VoiceMessageType type1;

        @Before
        public void setUp() {
            outboundVoiceMessageDao.removeAll();
            type1 = new VoiceMessageType();
            type1.setVoiceMessageTypeName("Play something");
            type1.setTemplateName("appointmentReminder");

            VoiceMessageType type2 = new VoiceMessageType();
            type2.setVoiceMessageTypeName("Text something");
            type2.setTemplateName("appointmentReminder");

            // create messages
            DateTime now = DateUtil.now();
            for (int i = 0; i < 20; i++) {
                OutboundVoiceMessage msg = new OutboundVoiceMessage();
                msg.setExternalId(i < 10 ? externalId1 : externalId2);

                if((i&1)>0) {
                    msg.setStatus(OutboundVoiceMessageStatus.PENDING);
                    msg.setVoiceMessageType(type1);

                } else {
                    msg.setStatus(OutboundVoiceMessageStatus.SAVED);
                    msg.setVoiceMessageType(type2);
                }

                msg.setCreationTime(now.plusDays(2).toDate());
                msg.setExpirationDate(now.plusDays(1 - 2 * (i & 2)).toDate());
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
            assertEquals(3, messages.size());
            for (OutboundVoiceMessage m : messages) {
                assertEquals(OutboundVoiceMessageStatus.SAVED, m.getStatus());
                assertTrue(m.getExpirationDate().after(new Date()));
            }
        }


        @Test
        public void getAllMessagesForGivenExternalIdStatusAndMessageType() {

            int pendingMessagesCount = outboundVoiceMessageDao.getMessagesCount(externalId1, OutboundVoiceMessageStatus.PENDING, type1.getVoiceMessageTypeName());
            assertThat(pendingMessagesCount, is(3));

        }

    }
