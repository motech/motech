package org.motechproject.outbox.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.outbox.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.model.OutboundVoiceMessage;
import org.motechproject.outbox.model.OutboundVoiceMessageStatus;
import org.motechproject.server.outbox.service.VoiceOutboxService;
import org.motechproject.server.outbox.service.VoiceOutboxServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;


/**
 * Voice Outbox Service Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class VoiceOutboxServiceTest {


    @InjectMocks
    VoiceOutboxService voiceOutboxService = new VoiceOutboxServiceImpl();

    @Mock
    OutboundVoiceMessageDao outboundVoiceMessageDaoMock;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
     public void testAddMessage() {

        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();

        voiceOutboxService.addMessage(outboundVoiceMessage);

        verify(outboundVoiceMessageDaoMock).add(outboundVoiceMessage);
     }

    @Test(expected = IllegalArgumentException.class)
     public void testAddMessageNullMessage() {

        voiceOutboxService.addMessage(null);

        verify(outboundVoiceMessageDaoMock, times(0)).add(null);
     }

    @Test
    public void testGetNextPendingMessage() {

        String partyId = "pid";

        OutboundVoiceMessage  outboundVoiceMessage1 = new OutboundVoiceMessage();
        OutboundVoiceMessage  outboundVoiceMessage2 = new OutboundVoiceMessage();


        List<OutboundVoiceMessage> pendingVoiceMessages = new ArrayList<OutboundVoiceMessage>();
        pendingVoiceMessages.add(outboundVoiceMessage1);
        pendingVoiceMessages.add(outboundVoiceMessage2);

        when(outboundVoiceMessageDaoMock.getPendingMessages(partyId)).thenReturn(pendingVoiceMessages);

        assertEquals(outboundVoiceMessage1, voiceOutboxService.getNextPendingMessage(partyId));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextPendingMessageNullPartyId() {

        voiceOutboxService.getNextPendingMessage(null);

        verify(outboundVoiceMessageDaoMock, times(0)).getPendingMessages(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextPendingMessageEmptyPartyId() {

        voiceOutboxService.getNextPendingMessage("");

        verify(outboundVoiceMessageDaoMock, times(0)).getPendingMessages(anyString());

    }

    @Test
    public void testGetNextPendingMessageNoMessages() {

        String partyId = "pid";

        when(outboundVoiceMessageDaoMock.getPendingMessages(partyId)).thenReturn(new ArrayList<OutboundVoiceMessage>());

        assertNull(voiceOutboxService.getNextPendingMessage(partyId));
    }

    @Test
    public void testGetMessageById() {

        String messageId = "msgId";
        OutboundVoiceMessage message = new OutboundVoiceMessage();

        when(outboundVoiceMessageDaoMock.get(messageId)).thenReturn(message);

        assertEquals(message, voiceOutboxService.getMessageById(messageId));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMessageByIdNullId() {

        voiceOutboxService.getMessageById(null);

        verify(outboundVoiceMessageDaoMock, times(0)).get(Matchers.<String>anyObject());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetMessageByIdEmptyId() {

        voiceOutboxService.getMessageById("");

        verify(outboundVoiceMessageDaoMock, times(0)).get(Matchers.<String>anyObject());
    }

    @Test
    public void testRemoveMessage() {

        String messageId = "msgId";
        OutboundVoiceMessage message = new OutboundVoiceMessage();

        when(outboundVoiceMessageDaoMock.get(messageId)).thenReturn(message);

        voiceOutboxService.removeMessage(messageId);

        verify(outboundVoiceMessageDaoMock, times(1)).remove(message);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveMessageNullMessageId() {

        voiceOutboxService.removeMessage(null);

        verify(outboundVoiceMessageDaoMock, times(0)).getPendingMessages(anyString());
    }

    @Test(expected = IllegalArgumentException.class)
     public void testRemoveMessageEmptyMessageId() {

         voiceOutboxService.removeMessage("");

        verify(outboundVoiceMessageDaoMock, times(0)).getPendingMessages(anyString());
    }

    @Test
     public void testRemoveMessageMessageNotExist() {
        //TODO - implement
    }

    @Test
    public void testSetMessageStatus() {

        String messageId = "msgId";

        when(outboundVoiceMessageDaoMock.get(messageId)).thenReturn(new OutboundVoiceMessage());

        voiceOutboxService.setMessageStatus(messageId, OutboundVoiceMessageStatus.PLAYED);
        verify(outboundVoiceMessageDaoMock).update(Matchers.<OutboundVoiceMessage>anyObject());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMessageStatusNullMessageId() {

        voiceOutboxService.setMessageStatus(null, OutboundVoiceMessageStatus.PLAYED);
        verify(outboundVoiceMessageDaoMock, times(0)).update(Matchers.<OutboundVoiceMessage>anyObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMessageStatusEmptyMessageId() {

        voiceOutboxService.setMessageStatus("", OutboundVoiceMessageStatus.PLAYED);
        verify(outboundVoiceMessageDaoMock, times(0)).update(Matchers.<OutboundVoiceMessage>anyObject());
    }

    @Test
    public void testGetNumberPendingMessages() {

         String partyId = "pid";

        OutboundVoiceMessage  outboundVoiceMessage1 = new OutboundVoiceMessage();
        OutboundVoiceMessage  outboundVoiceMessage2 = new OutboundVoiceMessage();


         List<OutboundVoiceMessage> pendingVoiceMessages = new ArrayList<OutboundVoiceMessage>();
        pendingVoiceMessages.add(outboundVoiceMessage1);
        pendingVoiceMessages.add(outboundVoiceMessage2);

        when(outboundVoiceMessageDaoMock.getPendingMessages(partyId)).thenReturn(pendingVoiceMessages);

        assertEquals(2, voiceOutboxService.getNumberPendingMessages(partyId));
    }

     @Test
    public void testGetNumberPendingMessagesNoMessages() {

         String partyId = "pid";

        when(outboundVoiceMessageDaoMock.getPendingMessages(partyId)).thenReturn(new ArrayList<OutboundVoiceMessage>());

        assertEquals(0, voiceOutboxService.getNumberPendingMessages(partyId));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberPendingMessagesNullPartyId() {

        voiceOutboxService.getNumberPendingMessages(null);

        verify(outboundVoiceMessageDaoMock, times(0)).getPendingMessages(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberPendingMessagesEmptyPartyId() {

        voiceOutboxService.getNumberPendingMessages("");

        verify(outboundVoiceMessageDaoMock, times(0)).getPendingMessages(anyString());

    }

}