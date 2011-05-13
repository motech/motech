package org.motechproject.server.outbox.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.outbox.api.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;


/**
 * Voice Outbox Service Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class VoiceOutboxServiceTest {


    private static final int MAX_MESSAGES_PENDING = 15;

	@InjectMocks
    VoiceOutboxServiceImpl voiceOutboxService = new VoiceOutboxServiceImpl();

    @Mock
    OutboundVoiceMessageDao outboundVoiceMessageDaoMock;

    @Mock
    EventRelay eventRelay;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        voiceOutboxService.setNumDayskeepSavedMessages(10);
        voiceOutboxService.setMaxNumberOfPendingMessages(MAX_MESSAGES_PENDING);
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


    @Test
    public void testGetNextSavedMessage() {

        String partyId = "pid";

        OutboundVoiceMessage  outboundVoiceMessage1 = new OutboundVoiceMessage();
        OutboundVoiceMessage  outboundVoiceMessage2 = new OutboundVoiceMessage();


        List<OutboundVoiceMessage> pendingVoiceMessages = new ArrayList<OutboundVoiceMessage>();
        pendingVoiceMessages.add(outboundVoiceMessage1);
        pendingVoiceMessages.add(outboundVoiceMessage2);

        when(outboundVoiceMessageDaoMock.getSavedMessages(partyId)).thenReturn(pendingVoiceMessages);

        assertEquals(outboundVoiceMessage1, voiceOutboxService.getNextSavedMessage(partyId));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextSavedMessageNullPartyId() {

        voiceOutboxService.getNextSavedMessage(null);

        verify(outboundVoiceMessageDaoMock, times(0)).getSavedMessages(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextSavedMessageEmptyPartyId() {

        voiceOutboxService.getNextSavedMessage("");

        verify(outboundVoiceMessageDaoMock, times(0)).getSavedMessages(anyString());

    }

    @Test
    public void testGetNextSavedMessageNoMessages() {

        String partyId = "pid";

        when(outboundVoiceMessageDaoMock.getSavedMessages(partyId)).thenReturn(new ArrayList<OutboundVoiceMessage>());

        assertNull(voiceOutboxService.getNextSavedMessage(partyId));
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

        when(outboundVoiceMessageDaoMock.getPendingMessagesCount(partyId)).thenReturn(2);

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

    @Test
    public void testSaveMessage() {

         String messageId = "msgId";

        OutboundVoiceMessage message = new OutboundVoiceMessage();

        when(outboundVoiceMessageDaoMock.get(messageId)).thenReturn(message);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, voiceOutboxService.getNumDayskeepSavedMessages());

        voiceOutboxService.saveMessage(messageId);
        verify(outboundVoiceMessageDaoMock).update(message);
        assertEquals(OutboundVoiceMessageStatus.SAVED, message.getStatus());
        System.out.println(calendar.getTime().getTime() - message.getExpirationDate().getTime());
        assertTrue(message.getExpirationDate().getTime() - calendar.getTime().getTime() < 1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveMessageNoMessageId() {

        voiceOutboxService.saveMessage(null);
        verify(outboundVoiceMessageDaoMock, times(0)).update(Matchers.<OutboundVoiceMessage>any());
    }

    @Test
    public void testMaxPendingMessagesReached() {
    	String partyId = "001";
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setPartyId(partyId);
        
        when(outboundVoiceMessageDaoMock.getPendingMessagesCount(partyId)).thenReturn(MAX_MESSAGES_PENDING);
        voiceOutboxService.addMessage(outboundVoiceMessage);
        
        verify(outboundVoiceMessageDaoMock).add(outboundVoiceMessage);

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.<MotechEvent>forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(argument.capture());
        assertEquals(argument.getValue().getSubject(), EventKeys.OUTBOX_MAX_PENDING_MESSAGES_EVENT_SUBJECT);
        assertEquals(EventKeys.getPartyID(argument.getValue()), partyId);
    }
    
    @Test
    public void testMaxPendingMessagesMoreAndLess() {
    	String partyId = "001";
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setPartyId(partyId);
        
        when(outboundVoiceMessageDaoMock.getPendingMessagesCount(partyId)).thenReturn(MAX_MESSAGES_PENDING-1);
        voiceOutboxService.addMessage(outboundVoiceMessage);
        
        verify(outboundVoiceMessageDaoMock).add(outboundVoiceMessage);
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
    }
}