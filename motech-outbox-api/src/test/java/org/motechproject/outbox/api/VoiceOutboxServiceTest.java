package org.motechproject.outbox.api;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.repository.AllOutboundVoiceMessages;
import org.motechproject.outbox.api.service.impl.VoiceOutboxServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.*;
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
    AllOutboundVoiceMessages allOutboundVoiceMessages;
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
        String externalId = "pid";

        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setExternalId(externalId);

        voiceOutboxService.addMessage(outboundVoiceMessage);

        when(allOutboundVoiceMessages.get(null)).thenThrow(new NullPointerException("Argument cannot be null"));
        verify(allOutboundVoiceMessages).add(outboundVoiceMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMessageNullMessage() {

        voiceOutboxService.addMessage(null);

        verify(allOutboundVoiceMessages, times(0)).add(null);
    }

    @Test
    public void testGetNextPendingMessage() {

        String externalId = "pid";

        OutboundVoiceMessage outboundVoiceMessage1 = new OutboundVoiceMessage();
        OutboundVoiceMessage outboundVoiceMessage2 = new OutboundVoiceMessage();


        List<OutboundVoiceMessage> pendingVoiceMessages = new ArrayList<OutboundVoiceMessage>();
        pendingVoiceMessages.add(outboundVoiceMessage1);
        pendingVoiceMessages.add(outboundVoiceMessage2);

        when(allOutboundVoiceMessages.getPendingMessages(externalId)).thenReturn(pendingVoiceMessages);

        assertEquals(outboundVoiceMessage1, voiceOutboxService.getNextPendingMessage(externalId));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextPendingMessageNullExternalId() {

        voiceOutboxService.getNextPendingMessage(null);

        verify(allOutboundVoiceMessages, times(0)).getPendingMessages(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextPendingMessageEmptyExternalId() {

        voiceOutboxService.getNextPendingMessage("");

        verify(allOutboundVoiceMessages, times(0)).getPendingMessages(anyString());

    }

    @Test
    public void testGetNextPendingMessageNoMessages() {

        String externalId = "pid";

        when(allOutboundVoiceMessages.getPendingMessages(externalId)).thenReturn(new ArrayList<OutboundVoiceMessage>());

        assertNull(voiceOutboxService.getNextPendingMessage(externalId));
    }

    @Test
    public void testGetMessageById() {

        String messageId = "msgId";
        OutboundVoiceMessage message = new OutboundVoiceMessage();

        when(allOutboundVoiceMessages.get(messageId)).thenReturn(message);

        assertEquals(message, voiceOutboxService.getMessageById(messageId));

    }


    @Test
    public void testGetNextSavedMessage() {

        String externalId = "pid";

        OutboundVoiceMessage outboundVoiceMessage1 = new OutboundVoiceMessage();
        OutboundVoiceMessage outboundVoiceMessage2 = new OutboundVoiceMessage();


        List<OutboundVoiceMessage> pendingVoiceMessages = new ArrayList<OutboundVoiceMessage>();
        pendingVoiceMessages.add(outboundVoiceMessage1);
        pendingVoiceMessages.add(outboundVoiceMessage2);

        when(allOutboundVoiceMessages.getSavedMessages(externalId)).thenReturn(pendingVoiceMessages);

        assertEquals(outboundVoiceMessage1, voiceOutboxService.getNextSavedMessage(externalId));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextSavedMessageNullExternalId() {

        voiceOutboxService.getNextSavedMessage(null);

        verify(allOutboundVoiceMessages, times(0)).getSavedMessages(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextSavedMessageEmptyExternalId() {

        voiceOutboxService.getNextSavedMessage("");

        verify(allOutboundVoiceMessages, times(0)).getSavedMessages(anyString());

    }

    @Test
    public void testGetNextSavedMessageNoMessages() {

        String externalId = "pid";

        when(allOutboundVoiceMessages.getSavedMessages(externalId)).thenReturn(new ArrayList<OutboundVoiceMessage>());

        assertNull(voiceOutboxService.getNextSavedMessage(externalId));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetMessageByIdNullId() {

        voiceOutboxService.getMessageById(null);

        verify(allOutboundVoiceMessages, times(0)).get(Matchers.<String>anyObject());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetMessageByIdEmptyId() {

        voiceOutboxService.getMessageById("");

        verify(allOutboundVoiceMessages, times(0)).get(Matchers.<String>anyObject());
    }

    @Test
    public void testRemoveMessage() {

        String messageId = "msgId";
        OutboundVoiceMessage message = new OutboundVoiceMessage();

        when(allOutboundVoiceMessages.get(messageId)).thenReturn(message);

        voiceOutboxService.removeMessage(messageId);

        verify(allOutboundVoiceMessages, times(1)).safeRemove(message);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveMessageNullMessageId() {

        voiceOutboxService.removeMessage(null);

        verify(allOutboundVoiceMessages, times(0)).getPendingMessages(anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveMessageEmptyMessageId() {

        voiceOutboxService.removeMessage("");

        verify(allOutboundVoiceMessages, times(0)).getPendingMessages(anyString());
    }

    @Test
    public void testRemoveMessageMessageNotExist() {
        //TODO - implement
    }

    @Test
    public void testSetMessageStatus() {

        String messageId = "msgId";

        when(allOutboundVoiceMessages.get(messageId)).thenReturn(new OutboundVoiceMessage());

        voiceOutboxService.setMessageStatus(messageId, OutboundVoiceMessageStatus.PLAYED);
        verify(allOutboundVoiceMessages).update(Matchers.<OutboundVoiceMessage>anyObject());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMessageStatusNullMessageId() {

        voiceOutboxService.setMessageStatus(null, OutboundVoiceMessageStatus.PLAYED);
        verify(allOutboundVoiceMessages, times(0)).update(Matchers.<OutboundVoiceMessage>anyObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMessageStatusEmptyMessageId() {

        voiceOutboxService.setMessageStatus("", OutboundVoiceMessageStatus.PLAYED);
        verify(allOutboundVoiceMessages, times(0)).update(Matchers.<OutboundVoiceMessage>anyObject());
    }

    @Test
    public void testGetNumberPendingMessages() {

        String externalId = "pid";

        when(allOutboundVoiceMessages.getPendingMessagesCount(externalId)).thenReturn(2);

        assertEquals(2, voiceOutboxService.getNumberPendingMessages(externalId));
    }

    @Test
    public void testGetNumberPendingMessagesNoMessages() {

        String externalId = "pid";

        when(allOutboundVoiceMessages.getPendingMessages(externalId)).thenReturn(new ArrayList<OutboundVoiceMessage>());

        assertEquals(0, voiceOutboxService.getNumberPendingMessages(externalId));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberPendingMessagesNullExternalId() {

        voiceOutboxService.getNumberPendingMessages(null);

        verify(allOutboundVoiceMessages, times(0)).getPendingMessages(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberPendingMessagesEmptyExternalId() {

        voiceOutboxService.getNumberPendingMessages("");

        verify(allOutboundVoiceMessages, times(0)).getPendingMessages(anyString());

    }

    @Test
    public void testSaveMessage() {

        String messageId = "msgId";

        OutboundVoiceMessage message = new OutboundVoiceMessage();

        when(allOutboundVoiceMessages.get(messageId)).thenReturn(message);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, voiceOutboxService.getNumDayskeepSavedMessages());

        voiceOutboxService.saveMessage(messageId);
        verify(allOutboundVoiceMessages).update(message);
        assertEquals(OutboundVoiceMessageStatus.SAVED, message.getStatus());
        System.out.println(calendar.getTime().getTime() - message.getExpirationDate().getTime());
        assertTrue(message.getExpirationDate().getTime() - calendar.getTime().getTime() < 1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveMessageNoMessageId() {

        voiceOutboxService.saveMessage(null);
        verify(allOutboundVoiceMessages, times(0)).update(Matchers.<OutboundVoiceMessage>any());
    }

    @Test
    public void testMaxPendingMessagesReached() {
        String externalId = "001";
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setExternalId(externalId);

        when(allOutboundVoiceMessages.getPendingMessagesCount(externalId)).thenReturn(MAX_MESSAGES_PENDING);
        voiceOutboxService.addMessage(outboundVoiceMessage);

        verify(allOutboundVoiceMessages).add(outboundVoiceMessage);

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(argument.capture());
        assertEquals(argument.getValue().getSubject(), EventKeys.OUTBOX_MAX_PENDING_MESSAGES_EVENT_SUBJECT);
        assertEquals(EventKeys.getExternalID(argument.getValue()), externalId);
    }

    @Test
    public void testMaxPendingMessagesMoreAndLess() {
        String externalId = "001";
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setExternalId(externalId);

        // LESS
        when(allOutboundVoiceMessages.getPendingMessagesCount(externalId)).thenReturn(MAX_MESSAGES_PENDING - 1);
        voiceOutboxService.addMessage(outboundVoiceMessage);

        // MORE
        when(allOutboundVoiceMessages.getPendingMessagesCount(externalId)).thenReturn(MAX_MESSAGES_PENDING + 1);
        voiceOutboxService.addMessage(outboundVoiceMessage);

        verify(allOutboundVoiceMessages, times(2)).add(outboundVoiceMessage);
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void getTheFirstMessage() {
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        String externalId = "123";
        when(allOutboundVoiceMessages.getPendingMessages(externalId)).thenReturn(Arrays.asList(outboundVoiceMessage));
        OutboundVoiceMessage nextMessage = voiceOutboxService.nextMessage(null, externalId);
        assertEquals(outboundVoiceMessage, nextMessage);
    }

    @Test
    public void markMessageAsRead() {
        String externalId = "123";
        OutboundVoiceMessage currentMessage = mock(OutboundVoiceMessage.class);
        String currentMessageId = "1";

        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setId("2");

        when(allOutboundVoiceMessages.get(currentMessageId)).thenReturn(currentMessage);
        when(allOutboundVoiceMessages.getPendingMessages(externalId)).thenReturn(Arrays.asList(outboundVoiceMessage));

        OutboundVoiceMessage nextMessage = voiceOutboxService.nextMessage(currentMessageId, externalId);

        InOrder inOrder = inOrder(currentMessage, allOutboundVoiceMessages);
        assertEquals(outboundVoiceMessage, nextMessage);
        inOrder.verify(currentMessage).setStatus(OutboundVoiceMessageStatus.PLAYED);
        inOrder.verify(allOutboundVoiceMessages).update(currentMessage);
        inOrder.verify(allOutboundVoiceMessages).getPendingMessages(externalId);
    }
}